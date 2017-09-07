package net.translives.app.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppConfig;
import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Topic;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.media.ImageLoaderListener;
import net.translives.app.media.SpaceGridItemDecoration;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.CacheManager;
import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;
import net.translives.app.widget.SimplexToast;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

public class TopicCategoryActivity extends BaseBackActivity implements ImageLoaderListener, BaseRecyclerAdapter.OnItemClickListener  {
    @Bind(R.id.rv_image)
    RecyclerView mContentView;
    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    protected boolean isRefreshing;
    protected PageBean<Topic> mBean;

    private TopicCategoryAdapter mImageAdapter;

    public static void show(Context context) {
        Intent intent = new Intent(context, TopicCategoryActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_topic_catetgory;
    }

    @Override
    protected void initWidget() {

        mContentView.setLayoutManager(new GridLayoutManager(this, 3));
        //mContentView.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        mImageAdapter = new TopicCategoryAdapter(this, this);
        mContentView.setAdapter(mImageAdapter);
        //mContentView.setItemAnimator(null);
        mImageAdapter.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
        });
    }

    @Override
    protected void initData() {
        mBean = new PageBean<>();
        isRefreshing = true;
        mImageAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, true);

        OSChinaApi.getTopicCategory(isRefreshing ? null : mBean.getNextPageToken(), new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                TLog.log("HttpResponseHandler:onStart");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mImageAdapter.getItems().size() == 0) {
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    mImageAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
                }
                TLog.error("HttpResponseHandler:onFailure responseString:" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    ResultBean<PageBean<Topic>> resultBean = AppOperator.createGson().fromJson(responseString, new TypeToken<ResultBean<PageBean<Topic>>>() {
                    }.getType());
                    if (resultBean != null && resultBean.isSuccess() && resultBean.getResult().getItems() != null) {
                        mBean.setNextPageToken(resultBean.getResult().getNextPageToken());

                        if (isRefreshing) {
                            AppConfig.getAppConfig(getApplicationContext()).set("system_time", resultBean.getTime());
                            mBean.setItems(resultBean.getResult().getItems());
                            mImageAdapter.clear();
                            mImageAdapter.addAll(mBean.getItems());
                            mBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());

                        } else {
                            mImageAdapter.addAll(resultBean.getResult().getItems());
                        }

                        if (resultBean.getResult().getItems() == null || resultBean.getResult().getItems().size() < 20)
                            mImageAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);

                        if (mImageAdapter.getItems().size() > 0) {
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        } else {
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        }
                    } else {
                        if (resultBean.getCode() == ResultBean.RESULT_TOKEN_ERROR) {
                            SimplexToast.show(resultBean.getMessage());
                        }
                        mImageAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public void displayImage(ImageView iv, String path) {
        getImageLoader().load(path)
                .asBitmap()
                .centerCrop()
                .error(R.mipmap.ic_split_graph)
                .into(iv);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Topic topic = mImageAdapter.getItem(position);
        if (topic == null)
            return;
        TopicActiveListsActivity.show(this,topic);
    }
}
