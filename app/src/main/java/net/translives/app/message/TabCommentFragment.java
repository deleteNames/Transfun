package net.translives.app.message;

import android.content.Context;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.Mention;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.simple.Origin;
import net.translives.app.news.NewsDetailActivity;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.message.TabMentionAdapter;
import java.lang.reflect.Type;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class TabCommentFragment extends BaseRecyclerViewFragment<Mention> {

    private MessagePageFragment activity;
/*
    public TabCommentFragment(MessagePageFragment messagePageFragment) {
        activity = messagePageFragment;
    }
*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        //activity.onRequestSuccess(1);
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getMsgCommentList(isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Mention mention = mAdapter.getItem(position);
        if (mention == null)
            return;
        Origin origin = mention.getOrigin();
        switch (origin.getType()) {
            case Origin.ORIGIN_TYPE_DISCUSS:
                QuestionDetailActivity.show(getContext(), origin.getId());
                break;
            case Origin.ORIGIN_TYPE_NEWS:
                NewsDetailActivity.show(getContext(), origin.getId());
                break;
            default:
                // pass
        }
    }

    @Override
    protected BaseRecyclerAdapter<Mention> getRecyclerAdapter() {
        return new TabMentionAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Mention>>>() {
        }.getType();
    }
}
