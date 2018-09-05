package ke.tang.logger;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Log数据类
 * 如果调用{@link Logger#addLog(Log)}方法使用，无须调用{@link #recycle()}方法，其它时候使用建议调用{@link #recycle()}
 *
 * @author tangke
 */

public class Log implements Parcelable {
    public static final int MAX_RECYCLED = 10;
    private String mContent;

    private static final Object sRecycleLock = new Object();
    private static Log sRecycleLogTop;
    private static int sRecycleUsed;

    private Log mNext;

    private Log() {
    }

    public static Log obtain() {
        final Log log;
        synchronized (sRecycleLock) {
            log = sRecycleLogTop;
            if (log == null) {
                return new Log();
            }
            sRecycleLogTop = log.mNext;
            sRecycleUsed -= 1;
        }
        log.mNext = null;
        log.reset();
        return log;
    }

    public static Log obtain(String content) {
        final Log log = obtain();
        log.mContent = content;
        return log;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    private void reset() {
        mContent = null;
    }

    public void recycle() {
        synchronized (sRecycleLock) {
            if (sRecycleUsed < MAX_RECYCLED) {
                sRecycleUsed++;
                mNext = sRecycleLogTop;
                sRecycleLogTop = this;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mContent);
        dest.writeParcelable(this.mNext, flags);
    }

    protected Log(Parcel in) {
        this.mContent = in.readString();
        this.mNext = in.readParcelable(Log.class.getClassLoader());
    }

    public static final Creator<Log> CREATOR = new Creator<Log>() {
        @Override
        public Log createFromParcel(Parcel source) {
            return new Log(source);
        }

        @Override
        public Log[] newArray(int size) {
            return new Log[size];
        }
    };
}
