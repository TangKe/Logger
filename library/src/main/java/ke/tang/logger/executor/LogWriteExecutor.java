package ke.tang.logger.executor;

import ke.tang.logger.io.out.LogOutput;

/**
 * 日志写入器
 *
 * @author tangke
 */

public interface LogWriteExecutor extends LogExecutor {
    /**
     * 增加数据
     *
     * @param output
     */
    void addLogOutput(LogOutput output);

    void removeLogOutput(LogOutput output);
}
