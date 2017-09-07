package net.translives.app.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BackActivity;
import net.translives.app.bean.Event;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.dialog.ShareDialog;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.DetailCache;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailActivity extends BackActivity implements Runnable{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.iv_event)
    ImageView mImageEvent;

    @Bind(R.id.header_view)
    View mHeaderView;

    protected EmptyLayout mEmptyLayout;
    protected EventDetailFragment mDetailFragment;

    protected ShareDialog mAlertDialog;

    protected Event mBean;
    private Event mCacheBean;

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        Bundle bundle = new Bundle();
        Event bean = new Event();
        bean.setId(id);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_event_detail;
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


        mBean = (Event) getIntent().getSerializableExtra("bean");
        //mIdent = getIntent().getStringExtra("ident");
        mDetailFragment = getDetailFragment();
        addFragment(R.id.lay_container, mDetailFragment);

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

        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                getCache();
                getDetail();
            }
        });
    }

    public void getCache() {
        mCacheBean = DetailCache.readCache("event",mBean.getId(),Event.class);
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
                    Type type = new TypeToken<ResultBean<Event>>() {}.getType();
                    ResultBean<Event> bean = AppOperator.createGson().fromJson(responseString, type);
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

    public void showGetDetailSuccess(Event bean) {
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

    protected EventDetailFragment getDetailFragment() {
        return EventDetailFragment.newInstance();
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
            DetailCache.addCache("event",mBean.getId(),mBean);
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
