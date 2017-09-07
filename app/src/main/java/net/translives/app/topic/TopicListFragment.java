package net.translives.app.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;

import net.translives.app.AppConfig;
import net.translives.app.api.OSChinaApi;
import net.translives.app.banner.TopicHeader;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;


public class TopicListFragment extends BaseGeneralRecyclerFragment<TopicActive> {

    public static final int CATALOG_HOT = 0X0001;
    public static final int CATALOG_NEW = 0X0002;


    public static final String BUNDLE_KEY_REQUEST_CATALOG = "BUNDLE_KEY_REQUEST_CATALOG";

    public int mReqCatalog;

    public static Fragment instantiate(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_REQUEST_CATALOG, catalog);
        Fragment fragment = new TopicListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mReqCatalog = bundle.getInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_HOT);
    }

    /**
     * fragment被销毁的时候重新调用，初始化保存的数据
     *
     * @param bundle onSaveInstanceState
     */
    @Override
    protected void onRestartInstance(Bundle bundle) {
        super.onRestartInstance(bundle);
        mReqCatalog = bundle.getInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_HOT);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onItemClick(int position, long itemId) {
        TopicActive topic = mAdapter.getItem(position);
        if (topic == null)
            return;

        TopicActiveDetailActivity.show(getActivity(), topic.getId());
        mAdapter.updateItem(position);
    }

    @Override
    protected void requestData() {

        String pageToken = isRefreshing ? null : mBean.getNextPageToken();
        switch (mReqCatalog) {
            case CATALOG_NEW:
                OSChinaApi.getTopicFeed(pageToken, mHandler);
                break;
            case CATALOG_HOT:
                OSChinaApi.getTopicFeed(pageToken, mHandler);
                break;
        }
    }

    @Override
    protected void setListData(ResultBean<PageBean<TopicActive>> resultBean) {
        super.setListData(resultBean);
        mAdapter.setSystemTime(resultBean.getTime());
    }

    @Override
    protected BaseRecyclerAdapter<TopicActive> getRecyclerAdapter() {
        int mode = BaseRecyclerAdapter.ONLY_FOOTER;
        return new TopicActiveAdapter(this, mode);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TopicActive>>>() {
        }.getType();
    }

    @Override
    protected Class<TopicActive> getCacheClass() {
        return TopicActive.class;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_KEY_REQUEST_CATALOG, mReqCatalog);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 1) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            //mUserId = AccountHelper.getUserId();
            onRefreshing();
        }
    }
}
