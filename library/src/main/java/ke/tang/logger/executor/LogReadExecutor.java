package ke.tang.logger.executor;

import ke.tang.logger.io.in.LogInput;

/**
 * 日志读取器
 *
 * @author tangke
 */

public interface LogReadExecutor extends LogExecutor {
    void setLogInput(LogInput input);
}
