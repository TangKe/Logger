package ke.tang.logger.util;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ke.tang.logger.ui.adapter.holder.FileItem;

/**
 * @author tangke
 */
public class FileDeleteTask extends AsyncTask<FileItem, Integer, List<FileItem>> {
    private OnFileDeleteTaskListener mListener;

    public FileDeleteTask(OnFileDeleteTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected List<FileItem> doInBackground(FileItem... fileItems) {
        List<FileItem> result = new ArrayList<>();
        if (null != fileItems) {
            for (FileItem item : fileItems) {
                if (null != item.getFile()) {
                    if (item.getFile().delete()) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<FileItem> result) {
        super.onPostExecute(result);
        if (null != mListener) {
            mListener.onTaskComplete(result);
        }
    }

    public interface OnFileDeleteTaskListener {
        void onTaskComplete(List<FileItem> result);
    }
}
