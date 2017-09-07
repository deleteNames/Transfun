package net.translives.app.news;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.News;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.comment.CommentsActivity;
import net.translives.app.dialog.ShareDialog;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.DetailCache;
import net.translives.app.util.DialogHelper;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class NewsDetailActivity extends BaseBackActivity implements Runnable, EasyPermissions.PermissionCallbacks {

    protected String mCommentHint;
    protected EmptyLayout mEmptyLayout;
    protected NewsDetailFragment mDetailFragment;
    //protected ShareDialog mAlertDialog;
    //protected TextView mCommentCountView;

    protected News mBean;
    private News mCacheBean;
    //protected String mIdent;

    protected ShareDialog mAlertDialog;

    public static void show(Context context, News news) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", news);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
/*
    public static void show(Context context, long id, int type) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(type);
        bean.setId(id);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
*/
    public static void show(Context context, long id) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        Bundle bundle = new Bundle();
        News bean = new News();
        bean.setId(id);
        bundle.putSerializable("bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    public static void show(Context context, long id, boolean isFav) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        Bundle bundle = new Bundle();
        News bean = new News();
        //bean.setType(News.TYPE_NEWS);
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

    protected NewsDetailFragment getDetailFragment() {
        return NewsDetailFragment.newInstance();
    }


    @Override
    public void run() {
        hideEmptyLayout();
        mDetailFragment.onPageFinished();
    }


    @Override
    protected void initWidget() {
        super.initWidget();

        if (!TDevice.hasWebView(this)) {
            finish();
            return;
        }

        mCommentHint = getString(R.string.pub_comment_hint);
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
        mBean = (News) getIntent().getSerializableExtra("bean");
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

    /*
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
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
            DetailCache.addCache("news",mBean.getId(),mBean);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAlertDialog == null)
            return;
        mAlertDialog.hideProgressDialog();
    }


    public void getCache() {
        mCacheBean = DetailCache.readCache("news",mBean.getId(),News.class);
        if (mCacheBean == null)
            return;

        mDetailFragment.showGetDetailSuccess(mCacheBean);

        showGetDetailSuccess(mCacheBean);
    }

    public void getDetail() {
        OSChinaApi.getNewDetail(mBean.getId(), new TextHttpResponseHandler() {
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
                    Type type = new TypeToken<ResultBean<News>>() {}.getType();
                    ResultBean<News> bean = AppOperator.createGson().fromJson(responseString, type);
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

    public void showGetDetailSuccess(News bean) {
        mBean = bean;

        //if (mCommentCountView != null && mBean.getStatistics() != null) {
        //    mCommentCountView.setText(String.valueOf(mBean.getStatistics().getComment()));
        //}
    }

    public void hideEmptyLayout() {
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        /*
        if (mCommentCountView != null) {
            mCommentCountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                CommentsActivity.show(NewsDetailActivity.this, mBean.getId(), OSChinaApi.COMMENT_NEWS, OSChinaApi.COMMENT_NEW_ORDER);
                }
            });
        }
*/
    }


    private static final int PERMISSION_ID = 0x0001;

    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    public void saveToFileByPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "请授予文件读写权限", PERMISSION_ID, permissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "", "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                //finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
