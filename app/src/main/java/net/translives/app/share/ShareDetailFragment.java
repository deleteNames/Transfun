package net.translives.app.share;

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
import net.translives.app.bean.Question;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.comment.CommentView;
import net.translives.app.comment.CommentsActivity;
import net.translives.app.dialog.ShareDialog;
import net.translives.app.question.AnswerPublishActivity;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TLog;
import net.translives.app.widget.OWebView;
import net.translives.app.widget.PortraitView;
import net.translives.app.widget.SimplexToast;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

//import net.translives.app.widget.FlowLayout;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class ShareDetailFragment extends BaseFragment implements  View.OnClickListener {
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


    @Bind(R.id.iv_fav)
    ImageView mImageFav;
    @Bind(R.id.iv_fav_txt)
    TextView mFavTxt;

    @Bind(R.id.tv_comment_count)
    TextView mTextCommentCount;

    //@Bind(R.id.fl_lab)
    //FlowLayout mFlowLayout;

    //@Bind(R.id.pics_layout)
    //DetailPicturesLayout mLayoutGrid;

    protected OWebView mWebView;
    protected Question mBean;

    protected ShareDialog mAlertDialog;

    public static ShareDetailFragment newInstance() {
        return new ShareDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWebView = (OWebView) mRoot.findViewById(R.id.webView);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({ R.id.ll_fav,R.id.ll_comment,R.id.ll_share})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_comment:
                CommentsActivity.show(getActivity(), mBean.getId(), OSChinaApi.COMMENT_SHARE, OSChinaApi.COMMENT_NEW_ORDER);
                break;
            case R.id.ll_fav:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(mContext);
                    return;
                }
                favReverse("share",mBean.getId());
                break;

            case R.id.ll_share:
                toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
                break;
        }
    }

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

        mTextCommentCount.setText(String.format("评论(%d)", bean.getStatistics().getComment()));

        mImageFav.setImageResource(R.drawable.ic_fav);
        mFavTxt.setText(bean.isFavorite() ? "已收藏" : "收藏");

        //mLayoutGrid.setImage(bean.getImages());
    }

    public void favReverse(String type, long id) {

        OSChinaApi.getFavReverse(id, type, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                TLog.error(responseString);
                try {
                    Type type = new TypeToken<ResultBean<Collection>>() {
                    }.getType();
                    ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        Collection collection = resultBean.getResult();
                        mBean.setFavorite(collection.isFavorite());
                        showFavReverseSuccess(collection.isFavorite(), collection.isFavorite() ? R.string.add_favorite_success : R.string.del_favorite_success);

                    } else {
                        SimplexToast.show(mContext, "收藏失败");
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
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }


    public void showNetworkError(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
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
            mAlertDialog = new ShareDialog(getActivity(), mBean.getId())
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)//如果没有图片，即url为null，直接加入app默认分享icon
                    .url(url).with();
        }
        mAlertDialog.show();

        return true;
    }
}
