package ke.tang.logger.executor;

import android.content.Context;
import android.text.format.DateUtils;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import ke.tang.logger.util.Common;
import ke.tang.logger.util.FileSortComparator;

/**
 * 自动清理
 *
 * @author tangke
 */
public class LogFileClearExecutor extends Thread implements LogExecutor {
    private volatile long mFileClearThresholdSize;
    private final static long SCAN_INTERVAL = 20 * DateUtils.SECOND_IN_MILLIS;
    private Context mContext;
    private File mContinuedLogFileDirectory;
    private FileSortComparator mFileSortComparator = new FileSortComparator();
    private ReentrantLock mLock = new ReentrantLock();
    private Condition mEnable = mLock.newCondition();

    public LogFileClearExecutor(Context context) {
        mContext = context;
        setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void shutdown() {
        interrupt();
    }

    private void ensureFile() {
        if (null == mContinuedLogFileDirectory) {
            mContinuedLogFileDirectory = Common.getContinuedLogFileDirectory(mContext);
        }
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            mLock.lock();
            while (mFileClearThresholdSize <= 0) {
                try {
                    mEnable.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mLock.unlock();
            doFileScan();
            try {
                Thread.sleep(SCAN_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doFileScan() {
        ensureFile();
        if (!mContinuedLogFileDirectory.exists() || !mContinuedLogFileDirectory.isDirectory()) {
            return;
        }
        File[] files = mContinuedLogFileDirectory.listFiles();
        if (null == files || files.length == 0) {
            //无日志文件
            return;
        }
        //按照日期升序排序
        Arrays.sort(files, mFileSortComparator);
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }

        if (totalSize > mFileClearThresholdSize && files.length > 1) {
            File currentContinuedLogFile = Common.getCurrentContinuedLogFile();
            for (File file : files) {
                if (!file.equals(currentContinuedLogFile)) {
                    totalSize -= file.length();
                    file.delete();
                    if (totalSize <= mFileClearThresholdSize) {
                        break;
                    }
                }
            }
        }
    }

    public long getFileClearThresholdSize() {
        return mFileClearThresholdSize;
    }

    /**
     * 设置清理阈值，日志文件大小超过该值会触发清理，小于或等于0则不清理，不会清理当前正在写的文件
     *
     * @param fileClearThresholdSize
     */
    public void setAutoClearThresholdFileSize(long fileClearThresholdSize) {
        mLock.lock();
        mFileClearThresholdSize = fileClearThresholdSize;
        if (fileClearThresholdSize > 1) {
            mEnable.signal();
        }
        mLock.unlock();
    }
}
