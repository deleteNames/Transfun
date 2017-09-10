package net.translives.app.vote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseRecyclerViewActivity;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Vote;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.topic.TopicActiveListActivity;
import net.translives.app.topic.TopicAdapter;

import java.lang.reflect.Type;

public class VoteListActivity extends BaseRecyclerViewActivity<Vote> {

    public static void show(Context context) {
        Intent intent = new Intent(context, VoteListActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onItemClick(Vote item, int position) {
        super.onItemClick(item, position);
        if (item.getId() <= 0) return;
        VoteDetailActivity.show(this, item.getId());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Vote>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Vote> getRecyclerAdapter() {
        return new VoteAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getEventList( mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }
}
