package ke.tang.logger.ui.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ke.tang.logger.R;
import ke.tang.logger.util.Common;
import ke.tang.logger.util.Intents;

/**
 * @author tangke
 */

public class LogFileViewHolder extends AbstractViewHolder<FileItem> implements View.OnClickListener {
    public TextView mName;
    private TextView mIsCurrentContinuedLogFile;

    public LogFileViewHolder(Context context, ViewGroup parent) {
        super(context, LayoutInflater.from(context).inflate(R.layout.logger_layout_log_file_list_item, parent, false));
        itemView.setOnClickListener(this);
        mName = itemView.findViewById(R.id.name);
        mIsCurrentContinuedLogFile = itemView.findViewById(R.id.isCurrentContinuedLogFile);
    }

    @Override
    public void onBind() {
        mName.setText(mItem.getFile().getName());
        mIsCurrentContinuedLogFile.setVisibility(mItem.getFile().equals(Common.getCurrentContinuedLogFile()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        mContext.startActivity(Intents.view(mContext, mItem.mFile, "text/*"));
    }
}
