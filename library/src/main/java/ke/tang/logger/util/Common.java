package ke.tang.logger.util;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ke.tang.logger.R;

/**
 * @author tangke
 */

public class Common {
    private volatile static File sCurrentContinuedLogFile;

    public static File getCurrentContinuedLogFile() {
        return sCurrentContinuedLogFile;
    }

    public static void setCurrentContinuedLogFile(File currentContinuedLogFile) {
        sCurrentContinuedLogFile = currentContinuedLogFile;
    }

    public static File getTraceLogFileDirectory(Context context) {
        final File trace = new File(getLoggerDirectory(context), "trace");
        trace.mkdirs();
        return trace;
    }

    public static File getContinuedLogFileDirectory(Context context) {
        final File log = new File(getLoggerDirectory(context), "continued");
        log.mkdirs();
        return log;
    }

    public static File getLoggerDirectory(Context context) {
        return context.getExternalFilesDir("logger");
    }

    public static String getLoggerProviderAuthorities(Context context) {
        return context.getPackageName().concat(".logger");
    }

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();
    private final static Date TEMP_DATE = new Date();

    public static String buildTraceFileName(Context context, long startDate, long endDate) {
        TEMP_DATE.setTime(startDate);
        DATE_FORMAT.applyPattern(context.getString(R.string.logger_log_file_name_pattern));
        String fromDate = DATE_FORMAT.format(Common.TEMP_DATE);
        TEMP_DATE.setTime(endDate);
        return String.format("%s-%s.log", fromDate, DATE_FORMAT.format(TEMP_DATE));
    }

    public static String buildContinuedLogFileName(Context context, long time) {
        TEMP_DATE.setTime(time);
        DATE_FORMAT.applyPattern(context.getString(R.string.logger_continued_log_file_name_pattern));
        return String.format("%s.log", DATE_FORMAT.format(TEMP_DATE));
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
