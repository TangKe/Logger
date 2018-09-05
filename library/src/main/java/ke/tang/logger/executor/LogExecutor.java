package ke.tang.logger.executor;

/**
 * 日志处理器定义
 *
 * @author tangke
 */

public interface LogExecutor {
    /**
     * 执行
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();
}
