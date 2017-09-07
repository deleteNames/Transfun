package net.translives.app.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.TopicComment;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.topic.TopicActiveDetailActivity;

import java.lang.reflect.Type;

/**
 * @author thanatosx
 */
public class UserCommentFragment extends BaseRecyclerViewFragment<TopicComment> {

    private long userId;

    public static Fragment instantiate(long authorId) {
        UserCommentFragment fragment = new UserCommentFragment();
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
        OSChinaApi.getUserTopicCommentList(token, userId, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<TopicComment> getRecyclerAdapter() {
        return new UserCommentAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TopicComment>>>() {
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
        TopicComment comment = mAdapter.getItem(position);
        if (comment == null)
            return;

        TopicActiveDetailActivity.show(getContext(), comment.getActive().getTopic(), comment.getActive().getId());
    }
}
