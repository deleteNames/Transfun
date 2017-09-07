package net.translives.app.question;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
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
import net.translives.app.bean.Follow;
import net.translives.app.bean.Question;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.comment.CommentView;
import net.translives.app.comment.CommentsActivity;
import net.translives.app.interf.OnCommentClickListener;
import net.translives.app.cache.ReadedIndexCacheManager;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TLog;
import net.translives.app.widget.OWebView;
import net.translives.app.widget.PortraitView;
import net.translives.app.widget.ScreenView;
import net.translives.app.widget.SimplexToast;
import net.translives.app.widget.DetailPicturesLayout;
//import net.translives.app.widget.FlowLayout;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class QuestionDetailFragment extends BaseFragment implements  View.OnClickListener {
    @Bind(R.id.tv_title)
    TextView mTextTitle;
    @Bind(R.id.iv_user_avatar)
    PortraitView mAvatar;
    @Bind(R.id.tv_nick)
    TextView mTextAuthor;
    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;
    //@Bind(R.id.tv_content)
    //TextView mTextContent;

    @Bind(R.id.iv_follow)
    ImageView mImageFollow;

    @Bind(R.id.iv_follow_txt)
    TextView mFollowTxt;

    //@Bind(R.id.fl_lab)
    //FlowLayout mFlowLayout;

    //@Bind(R.id.pics_layout)
    //DetailPicturesLayout mLayoutGrid;

    protected OWebView mWebView;
    protected CommentView mCommentView;
    protected Question mBean;
    protected int CACHE_CATALOG;

    public static QuestionDetailFragment newInstance() {
        return new QuestionDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_question_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWebView = (OWebView) mRoot.findViewById(R.id.webView);
        mCommentView = (CommentView) mRoot.findViewById(R.id.cv_comment);
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_QUESTION;
    }

    @OnClick({ R.id.ll_follow,R.id.ll_add_answer})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_add_answer:
                AnswerPublishActivity.show(mContext, mBean);
                break;
            case R.id.ll_follow:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(mContext);
                    return;
                }
                followReverse("question",mBean.getId());
                break;
        }
    }

    @Override
    public void onDestroy() {

        mWebView.destroy();
        super.onDestroy();
    }

    public void showGetDetailSuccess(Question bean) {

        this.mBean = bean;
        if (mContext == null) return;
        mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);

        mTextTitle.setText(bean.getTitle());
        //mTextContent.setText(bean.getBody());
        mAvatar.setup(bean.getAuthor());
        mTextAuthor.setText(bean.getAuthor().getName());
        mTextPubDate.setText(StringUtils.formatSomeAgo(bean.getPubDate()));
        //mTextCommentCount.setText(String.valueOf(bean.getStatistics().getComment()));
        //mTextViewCount.setText(String.valueOf(bean.getStatistics().getView()));

        //mImageFollow.setImageResource(R.drawable.ic_fav);
        mFollowTxt.setText(bean.isFollow() ? "已关注" : "关注");

        //mLayoutGrid.setImage(bean.getImages());


        mCommentView.setTitle(String.format("%d 个回答", bean.getStatistics().getComment()));
        mCommentView.init(bean.getId(),
                OSChinaApi.COMMENT_QUESTION,
                OSChinaApi.COMMENT_NEW_ORDER,
                bean.getStatistics().getComment(),
                getImgLoader());
    }

    public void followReverse(String type, long id) {

        OSChinaApi.getFollowReverse(id, type, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                TLog.error(responseString);
                try {
                    Type type = new TypeToken<ResultBean<Follow>>() {
                    }.getType();
                    ResultBean<Follow> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        Follow collection = resultBean.getResult();
                        mBean.setFollow(collection.isFollow());
                        showFollowReverseSuccess(collection.isFollow(), collection.isFollow() ? R.string.add_follow_success : R.string.del_follow_success);

                    } else {
                        SimplexToast.show(mContext, mContext.getResources().getString(R.string.follow_faile));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    public void showFollowReverseSuccess(boolean isFav, int strId) {
        mFollowTxt.setText(isFav ? "已关注" : "关注");
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }


    public void showNetworkError(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }
}
