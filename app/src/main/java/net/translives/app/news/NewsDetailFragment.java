package net.translives.app.news;

import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.fragments.BaseFragment;
import net.translives.app.bean.Collection;
import net.translives.app.bean.News;
//import net.translives.app.bean.Software;
//import net.translives.app.util.ReadedIndexCacheManager;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.comment.CommentsActivity;
import net.translives.app.dialog.ShareDialog;
import net.translives.app.util.HTMLUtil;
import net.translives.app.cache.ReadedIndexCacheManager;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TLog;
import net.translives.app.widget.OWebView;
import net.translives.app.widget.ScreenView;
import net.translives.app.widget.SimplexToast;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class NewsDetailFragment extends BaseFragment implements  View.OnClickListener {
    @Bind(R.id.tv_title)
    TextView mTextTitle;
    //@Bind(R.id.tv_author)
    //TextView mTextAuthor;
    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;
    //@Bind(R.id.tv_info_view)
    //TextView mTextViewCount;
    //@Bind(R.id.tv_info_comment)
    //TextView mTextCommentCount;
    //@Bind(R.id.fl_lab)
    //FlowLayout mFlowLayout;

    @Bind(R.id.iv_fav)
    ImageView mImageFav;
    @Bind(R.id.iv_fav_txt)
    TextView mFavTxt;

    @Bind(R.id.tv_comment_count)
    TextView mTextCommentCount;

    protected OWebView mWebView;
    protected News mBean;

    //protected DetailAboutView mDetailAboutView;
    protected int CACHE_CATALOG;
    protected NestedScrollView mViewScroller;
    protected ScreenView mScreenView;



    public static NewsDetailFragment newInstance() {
        return new NewsDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail;
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_NEWS;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWebView = (OWebView) mRoot.findViewById(R.id.webView);

        //mDetailAboutView = (DetailAboutView) mRoot.findViewById(R.id.lay_detail_about);
        mViewScroller = (NestedScrollView) mRoot.findViewById(R.id.lay_nsv);
        mScreenView = (ScreenView) mRoot.findViewById(R.id.screenView);
    }

    @OnClick({R.id.ll_comment, R.id.ll_fav,R.id.ll_share})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_comment:
                CommentsActivity.show(getActivity(), mBean.getId(), OSChinaApi.COMMENT_NEWS, OSChinaApi.COMMENT_NEW_ORDER);
                break;
            case R.id.ll_fav:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(mContext);
                    return;
                }
                favReverse("news",mBean.getId());
                break;
            case R.id.ll_share:
                toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
                break;
        }
    }

    /*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mAlertDialog == null)
                return;
            mAlertDialog.hideProgressDialog();
        } else {
            //相当于Fragment的onPause
        }
    }*/

    @Override
    public void onDestroy() {

        if (mBean != null && mBean.getId() > 0 && mViewScroller != null) {
            ReadedIndexCacheManager.saveIndex(getContext(), mBean.getId(), CACHE_CATALOG, (mScreenView != null && mScreenView.isViewInScreen()) ? 0 : mViewScroller.getScrollY());
        }

        mWebView.destroy();
        super.onDestroy();
    }

    public void showGetDetailSuccess(News bean) {

        this.mBean = bean;
        if (mContext == null) return;
        mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);

        mTextTitle.setText(bean.getTitle());
        mTextPubDate.setText("发布于 " + StringUtils.formatYearMonthDay(bean.getPubDate()));

        mImageFav.setImageResource(bean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);
        mFavTxt.setText(bean.isFavorite() ? "已收藏" : "收藏");

        News.Statistics statistics = bean.getStatistics();
        mTextCommentCount.setText(String.format("评论(%d)", statistics != null ? statistics.getComment() : 0));

    }


    public void onPageFinished() {
        if (mBean == null || mBean.getId() <= 0) return;

        final int index = ReadedIndexCacheManager.getIndex(getContext(), mBean.getId(), CACHE_CATALOG);
        if (index != 0) {
            if (mViewScroller == null)
                return;
            mViewScroller.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mViewScroller == null)
                        return;
                    mViewScroller.smoothScrollTo(0, index);
                }
            }, 250);
        }
    }


    public void favReverse(String type, long id) {

        OSChinaApi.getFavReverse(id, type, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showFavError();
                showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Collection>>() {
                    }.getType();
                    ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        Collection collection = resultBean.getResult();
                        mBean.setFavorite(collection.isFavorite());
                        showFavReverseSuccess(collection.isFavorite(), collection.isFavorite() ? R.string.add_favorite_success : R.string.del_favorite_success);


                    } else {
                        showFavError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    public void showFavReverseSuccess(boolean isFav, int strId) {
        mFavTxt.setText(isFav ? "已收藏" : "收藏");
        mImageFav.setImageResource(isFav ? R.drawable.ic_faved : R.drawable.ic_fav);
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }

    public void showFavError() {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, "收藏失败");
    }

    public void showNetworkError(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }

    public void toShare(String title, String content, String url){
        ((NewsDetailActivity) mContext).toShare(title, content, url);
    }

    public void showCommentSuccess(Comment comment) {

    }

    public void showCommentError(String message) {
        SimplexToast.show(mContext, message);
    }


}
