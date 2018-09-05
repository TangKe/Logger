package ke.tang.logger.ui.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import ke.tang.logger.ui.adapter.AbstractAdapter;

/**
 * @author tangke
 */

public class LogFileEditViewHolder extends AbstractViewHolder<FileItem> implements View.OnClickListener {
    private CheckedTextView mText;
    private AbstractAdapter mAdapter;

    public LogFileEditViewHolder(Context context, ViewGroup parent, AbstractAdapter adapter) {
        super(context, LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false));
        itemView.setOnClickListener(this);
        mText = (CheckedTextView) itemView;
        mAdapter = adapter;
    }

    @Override
    public void onBind() {
        mText.setText(mItem.getFile().getName());
        mText.setChecked(mItem.isChecked());
    }

    @Override
    public void onClick(View v) {
        mItem.toggle();
        mAdapter.notifyDataSetChanged();
    }
}
