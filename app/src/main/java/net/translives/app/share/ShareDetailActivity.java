package net.translives.app.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.Question;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.DetailCache;
import net.translives.app.util.TDevice;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

//import net.translives.app.bean.Report;
//import net.translives.app.detail.ReportDialog;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class ShareDetailActivity extends BaseBackActivity implements Runnable{

    protected String mCommentHint;
    protected EmptyLayout mEmptyLayout;
    protected ShareDetailFragment mDetailFragment;
    //protected TextView mCommentCountView;

    protected Question mBean;
    private Question mCacheBean;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, ShareDetailActivity.class);
        Bundle bundle = new Bundle();
        Question bean = new Question();
        //bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id, boolean isFav) {
        Intent intent = new Intent(context, ShareDetailActivity.class);
        Bundle bundle = new Bundle();
        Question bean = new Question();
        //bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bean.setFavorite(isFav);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_detail_v2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        if (!TDevice.hasWebView(this)) {
            finish();
            return;
        }
        mCommentHint = "我要回答";
        LinearLayout layComment = (LinearLayout) findViewById(R.id.ll_comment);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.lay_error);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    getDetail();
                }
            }
        });
        mBean = (Question) getIntent().getSerializableExtra("bean");
        //mIdent = getIntent().getStringExtra("ident");
        mDetailFragment = getDetailFragment();

        Bundle bundle = getIntent().getExtras();
        mDetailFragment.setArguments(bundle);
        addFragment(R.id.lay_container, mDetailFragment);

        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                getCache();
                getDetail();
            }
        });
    }

    protected ShareDetailFragment getDetailFragment() {
        return ShareDetailFragment.newInstance();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_scroll_comment);
        if (item != null) {
            View action = item.getActionView();
            if (action != null) {
                View tv = action.findViewById(R.id.tv_comment_count);
                if (tv != null) {
                    mCommentCountView = (TextView) tv;
                    if (mBean.getStatistics() != null)
                        mCommentCountView.setText(mBean.getStatistics().getComment() + "");
                }
            }
        }
        return true;
    }
*/

    @Override
    public void finish() {
        if (mEmptyLayout.getErrorState() == EmptyLayout.HIDE_LAYOUT)
            DetailCache.addCache("share",mBean.getId(),mBean);
        super.finish();
    }

    public void getCache() {
        mCacheBean = DetailCache.readCache("share",mBean.getId(),Question.class);
        if (mCacheBean == null)
            return;

        mDetailFragment.showGetDetailSuccess(mCacheBean);

        showGetDetailSuccess(mCacheBean);
    }

    public void getDetail() {

        if (mCacheBean != null){
            //return;
        }

        OSChinaApi.getQuestionDetail(mBean.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //mView.showNetworkError(R.string.tip_network_error);
                if (mCacheBean != null)
                    return;
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Question>>() {}.getType();
                    ResultBean<Question> bean = AppOperator.createGson().fromJson(responseString, type);
                    if (bean.isSuccess()) {
                        mBean = bean.getResult();
                        mDetailFragment.showGetDetailSuccess(mBean);
                        showGetDetailSuccess(mBean);
                    } else {
                        if (mCacheBean != null)
                            return;
                        mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showGetDetailSuccess(Question bean) {
        mBean = bean;
        //if (mDelegation != null)
        //    mDelegation.setFavDrawable(mBean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        //if (mCommentCountView != null && mBean.getStatistics() != null) {
        //    mCommentCountView.setText(String.valueOf(mBean.getStatistics().getComment()));
        //}
    }

    @Override
    public void run() {
        hideEmptyLayout();
    }

    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        //if (mCommentCountView != null) {
        //    mCommentCountView.setOnClickListener(new View.OnClickListener() {
        //        @Override
         //       public void onClick(View v) {
        //        CommentsActivity.show(QuestionDetailActivity.this, mBean.getId(), OSChinaApi.COMMENT_QUESTION, OSChinaApi.COMMENT_NEW_ORDER);
        //        }
        //    });
        //}

    }
}
