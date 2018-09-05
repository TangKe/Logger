package ke.tang.logger.util;

import android.os.Handler;
import android.os.Looper;

/**
 * @author tangke
 */

public class HandlerUtils {
    public static Handler sMainHandler = new Handler(Looper.getMainLooper());
}
