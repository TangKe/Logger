package ke.tang.logger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import ke.tang.contextinjector.annotations.InjectContext;
import ke.tang.logger.ui.activity.LogManageActivity;

/**
 * {@link LoggerService}的接口访问类
 * 可提供两种类型的日志抓取
 * <ul>
 * <li>运行日志</li>
 * <li>时间段内运行日志</li>
 * </ul>
 * 可以调用{@link #addLog(Log)}添加自定义日志，添加的自定义日志都会记录到上述两种日志生成的日志文件中
 * 日志文件存储位置
 * <ul>
 * <li>/sdcard/Android/data/{packageName}/logger/continued/</li>
 * <li>/sdcard/Android/data/{packageName}/logger/trace/</li>
 * </ul>
 *
 * @author tangke
 */
public class Logger {
    static ILoggerService sLoggerServiceInterface;
    static Context sApplicationContext;

    @InjectContext
    static void onContextReady(Context context) {
        sApplicationContext = context;
        ensureConnection();
    }

    static ServiceConnection sLoggerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sLoggerServiceInterface = ILoggerService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sLoggerServiceInterface = null;
        }
    };

    /**
     * 展示一个管理日志界面
     */
    public static void manage() {
        if (null != sApplicationContext) {
            Intent intent = new Intent(sApplicationContext, LogManageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sApplicationContext.startActivity(intent);
        }
    }

    /**
     * 获取{@link LoggerService}运行状态
     *
     * @return
     * @see LoggerService#STATUS_IDLE
     * @see LoggerService#STATUS_RUNNING
     * @see LoggerService#STATUS_TRACING
     */
    public static int getStatus() {
        if (ensureConnection()) {
            try {
                return sLoggerServiceInterface.getStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 启动日志抓取，必须在抓取完成后调用{@link #stopTrace()}，完成后返回该时间段内产生的日志内容
     */
    public static void startTrace() {
        if (ensureConnection()) {
            try {
                sLoggerServiceInterface.startTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 完成日志抓取
     *
     * @return 文件路径{@link Uri}, 你可以通过{@link android.content.ContentResolver#openInputStream(Uri)}方法打开
     */
    public static Uri stopTrace() {
        if (ensureConnection()) {
            try {
                return sLoggerServiceInterface.stopTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 添加一个自定义日志
     *
     * @param log
     */
    public static void addLog(Log log) {
        if (ensureConnection()) {
            try {
                sLoggerServiceInterface.addLog(log);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册一个会回调，该回调支持跨进程
     *
     * @param callback
     */
    public static void registerCallback(ILoggerServiceCallback callback) {
        if (ensureConnection()) {
            try {
                sLoggerServiceInterface.registerCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消注册回调，该回调支持跨进程
     *
     * @param callback
     */
    public static void unregisterCallback(ILoggerServiceCallback callback) {
        if (ensureConnection()) {
            try {
                sLoggerServiceInterface.unregisterCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置自动清除日志文件大小，小于等于0则不清除，不会删除当前正在写入的日志文件
     *
     * @param size
     */
    public static void setAutoClearThresholdFileSize(long size) {
        if (ensureConnection()) {
            try {
                sLoggerServiceInterface.setAutoClearThresholdFileSize(size);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 确保日志服务是启动的并连接上的
     *
     * @return true是连接上的, false没连接上，并发起连接
     */
    static boolean ensureConnection() {
        if (!isConnected()) {
            connect();
            return false;
        }
        return true;
    }

    /**
     * 启动并连接日志服务
     */
    static void connect() {
        if (null != sApplicationContext) {
            try {
                sApplicationContext.startService(LoggerService.obtainIntent(sApplicationContext));
                sApplicationContext.bindService(LoggerService.obtainIntent(sApplicationContext), sLoggerServiceConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断服务是否已连接
     *
     * @return
     */
    static boolean isConnected() {
        return null != sLoggerServiceInterface;
    }

    /**
     * 在每个{@link android.app.Activity}中展示一个快捷按钮，便于启动日志抓取和关闭
     */
    public static void enableLogMonitor() {
        ensureConnection();
        LogMonitor.enable();
    }

    /**
     * 隐藏快捷按钮
     */
    public static void disableLogMonitor() {
        ensureConnection();
        LogMonitor.disable();
    }

    /**
     * 开关快捷按钮
     */
    public static void toggleLogMonitor() {
        ensureConnection();
        LogMonitor.toggle();
    }
}
