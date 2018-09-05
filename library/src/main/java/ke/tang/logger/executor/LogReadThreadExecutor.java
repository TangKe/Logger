package ke.tang.logger.executor;

import ke.tang.logger.Log;
import ke.tang.logger.LogIOException;
import ke.tang.logger.LogQueue;
import ke.tang.logger.io.in.LogInput;
import ke.tang.logger.util.RetryCounter;

/**
 * 日志读取器
 *
 * @author tangke
 */

public class LogReadThreadExecutor extends Thread implements LogReadExecutor {
    private LogQueue mLogQueue;

    private LogInput mInput;

    private RetryCounter mRetryCounter = new RetryCounter();

    public LogReadThreadExecutor(LogQueue logQueue) {
        mLogQueue = logQueue;
        setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            if (null != mInput) {
                final Log log = readLog();
                if (null != log) {
                    try {
                        mLogQueue.put(log);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Log readLog() {
        try {
            final Log log = mInput.read();
            mRetryCounter.reset();
            return log;
        } catch (LogIOException e) {
            if (mRetryCounter.retry()) {
                mInput.connect();
                return readLog();
            }
        }
        return null;
    }

    @Override
    public void shutdown() {
        interrupt();
        if (null != mInput) {
            mInput.close();
        }
    }

    @Override
    public void setLogInput(LogInput input) {
        mInput = input;
        try {
            notifyAll();
        } catch (Exception e) {

        }
    }
}
