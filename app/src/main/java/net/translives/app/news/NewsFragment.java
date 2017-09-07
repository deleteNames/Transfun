package net.translives.app.news;

import com.google.gson.reflect.TypeToken;

import net.translives.app.AppConfig;
import net.translives.app.OSCApplication;
import net.translives.app.api.OSChinaApi;
import net.translives.app.banner.HeaderView;
import net.translives.app.banner.NewsHeaderView;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.bean.News;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class NewsFragment extends BaseGeneralRecyclerFragment<News> {
    private OSCApplication.ReadState mReadState;
    private HeaderView mHeaderView;

    @Override
    public void initData() {

        mHeaderView = new NewsHeaderView(mContext, getImgLoader(),"banner");
        super.initData();
        mAdapter.setHeaderView(mHeaderView);
        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));

        mReadState = OSCApplication.getReadState("news_list");
    }

    @Override
    public void onItemClick(int position, long itemId) {
        News news = mAdapter.getItem(position);
        if (news == null)
            return;

        NewsDetailActivity.show(mContext, news);

        mReadState.put(news.getKey());
        mAdapter.updateItem(position);
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (mHeaderView != null)
            mHeaderView.requestBanner();
    }

    @Override
    protected void requestData() {
        OSChinaApi.getNewList(isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected void setListData(ResultBean<PageBean<News>> resultBean) {
        super.setListData(resultBean);
        mAdapter.setSystemTime(resultBean.getTime());
    }

    @Override
    protected BaseRecyclerAdapter<News> getRecyclerAdapter() {
        int mode = mHeaderView != null ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER;
        return new NewsAdapter(this, mode);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<News>>>() {
        }.getType();
    }

    @Override
    protected Class<News> getCacheClass() {
        return News.class;
    }

}
