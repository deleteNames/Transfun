package net.translives.app.vote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseRecyclerViewActivity;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Topic;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.topic.TopicActiveListActivity;
import net.translives.app.topic.TopicAdapter;

import java.lang.reflect.Type;

public class VoteListActivity extends BaseRecyclerViewActivity<Topic> {

    public static void show(Context context) {
        Intent intent = new Intent(context, VoteListActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onItemClick(Topic item, int position) {
        super.onItemClick(item, position);
        if (item.getId() <= 0) return;
        TopicActiveListActivity.show(this, item);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Topic>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Topic> getRecyclerAdapter() {
        return new TopicAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getTopicCategory( mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }
}
