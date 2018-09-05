package ke.tang.logger.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ke.tang.logger.ui.adapter.holder.FileItem;
import ke.tang.logger.ui.adapter.holder.LogFileViewHolder;

/**
 * @author tangke
 */

public class LogFileListAdapter extends AbstractAdapter<FileItem, LogFileViewHolder> {
    private File mLogFileDirectory;

    public LogFileListAdapter(Context context, File logFileDirectory) {
        super(context);
        mLogFileDirectory = logFileDirectory;
        reloadData();
    }

    @Override
    protected Collection<FileItem> onCreateDataContainer() {
        return new LinkedList<>();
    }

    public void reloadData() {
        List<FileItem> data = new ArrayList<>();
        File[] files = mLogFileDirectory.listFiles();
        if (null != files) {
            for (File file : files) {
                data.add(new FileItem(file));
            }
        }
        setData(data);
    }

    @Override
    public LogFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LogFileViewHolder(mContext, parent);
    }
}
