package ke.tang.logger.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ke.tang.logger.Logger;
import ke.tang.logger.R;
import ke.tang.logger.ui.adapter.LogFileEditListAdapter;
import ke.tang.logger.ui.adapter.LogFileListAdapter;
import ke.tang.logger.ui.adapter.holder.FileItem;
import ke.tang.logger.util.Common;
import ke.tang.logger.util.FileDeleteTask;
import ke.tang.logger.util.Intents;

/**
 * @author tangke
 */

public class LogManageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DatePickerDialog.OnDateSetListener, FileDeleteTask.OnFileDeleteTaskListener {
    private final static String EXTRA_FILE = "file";

    public static LogManageFragment newInstance(Context context, File file) {
        Bundle data = new Bundle();
        data.putSerializable(EXTRA_FILE, file);
        return (LogManageFragment) Fragment.instantiate(context, LogManageFragment.class.getName(), data);
    }

    private RecyclerView mList;
    private SwipeRefreshLayout mSwipe;
    private ActionMode mEditActionMode;
    private EditActionModeCallback mEditActionModeCallback = new EditActionModeCallback();
    private LogFileListAdapter mAdapter;
    private LogFileEditListAdapter mEditAdapter;

    private File mDirectory;

    private MenuItem mFilterByDateItem;
    private MenuItem mFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        File file = mDirectory = (File) getArguments().getSerializable(EXTRA_FILE);
        mAdapter = new LogFileListAdapter(getContext(), file);
        mEditAdapter = new LogFileEditListAdapter(getContext(), file);

        mEditAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                invalidateActionMode();
            }

            private void invalidateActionMode() {
                if (null != mEditActionMode) {
                    mEditActionMode.invalidate();
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                invalidateActionMode();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.logger_menu_log_manage, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mFilterByDateItem = menu.findItem(R.id.date);
        mFilter = menu.findItem(R.id.filter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (R.id.action == id) {
            mEditActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mEditActionModeCallback);
        } else if (R.id.date == id) {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), this, 2018, 4, 12);
            dialog.show();
        } else if (R.id.clearFilter == id) {
        } else if (R.id.record == id) {
            Logger.toggleLogMonitor();
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.logger_fragment_log_manage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList = view.findViewById(R.id.list);
        mSwipe = view.findViewById(R.id.swipe);
        mSwipe.setOnRefreshListener(this);
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        mAdapter.reloadData();
        mEditAdapter.reloadData();
        mSwipe.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mFilterByDateItem.setChecked(true);
    }

    @Override
    public void onTaskComplete(List<FileItem> result) {
        List<Integer> deletedFileItemPosition = new ArrayList<>();
        Collection<FileItem> items = mEditAdapter.getData();
        for (FileItem item : result) {
            int index = 0;
            for (FileItem adapterItem : items) {
                if (adapterItem == item) {
                    deletedFileItemPosition.add(index);
                    items.remove(adapterItem);
                    break;
                }
                index++;
            }
        }
        for (Integer position : deletedFileItemPosition) {
            mEditAdapter.notifyItemRemoved(position);
        }
        mAdapter.reloadData();
    }

    private class EditActionModeCallback implements ActionMode.Callback {
        MenuItem mSelectedFileCount;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mList.setAdapter(mEditAdapter);
            mode.getMenuInflater().inflate(R.menu.logger_action_mode_edit, menu);
            mSelectedFileCount = menu.findItem(R.id.selectedFileCount);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mSelectedFileCount.setTitle(String.format("%d/%d", mEditAdapter.getSelectedItemCount(), mEditAdapter.getItemCount()));
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final int id = item.getItemId();
            if (id == R.id.selectAll) {
                Collection<FileItem> data = mEditAdapter.getData();
                for (FileItem file : data) {
                    file.setChecked(true);
                }

                mEditAdapter.notifyDataSetChanged();
            } else if (id == R.id.unselectAll) {
                Collection<FileItem> data = mEditAdapter.getData();
                for (FileItem file : data) {
                    file.setChecked(false);
                }

                mEditAdapter.notifyDataSetChanged();
            } else if (id == R.id.delete) {
                final FileItem[] selectedItems = mEditAdapter.getSelectedItems();
                if (null != selectedItems && selectedItems.length > 0) {
                    FileDeleteTask task = new FileDeleteTask(LogManageFragment.this);
                    task.execute(selectedItems);
                }
            } else if (id == R.id.share) {
                final FileItem[] selectedItems = mEditAdapter.getSelectedItems();
                if (null != selectedItems && selectedItems.length > 0) {
                    if (selectedItems.length == 1) {
                        startActivity(Intents.send(getContext(), FileProvider.getUriForFile(getContext(), Common.getLoggerProviderAuthorities(getContext()), selectedItems[0].getFile())));
                    } else {
                        Uri[] uris = new Uri[selectedItems.length];
                        for (int index = 0; index < selectedItems.length; index++) {
                            uris[index] = FileProvider.getUriForFile(getContext(), Common.getLoggerProviderAuthorities(getContext()), selectedItems[index].getFile());
                        }
                        startActivity(Intents.send(getContext(), uris));
                    }
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mList.setAdapter(mAdapter);
            mEditAdapter.unselectAll();
        }
    }

}
