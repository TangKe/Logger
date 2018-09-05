package ke.tang.logger.executor;

import java.util.concurrent.CopyOnWriteArrayList;

import ke.tang.logger.Log;
import ke.tang.logger.LogIOException;
import ke.tang.logger.LogQueue;
import ke.tang.logger.io.out.LogOutput;
import ke.tang.logger.util.RetryCounter;

/**
 * 日志写入器
 *
 * @author tangke
 */

public class LogWriteThreadExecutor extends Thread implements LogWriteExecutor {
    private LogQueue mLogQueue;

    private RetryCounter mRetryCounter = new RetryCounter();

    /**
     * 不用上锁，读取多，写入少
     */
    private CopyOnWriteArrayList<LogOutput> mOutputs = new CopyOnWriteArrayList<>();

    public LogWriteThreadExecutor(LogQueue logQueue) {
        mLogQueue = logQueue;
        setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                final Log log = mLogQueue.take();
                for (LogOutput output : mOutputs) {
                    writeLog(output, log);
                }
                log.recycle();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void writeLog(LogOutput output, Log log) {
        try {
            output.write(log);
            mRetryCounter.reset();
        } catch (LogIOException e) {
            if (mRetryCounter.retry()) {
                output.connect();
                writeLog(output, log);
            }
        }
    }

    public void addLogOutput(LogOutput output) {
        mOutputs.add(output);
    }

    public void removeLogOutput(LogOutput output) {
        mOutputs.remove(output);
    }

    @Override
    public void shutdown() {
        interrupt();

        for (LogOutput output : mOutputs) {
            output.close();
        }
    }
}
