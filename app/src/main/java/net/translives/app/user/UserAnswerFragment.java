package net.translives.app.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.Answer;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.comment.QuesAnswerDetailActivity;
import net.translives.app.util.TLog;

import java.lang.reflect.Type;

/**
 * @author thanatosx
 */
public class UserAnswerFragment extends BaseRecyclerViewFragment<Answer> {

    private long userId;

    public static Fragment instantiate(long authorId) {
        UserAnswerFragment fragment = new UserAnswerFragment();
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
        OSChinaApi.getUserAnswerList(token, userId, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<Answer> getRecyclerAdapter() {
        return new UserAnswerAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Answer>>>() {
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
        Answer comment = mAdapter.getItem(position);
        if (comment == null)
            return;
        QuesAnswerDetailActivity.show(getActivity(),comment,comment.getQuestionId(),OSChinaApi.COMMENT_QUESTION);
    }
}
