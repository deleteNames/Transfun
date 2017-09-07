package net.translives.app.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseRecyclerViewActivity;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Topic;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.interf.AppBarStateChangeListener;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;

public class TopicActiveFollowActivity extends BaseRecyclerViewActivity<TopicActive> {

    public static void show(Context context) {
        Intent intent = new Intent(context, TopicActiveFollowActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onItemClick(TopicActive item, int position) {
        super.onItemClick(item, position);
        if (item.getId() <= 0) return;
        TopicActiveDetailActivity.show(this, item.getId());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TopicActive>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<TopicActive> getRecyclerAdapter() {
        return new TopicActiveAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getTopicFeed( mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }
}
