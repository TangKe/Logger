package ke.tang.logger;

import ke.tang.logger.ILoggerServiceCallback;
import android.net.Uri;
import ke.tang.logger.Log;

interface ILoggerService {
    //获取当前状态
    int getStatus();

    boolean startTrace();
    Uri stopTrace();

    void addLog(in Log log);

    void setAutoClearThresholdFileSize(long size);

    void registerCallback(ILoggerServiceCallback callback);
    void unregisterCallback(ILoggerServiceCallback callback);
}
