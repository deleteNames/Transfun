package net.translives.app.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;

import net.translives.app.base.BaseRecyclerViewActivity;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Follow;
import net.translives.app.bean.News;
import net.translives.app.bean.Topic;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.User;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.interf.AppBarStateChangeListener;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

public class TopicActiveListActivity extends BaseRecyclerViewActivity<TopicActive> {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.iv_topic_icon)
    ImageView mTipicIcon;
    @Bind(R.id.iv_topic_title)
    TextView mTipicTitle;

    @Bind(R.id.iv_topic_body)
    TextView mTipicBody;

    @Bind(R.id.layout_appbar)
    AppBarLayout mLayoutAppBar;
    @Bind(R.id.tv_toolbar_title)
    TextView mTitle;

    //@Bind(R.id.tv_btn_follow)
    //Button mBtnFollow;

    private Topic mTopic;

    public static void show(Activity activity, Topic topic) {
        Intent intent = new Intent(activity, TopicActiveListActivity.class);
        intent.putExtra("topic", topic);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_topic_list;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mTopic = (Topic) bundle.getSerializable("topic");
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        mToolbar.setNavigationIcon(R.mipmap.btn_back_normal);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLayoutAppBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if( state == State.EXPANDED ) {
                    mTitle.setVisibility(View.GONE);
                }else if(state == State.COLLAPSED){
                    mTitle.setVisibility(View.VISIBLE);
                }else {
                    //中间状态
                }
            }
        });

        List<Topic.Image> images = mTopic.getImages();
        if (images != null && images.size() > 0) {
            getImgLoader()
                    .load(images.get(0).getHref())
                    .placeholder(R.mipmap.ic_split_graph)
                    .into(mTipicIcon);
        } else {
            mTipicIcon.setImageResource(R.mipmap.ic_split_graph);
        }

        mTitle.setText(mTopic.getTitle());

        mTipicTitle.setText(mTopic.getTitle());
        mTipicBody.setText(mTopic.getBody());
/*
        if (mTopic.isFollow()) {
            mBtnFollow.setText("已关注");
            mBtnFollow.setTextColor(getResources().getColor(R.color.done_text_color_disabled));
            mBtnFollow.setSelected(true);
        }else{
            mBtnFollow.setText("+ 关注");;
            mBtnFollow.setTextColor(getResources().getColor(R.color.main_green));
        }

        mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followReverse();
            }
        });
*/
        invalidateOptionsMenu();
    }

    @Override
    protected void onItemClick(TopicActive item, int position) {
        super.onItemClick(item, position);
        if (item.getId() <= 0) return;
        TopicActiveDetailActivity.show(this,mTopic, item.getId());
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
        OSChinaApi.getTopicList(mTopic.getId(), mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pub_topic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.public_menu_send:
                // 判断登录
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(this);
                    return true;
                }

                TopicPublishActivity.show(this,mTopic);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
    }

/*
    public void followReverse() {
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(getContext());
            return;
        }

        //if (mFollowMenu == null) return;
        OSChinaApi.getFollowReverse(mTopic.getId(),"topic", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString
                    , Throwable throwable) {
                Toast.makeText(getContext(), "操作失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Follow> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<Follow>>() {
                        }.getType());
                if (result.isSuccess()) {
                    boolean isFollow = result.getResult().isFollow();
                    if(isFollow){
                        mBtnFollow.setText("已关注");
                        mBtnFollow.setTextColor(getResources().getColor(R.color.done_text_color_disabled));
                        mBtnFollow.setSelected(true);
                    }else{
                        mBtnFollow.setText("+ 关注");
                        mBtnFollow.setTextColor(getResources().getColor(R.color.main_green));
                        mBtnFollow.setSelected(false);
                    }

                } else {
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }
    */
}
