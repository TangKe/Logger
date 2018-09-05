package ke.tang.logger.ui.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ke.tang.logger.util.Intents;

/**
 * @author tangke
 */

public class LogFileViewHolder extends AbstractViewHolder<FileItem> implements View.OnClickListener {
    public TextView mText;

    public LogFileViewHolder(Context context, ViewGroup parent) {
        super(context, LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false));
        itemView.setOnClickListener(this);
        mText = (TextView) itemView;
    }

    @Override
    public void onBind() {
        mText.setText(mItem.getFile().getName());
    }

    @Override
    public void onClick(View v) {
        mContext.startActivity(Intents.view(mContext, mItem.mFile, "text/*"));
    }
}
