package ke.tang.logger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import ke.tang.contextinjector.annotations.InjectContext;
import ke.tang.logger.ui.view.LogShortcutViewHolder;
import ke.tang.logger.util.Intents;

/**
 * 用于监视Log服务的状态，带图形界面
 *
 * @author tangke
 */

class LogMonitor {
    static Context sApplicationContext;
    private final static String PREFERENCE_LOG_MONITOR = "LogMonitorPreferences";
    private final static String KEY_COORDINATOR_X = "CoordinatorX";
    private final static String KEY_COORDINATOR_Y = "CoordinatorY";
    private static LogMonitorLifecycleCallbacks sLogMonitorLifecycleCallbacks = new LogMonitorLifecycleCallbacks();

    private static WeakHashMap<Activity, LogShortcutViewHolder> sViewWeakHashMap = new WeakHashMap<>();

    private static WeakReference<Activity> sCurrentResumedActivity;

    private static Point sShortcutCoordinator = new Point();
    private static SharedPreferences sSettings;
    private static boolean sInEnable;

    private static int sServiceStatus;

    private static ILoggerServiceCallback.Stub sCallback = new ILoggerServiceCallback.Stub() {
        @Override
        public void onStatusChanged(int newStatus) throws RemoteException {
            sServiceStatus = newStatus;
            updateShortStatus();
        }
    };

    private static View.OnClickListener sOnShortcutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveShortcutStatus();
            if (Logger.getStatus() != LoggerService.STATUS_TRACING) {
                Logger.startTrace();
            } else {
                Uri traceFile = Logger.stopTrace();
                if (null != traceFile) {
                    sApplicationContext.startActivity(Intents.send(sApplicationContext, traceFile).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    Toast.makeText(sApplicationContext, sApplicationContext.getString(R.string.logger_log_record_not_create), Toast.LENGTH_LONG).show();
                }
            }
            updateShortStatus();
        }
    };

    @InjectContext
    static void onContextReady(Context context) {
        sApplicationContext = context;
        sSettings = context.getSharedPreferences(PREFERENCE_LOG_MONITOR, Context.MODE_PRIVATE);
        restoreCoordinator();
        registerCallback();
    }

    static void registerCallback() {
        if (sApplicationContext instanceof Application) {
            ((Application) sApplicationContext).registerActivityLifecycleCallbacks(sLogMonitorLifecycleCallbacks);
        }
    }

    private static void restoreCoordinator() {
        if (sSettings.contains(KEY_COORDINATOR_X)) {
            sShortcutCoordinator.set(sSettings.getInt(KEY_COORDINATOR_X, 0), sSettings.getInt(KEY_COORDINATOR_Y, 0));
        }
    }

    private static void persistCoordinator() {
        sSettings.edit().putInt(KEY_COORDINATOR_X, sShortcutCoordinator.x).putInt(KEY_COORDINATOR_Y, sShortcutCoordinator.y).commit();
    }

    public static void enable() {
        sInEnable = true;
        sServiceStatus = Logger.getStatus();
        Logger.registerCallback(ILoggerServiceCallback.Stub.asInterface(sCallback));
        if (null != sCurrentResumedActivity && null != sCurrentResumedActivity.get()) {
            attachOverlay(sCurrentResumedActivity.get());
        }
        saveShortcutStatus();
        updateShortStatus();
    }

    public static void disable() {
        sInEnable = false;
        Logger.unregisterCallback(ILoggerServiceCallback.Stub.asInterface(sCallback));
        saveShortcutStatus();
        updateShortStatus();
    }

    public static void toggle() {
        if (sInEnable) {
            disable();
        } else {
            enable();
        }
    }

    private static void updateShortStatus() {
        for (LogShortcutViewHolder holder : sViewWeakHashMap.values()) {
            holder.setStatus(sServiceStatus);
            holder.updateShortcutCoordinator(sShortcutCoordinator);
            if (sInEnable) {
                holder.show();
            } else {
                holder.hide();
            }
        }
    }

    private static void attachOverlay(final Activity activity) {
        LogShortcutViewHolder holder = sViewWeakHashMap.get(activity);
        if (null == holder) {
            holder = new LogShortcutViewHolder(activity);
            holder.setStatus(Logger.getStatus());
            holder.setOnClickListener(sOnShortcutClickListener);
            sViewWeakHashMap.put(activity, holder);
        }
    }

    private static void saveShortcutStatus() {
        if (null != sCurrentResumedActivity && null != sCurrentResumedActivity.get()) {
            LogShortcutViewHolder holder = sViewWeakHashMap.get(sCurrentResumedActivity.get());
            if (null != holder) {
                holder.getShortcutCoordinator(sShortcutCoordinator);
                persistCoordinator();
            }
        }
    }

    private static class LogMonitorLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(final Activity activity) {
            sCurrentResumedActivity = new WeakReference<>(activity);
            attachOverlay(activity);
            updateShortStatus();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            saveShortcutStatus();
            updateShortStatus();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sViewWeakHashMap.remove(activity);
        }

    }
}
