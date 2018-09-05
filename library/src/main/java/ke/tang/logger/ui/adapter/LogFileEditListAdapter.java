package ke.tang.logger.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ke.tang.logger.ui.adapter.holder.FileItem;
import ke.tang.logger.ui.adapter.holder.LogFileEditViewHolder;

/**
 * @author tangke
 */

public class LogFileEditListAdapter extends AbstractAdapter<FileItem, LogFileEditViewHolder> {
    private File mLogFileDirectory;

    public LogFileEditListAdapter(Context context, File logFileDirectory) {
        super(context);
        mLogFileDirectory = logFileDirectory;
        reloadData();
    }

    @Override
    public LogFileEditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LogFileEditViewHolder(mContext, parent, this);
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

    public int getSelectedItemCount() {
        int selectedCount = 0;
        final Collection<FileItem> data = getData();
        for (FileItem file : data) {
            if (file.isChecked()) {
                selectedCount++;
            }
        }
        return selectedCount;
    }

    public FileItem[] getSelectedItems() {
        List<FileItem> selectedItems = new ArrayList<>();
        final Collection<FileItem> data = getData();
        for (FileItem file : data) {
            if (file.isChecked()) {
                selectedItems.add(file);
            }
        }

        FileItem[] items = new FileItem[selectedItems.size()];
        selectedItems.toArray(items);
        return items;
    }

    public void unselectAll() {
        final Collection<FileItem> data = getData();
        for (FileItem file : data) {
            file.setChecked(false);
        }
        notifyDataSetChanged();
    }
}
