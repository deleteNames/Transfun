package net.translives.app.base.fragments;

import net.translives.app.interf.OnTabReselectListener;

/**
 * Created by huanghaibin_dev
 * on 2016/8/30.
 */
public abstract class BaseGeneralRecyclerFragment<T> extends BaseRecyclerViewFragment<T> implements OnTabReselectListener {
    @Override
    public void onTabReselect() {
        if (mRecyclerView != null && !isRefreshing) {
            mRecyclerView.scrollToPosition(0);
            mRefreshLayout.setRefreshing(true);
            onRefreshing();
        }
    }
}
