package net.translives.app.vote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppContext;
import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.Attach;
import net.translives.app.bean.Event;
import net.translives.app.bean.Topic;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.Vote;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.comment.Reply;
import net.translives.app.behavior.CommentBar;
import net.translives.app.dialog.ShareDialog;
import net.translives.app.event.EventDetailActivity;
import net.translives.app.event.EventDetailFragment;
import net.translives.app.interf.OnKeyArrivedListenerAdapter;
import net.translives.app.topic.CommentAdapter;
import net.translives.app.topic.CommentView;
import net.translives.app.topic.TopicCommentPublishActivity;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.BitmapUtil;
import net.translives.app.util.DetailCache;
import net.translives.app.util.DialogHelper;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.PicturesCompressor;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;
import net.translives.app.widget.OWebView;
import net.translives.app.widget.PortraitView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

//import net.translives.app.bean.Report;
//import net.translives.app.detail.ReportDialog;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class VoteDetailActivity extends BaseBackActivity implements Runnable{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.iv_event)
    ImageView mImageEvent;

    @Bind(R.id.header_view)
    View mHeaderView;

    protected EmptyLayout mEmptyLayout;
    protected VoteDetailFragment mDetailFragment;

    protected ShareDialog mAlertDialog;

    protected Vote mBean;
    private Vote mCacheBean;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, VoteDetailActivity.class);
        Bundle bundle = new Bundle();
        Vote bean = new Vote();
        bean.setId(id);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_vote_detail;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        if (!TDevice.hasWebView(this)) {
            finish();
            return;
        }

        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        mBean = (Vote) getIntent().getSerializableExtra("bean");
        //mIdent = getIntent().getStringExtra("ident");
        mDetailFragment = getDetailFragment();
        addFragment(R.id.lay_container, mDetailFragment);

        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                getCache();
                getDetail();
            }
        });
    }

    public void getCache() {
        mCacheBean = DetailCache.readCache("vote",mBean.getId(),Vote.class);
        if (mCacheBean == null)
            return;

        mDetailFragment.showGetDetailSuccess(mCacheBean);

        showGetDetailSuccess(mCacheBean);
    }

    public void getDetail() {
        OSChinaApi.getEventDetail(mBean.getId(), new TextHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<Vote>>() {}.getType();
                    ResultBean<Vote> bean = AppOperator.createGson().fromJson(responseString, type);
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

            @Override
            public void onCancel() {
                super.onCancel();
                if (mCacheBean != null)
                    return;
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
    }

    public void showGetDetailSuccess(Vote bean) {
        mBean = bean;
    }


    @Override
    public void run() {
        hideEmptyLayout();
    }

    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        if (isDestroy())
            return;

        mHeaderView.setVisibility(View.VISIBLE);
        mHeaderView.getLayoutParams().height = 400;
        getImageLoader().load(mBean.getImg()).into(mImageEvent);
        mImageEvent.setVisibility(View.VISIBLE);
    }

    protected VoteDetailFragment getDetailFragment() {
        return VoteDetailFragment.newInstance();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (mAlertDialog == null)
            return;
        mAlertDialog.hideProgressDialog();
    }

    @Override
    public void finish() {
        if (mEmptyLayout.getErrorState() == EmptyLayout.HIDE_LAYOUT)
            DetailCache.addCache("vote",mBean.getId(),mBean);
        super.finish();
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    protected boolean toShare(String title, String content, String url) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(url))
            return false;

        String imageUrl = null;
        //匹配内容中是否有图片，有就返回第一张图片的url
        //"<\\s*img\\s+([^>]*)\\s*/>"
        String regex = "<img .*src=\"([^\"]+)\"";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            imageUrl = matcher.group(1);
            break;
        }

        content = content.trim();
        if (content.length() > 55) {
            content = HTMLUtil.delHTMLTag(content);
            if (content.length() > 55)
                content = StringUtils.getSubString(0, 55, content);
        } else {
            content = HTMLUtil.delHTMLTag(content);
        }
        if (TextUtils.isEmpty(content))
            content = "";


        // 分享
        if (mAlertDialog == null) {
            mAlertDialog = new ShareDialog(this, mBean.getId())
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)//如果没有图片，即url为null，直接加入app默认分享icon
                    .url(url).with();
        }
        mAlertDialog.show();

        return true;
    }
}
