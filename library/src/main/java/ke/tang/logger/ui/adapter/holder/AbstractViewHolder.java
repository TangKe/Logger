package ke.tang.logger.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author tangke
 */

public abstract class AbstractViewHolder<D> extends RecyclerView.ViewHolder {
    protected Context mContext;
    public D mItem;

    public AbstractViewHolder(Context context, View view) {
        super(view);
        mContext = context;
    }

    public abstract void onBind();
}