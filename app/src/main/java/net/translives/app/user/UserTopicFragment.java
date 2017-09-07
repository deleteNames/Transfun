package net.translives.app.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.share.ShareDetailActivity;
import net.translives.app.topic.TopicActiveDetailActivity;

import java.lang.reflect.Type;

/**
 * @author thanatosx
 */
public class UserTopicFragment extends BaseRecyclerViewFragment<TopicActive> {

    private long userId;

    public static Fragment instantiate(long authorId) {
        UserTopicFragment fragment = new UserTopicFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", authorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getLong("user_id", 0);
    }

    @Override
    protected void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getUserTopicList(token, userId, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<TopicActive> getRecyclerAdapter() {
        return new UserTopicAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TopicActive>>>() {
        }.getType();
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        TopicActive topic = mAdapter.getItem(position);
        if (topic == null)
            return;

        TopicActiveDetailActivity.show(getContext(), topic.getId());
    }
}
