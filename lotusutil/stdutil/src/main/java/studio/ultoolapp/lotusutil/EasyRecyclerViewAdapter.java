package studio.ultoolapp.lotusutil;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class EasyRecyclerViewAdapter<T> extends RecyclerView.Adapter {

    // 添加Header / Footer功能。參見 https://www.jianshu.com/p/991062d964cf
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;
    @LayoutRes
    protected int mViewHolderLayout;
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    protected List<T> mDataList;
    private View headerView = null;
    private View footerView = null;

    public EasyRecyclerViewAdapter(Context context, @LayoutRes int viewHolderLayout, List<T> data) {
        mContext = context;
        mViewHolderLayout = viewHolderLayout;
        mDataList = data;
    }

    /**
     * @param position 注意這裡的數字只會是0 ~ data-1， <b>並不會包含Header或Footer。</b>設定header或footer請使用 {@link #getHeaderView()} / {@link #getFooterView()}。
     */
    public abstract void bindData(View itemView, int position, T data);

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyItemInserted(0);
    }

    public void setHeaderView(@LayoutRes int headerViewLayout) {
        if (mRecyclerView == null) {
            Log.e(mContext.getClass().getSimpleName(), "setHeaderView: no recycler view applied. Please call RecyclerView.setAdapter() before this method.", new NullPointerException("RecyclerView"));
            return;
        }
        setHeaderView(mRecyclerView, headerViewLayout);
    }

    public void setHeaderView(RecyclerView parent, @LayoutRes int headerViewLayout) {
        View view = LayoutInflater.from(mContext).inflate(headerViewLayout, parent, false);
        if (view == null) {
            Log.e(mContext.getClass().getSimpleName(), "setHeaderView: error when inflating / creating view.", new NullPointerException("View"));
            return;
        }
        setHeaderView(view);
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setFooterView(@LayoutRes int headerViewLayout) {
        if (mRecyclerView == null) {
            Log.e(mContext.getClass().getSimpleName(), "setFooterView: no recycler view applied. Please call RecyclerView.setAdapter() before this method.", new NullPointerException("RecyclerView"));
            return;
        }
        setFooterView(mRecyclerView, headerViewLayout);
    }

    public void setFooterView(RecyclerView parent, @LayoutRes int footerViewLayout) {
        View view = LayoutInflater.from(mContext).inflate(footerViewLayout, parent, false);
        if (view == null) {
            Log.e(mContext.getClass().getSimpleName(), "setFooterView: error when inflating / creating view.", new NullPointerException("View"));
            return;
        }
        setFooterView(view);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (headerView != null && viewType == TYPE_HEADER) {
            return new RecyclerView.ViewHolder(headerView) {
            };
        }
        if (footerView != null && viewType == TYPE_FOOTER) {
            return new RecyclerView.ViewHolder(footerView) {
            };
        }
        View baseView = LayoutInflater.from(mContext).inflate(mViewHolderLayout, parent, false);
        return new RecyclerView.ViewHolder(baseView) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int correctFactor = (headerView != null ? 1 : 0);
        if (getItemViewType(position) == TYPE_NORMAL)
            bindData(viewHolder.itemView, position - correctFactor, mDataList.get(position - correctFactor)); // headerView會被計算進item count裡面，注意位置
        // headerView與footerView不用設定
    }

    @Override
    public int getItemCount() {
        // headerView和footerView需要被計算進recyclerView的大小裡面。注意總長度
        return mDataList.size() + (headerView != null ? 1 : 0) + (footerView != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) return TYPE_HEADER;
        if (footerView != null && position == getItemCount() - 1) return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    /**
     * 監聽器：RecyclerView滑動到尾端的時候。
     * 可以用在 {@link RecyclerView#addOnScrollListener(RecyclerView.OnScrollListener)}的場合。
     */
    public abstract static class OnScrollToEndListener extends RecyclerView.OnScrollListener {
        private boolean isVertical;
        private boolean smoothOperation = false;

        /**
         * @param isVertical 若該RecyclerView為垂直滑動，請填入true; 否則填入false.
         */
        protected OnScrollToEndListener(boolean isVertical) {
            this.isVertical = isVertical;
        }

        /**
         * @param smoothOperation 若為true，則在拖曳到尾端時就觸發監聽器，但可能較耗費效能；反之，在尾端停下(放手)後才會觸發。
         */
        public OnScrollToEndListener setSmoothOperation(boolean smoothOperation) {
            this.smoothOperation = smoothOperation;
            return this;
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && !smoothOperation) {
                if (isVertical) {
                    if (!recyclerView.canScrollVertically(1)) {
                        onScrollToEnd();
                    }
                } else {
                    if (!recyclerView.canScrollHorizontally(1)) {
                        onScrollToEnd();
                    }
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (smoothOperation) {
                if (isVertical) {
                    if (!recyclerView.canScrollVertically(1)) {
                        onScrollToEnd();
                    }
                } else {
                    if (!recyclerView.canScrollHorizontally(1)) {
                        onScrollToEnd();
                    }
                }
            }
        }

        /**
         * 當滑動到尾端的時候會觸發。
         */
        public abstract void onScrollToEnd();
    }
}
