package ke.tang.logger;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author tangke
 */

public class LogQueue extends ArrayBlockingQueue<Log> {
    public LogQueue() {
        super(128);
    }
}
