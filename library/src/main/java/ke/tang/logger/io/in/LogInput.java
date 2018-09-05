package ke.tang.logger.io.in;

import ke.tang.logger.Log;
import ke.tang.logger.LogIOException;
import ke.tang.logger.io.LogIO;

/**
 * @author tangke
 */

public interface LogInput extends LogIO {
    /**
     * 读取一行
     *
     * @return
     */
    Log read() throws LogIOException;
}
