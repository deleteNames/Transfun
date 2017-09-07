package net.translives.app.topic;

import com.google.gson.reflect.TypeToken;

import net.translives.app.AppConfig;
import net.translives.app.api.OSChinaApi;
import net.translives.app.banner.TopicHeader;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.topic.TopicActiveAdapter;
import net.translives.app.topic.TopicActiveDetailActivity;

import java.lang.reflect.Type;


public class TopicFeedFragment extends BaseGeneralRecyclerFragment<TopicActive> {

    private TopicHeader mHeaderView;

    @Override
    public void initData() {

        mHeaderView = new TopicHeader(mContext, getImgLoader(),"banner");
        super.initData();
        mAdapter.setHeaderView(mHeaderView);
        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));
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
        OSChinaApi.getTopicFeed(isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected void setListData(ResultBean<PageBean<TopicActive>> resultBean) {
        super.setListData(resultBean);
        mAdapter.setSystemTime(resultBean.getTime());
    }

    @Override
    protected BaseRecyclerAdapter<TopicActive> getRecyclerAdapter() {
        int mode = mHeaderView != null ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER;
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

}
