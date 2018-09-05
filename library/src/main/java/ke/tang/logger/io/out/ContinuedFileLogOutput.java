package ke.tang.logger.io.out;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.File;
import java.util.Calendar;

import ke.tang.logger.util.Common;

/**
 * 持续记录
 *
 * @author tangke
 */

public class ContinuedFileLogOutput extends FileLogOutput {
    private int mYear;
    private int mMonth;
    private int mDay;

    private Context mContext;

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据日期变化，切换写入文件
            invalidateOutputIfNeeded();
        }
    };

    public ContinuedFileLogOutput(Context context) {
        super();
        mContext = context;
        final IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        context.registerReceiver(mTimeReceiver, filter);
        invalidateOutputIfNeeded();
    }

    private void invalidateOutputIfNeeded() {
        final Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) != mYear || calendar.get(Calendar.MONTH) != mMonth || calendar.get(Calendar.DAY_OF_MONTH) != mDay) {
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            setFile(new File(Common.getContinuedFileDirectory(mContext), Common.buildContinuedLogFileName(mContext, calendar.getTimeInMillis())));
        }
    }

    @Override
    public void close() {
        super.close();
        mContext.unregisterReceiver(mTimeReceiver);
    }
}
