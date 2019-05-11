package studio.stc.lotusutil.util;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class RecyclerViewAdapterUtil<T> extends RecyclerView.Adapter {

    @LayoutRes
    private int mViewHolderLayout;

    public abstract void bindData(View itemView, int position, T data);

    private Context mContext;

    private List<T> mData;

    public RecyclerViewAdapterUtil(Context context, @LayoutRes int viewHolderLayout, List<T> data){
        mContext = context;
        mViewHolderLayout = viewHolderLayout;
        mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View baseView = LayoutInflater.from(mContext).inflate(mViewHolderLayout, parent, false);
        return new RecyclerView.ViewHolder(baseView) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        bindData(viewHolder.itemView, i, mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
