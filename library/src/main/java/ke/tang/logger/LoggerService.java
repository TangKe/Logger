package ke.tang.logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.content.FileProvider;

import java.io.File;

import ke.tang.logger.executor.CustomLogWriteExecutor;
import ke.tang.logger.executor.LogReadExecutor;
import ke.tang.logger.executor.LogReadThreadExecutor;
import ke.tang.logger.executor.LogWriteExecutor;
import ke.tang.logger.executor.LogWriteThreadExecutor;
import ke.tang.logger.io.in.LogCatInput;
import ke.tang.logger.io.out.ContinuedFileLogOutput;
import ke.tang.logger.io.out.FileLogOutput;
import ke.tang.logger.util.Common;
import ke.tang.logger.util.HandlerUtils;

/**
 * 日志服务
 * 负责日志的记录，抓取，保存
 *
 * @author tangke
 */

public class LoggerService extends Service {
    public static final int STATUS_IDLE = 1;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_TRACING = 3;

    public static Intent obtainIntent(Context context) {
        return new Intent(context, LoggerService.class);
    }

    private LogQueue mLogQueue = new LogQueue();

    private LogReadExecutor mRead;
    private LogWriteExecutor mWrite;
    private CustomLogWriteExecutor mCustom;
    private int mStatus = STATUS_IDLE;

    private RemoteCallbackList<ILoggerServiceCallback> mCallbacks = new RemoteCallbackList<>();
    private ILoggerService.Stub mStub = new ILoggerService.Stub() {

        private FileLogOutput mTraceFileOutput;
        private long mTraceStartTime;

        @Override
        public int getStatus() throws RemoteException {
            return mStatus;
        }

        @Override
        public boolean startTrace() throws RemoteException {
            if (mStatus != STATUS_TRACING) {
                setStatus(STATUS_TRACING);

                if (null != mTraceFileOutput) {
                    mTraceFileOutput.close();
                }

                mTraceStartTime = System.currentTimeMillis();
                mTraceFileOutput = new FileLogOutput(new File(getExternalCacheDir(), Common.randomUUID()));
                mWrite.addLogOutput(mTraceFileOutput);
                return true;
            }
            return false;
        }

        @Override
        public Uri stopTrace() throws RemoteException {
            if (mStatus == STATUS_TRACING) {
                setStatus(STATUS_RUNNING);
                if (null != mTraceFileOutput) {
                    mWrite.removeLogOutput(mTraceFileOutput);
                    mTraceFileOutput.close();
                    File outputFile = new File(Common.getTraceLogFileDirectory(LoggerService.this), Common.buildTraceFileName(LoggerService.this, mTraceStartTime, System.currentTimeMillis()));
                    mTraceFileOutput.getFile().renameTo(outputFile);
                    mTraceFileOutput = null;
                    return FileProvider.getUriForFile(LoggerService.this, Common.getLoggerProviderAuthorities(LoggerService.this), outputFile);
                }
            }
            return null;
        }

        @Override
        public void registerCallback(ILoggerServiceCallback callback) throws RemoteException {
            mCallbacks.register(callback);
        }

        @Override
        public void unregisterCallback(ILoggerServiceCallback callback) throws RemoteException {
            mCallbacks.unregister(callback);
        }

        @Override
        public void addLog(Log log) {
            mCustom.addLog(log);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRead = new LogReadThreadExecutor(mLogQueue);
        mWrite = new LogWriteThreadExecutor(mLogQueue);
        mCustom = new CustomLogWriteExecutor(mLogQueue);
        mWrite.addLogOutput(new ContinuedFileLogOutput(this));
        mRead.setLogInput(new LogCatInput());
        mRead.start();
        mWrite.start();
        mCustom.start();
        setStatus(STATUS_RUNNING);
    }

    public void setStatus(final int status) {
        int oldStatus = mStatus;
        mStatus = status;

        if (oldStatus != status) {
            HandlerUtils.sMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (int index = 0, count = mCallbacks.beginBroadcast(); index < count; index++) {
                        ILoggerServiceCallback callback = mCallbacks.getBroadcastItem(index);
                        try {
                            callback.onStatusChanged(status);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mCallbacks.finishBroadcast();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRead) {
            mRead.shutdown();
        }
        if (null != mWrite) {
            mWrite.shutdown();
        }
        if (null != mCustom) {
            mCustom.shutdown();
        }
    }
}
