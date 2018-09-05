package ke.tang.logger.io.out;

import ke.tang.logger.Log;
import ke.tang.logger.LogIOException;
import ke.tang.logger.io.LogIO;

/**
 * @author tangke
 */

public interface LogOutput extends LogIO {
    /**
     * 返回是否成功
     *
     * @param log
     * @return
     */
    void write(Log log) throws LogIOException;


}
