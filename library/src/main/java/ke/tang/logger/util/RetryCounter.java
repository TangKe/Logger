package ke.tang.logger.util;

/**
 * 重试计数器
 *
 * @author tangke
 */

public class RetryCounter {
    private final static int DEFAULT_MAX_RETRY_COUNT = 10;

    private int mMaxRetryCount;

    private int mRetryCount;

    public RetryCounter() {
        this(DEFAULT_MAX_RETRY_COUNT);
    }

    /**
     * @param maxRetryCount 为负数表示无限重试，为0表示不重试
     */
    public RetryCounter(int maxRetryCount) {
        mMaxRetryCount = maxRetryCount;
    }

    /**
     * 执行重试
     *
     * @return true执行成功，false执行失败
     */
    public boolean retry() {
        if (mMaxRetryCount >= 0) {
            mRetryCount++;
            return mRetryCount <= mMaxRetryCount;
        } else {
            //无限重试
            return true;
        }
    }

    /**
     * 重置重试次数
     */
    public void reset() {
        mRetryCount = 0;
    }
}
