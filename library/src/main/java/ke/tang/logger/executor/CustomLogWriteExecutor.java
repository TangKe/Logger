package ke.tang.logger.executor;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.List;

import ke.tang.logger.Log;
import ke.tang.logger.LogQueue;

/**
 * 自定义日志写入线程
 *
 * @author tangke
 */

public class CustomLogWriteExecutor extends HandlerThread implements LogExecutor {
    private LogQueue mLogQueue;
    private Handler mHandler;
    private List<Log> mPendingLogs = new ArrayList<>();

    public CustomLogWriteExecutor(LogQueue logQueue) {
        super("CustomLogWriteExecutor");
        mLogQueue = logQueue;
    }

    public void addLog(final Log log) {
        if (null != mHandler) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mLogQueue.put(log);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            synchronized (mPendingLogs) {
                mPendingLogs.add(log);
            }
        }
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(getLooper());

        synchronized (mPendingLogs) {
            for (Log log : mPendingLogs) {
                addLog(log);
            }
            mPendingLogs.clear();
        }
    }

    @Override
    public void shutdown() {
        quit();
    }
}
