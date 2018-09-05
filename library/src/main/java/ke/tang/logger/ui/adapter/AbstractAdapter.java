package ke.tang.logger.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

import ke.tang.logger.ui.adapter.holder.AbstractViewHolder;

/**
 * @author tangke
 */

public abstract class AbstractAdapter<D, V extends AbstractViewHolder<D>> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    protected Collection<D> mData;
    protected RecyclerView mRecyclerView;

    public AbstractAdapter(Context context) {
        mContext = context;
        mData = onCreateDataContainer();
        if (null == mData) {
            mData = new ArrayList<>();
        }
    }

    protected Collection<D> onCreateDataContainer() {
        return null;
    }

    @Override
    public final void onBindViewHolder(V holder, int position) {
        holder.mItem = getItem(position);
        holder.onBind();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    protected D getItem(int position) {
        int index = 0;
        for (D d : mData) {
            if (index == position) {
                return d;
            }
            index++;
        }
        return null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void setData(Collection<? extends D> data) {
        mData.clear();
        addData(data);
    }

    public void addData(Collection<? extends D> data) {
        if (null != data) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(D data) {
        mData.add(data);
        notifyDataSetChanged();
    }

    public Collection<D> getData() {
        return mData;
    }
}
