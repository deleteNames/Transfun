package net.translives.app.comment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppContext;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.AppOperator;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.Answer;
import net.translives.app.bean.Collection;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.comment.Reply;
import net.translives.app.bean.simple.About;
import net.translives.app.behavior.CommentBar;
//import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
//import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
//import net.oschina.app.improve.tweet.service.TweetPublishService;
//import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.translives.app.dialog.ShareDialog;
import net.translives.app.util.DialogHelper;
//import net.translives.app.widget.IdentityView;
import net.translives.app.util.HTMLUtil;
import net.translives.app.util.TLog;
import net.translives.app.widget.OWebView;
import net.translives.app.widget.PortraitView;
import net.translives.app.interf.OnKeyArrivedListenerAdapter;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.widget.SimplexToast;
//import net.translives.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos
 * on 16/6/16.
 * change by fie
 * on 16/11/17
 * desc:问答,活动的评论详情（相当于帖子）,可以对评论进行顶踩操作
 */
public class QuesAnswerDetailActivity extends BaseBackActivity {

    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String BUNDLE_ARTICLE_KEY = "BUNDLE_ARTICLE_KEY";
    public static final String BUNDLE_TYPE = "bundle_comment_type";

    @Bind(R.id.iv_portrait)
    PortraitView ivPortrait;
    //@Bind(R.id.identityView)
    //IdentityView identityView;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.iv_vote_up)
    ImageView ivVoteUp;

    @Bind(R.id.tv_up_count)
    TextView tvVoteCount;
    @Bind(R.id.webview)
    OWebView mWebView;
    @Bind(R.id.tv_comment_count)
    TextView tvCmnCount;
    @Bind(R.id.layout_container)
    LinearLayout mLayoutContainer;
    @Bind(R.id.layout_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.layout_scroll)
    NestedScrollView mScrollView;

    private long sid;
    private Answer comment;
    private int mType;

    private Dialog mVoteDialog;
    private Reply reply;
    private View mVoteDialogView;
    private List<Reply> replies = new ArrayList<>();

    private CommentBar mDelegation;
    private TextHttpResponseHandler onSendCommentHandler;
    private View.OnClickListener onReplyButtonClickListener;

    protected ShareDialog mAlertDialog;

    /**
     * @param context context
     * @param comment comment
     * @param sid     文章id
     */
    public static void show(Context context, Answer comment, long sid, int type) {
        Intent intent = new Intent(context, QuesAnswerDetailActivity.class);
        intent.putExtra(BUNDLE_KEY, comment);
        intent.putExtra(BUNDLE_ARTICLE_KEY, sid);
        intent.putExtra(BUNDLE_TYPE, type);
        context.startActivity(intent);
    }

    public static void show(Context context, long sid, boolean isFav) {
        Intent intent = new Intent(context, QuesAnswerDetailActivity.class);

        Answer bean = new Answer();
        bean.setId(sid);
        bean.setFavorite(isFav);
        intent.putExtra(BUNDLE_KEY, bean);
        intent.putExtra(BUNDLE_ARTICLE_KEY, sid);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        comment = (Answer) getIntent().getSerializableExtra(BUNDLE_KEY);
        sid = getIntent().getLongExtra(BUNDLE_ARTICLE_KEY, 0);
        mType = getIntent().getIntExtra(BUNDLE_TYPE, OSChinaApi.COMMENT_QUESTION);
        return !(comment == null || comment.getId() <= 0) && super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_post_answer_detail;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.back_hint);
        }
    }

    @SuppressWarnings("deprecation")
    protected void initWidget() {

        mDelegation = CommentBar.delegation(this, mCoordinatorLayout);

        mDelegation.setCommentHint("我要回复");
        mDelegation.getBottomSheet().getEditText().setHint("我要回复");
        mDelegation.getBottomSheet().getEditText().setText("");

        //mDelegation.hideFav();
        //mDelegation.hideShare();

        mDelegation.setFavListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(QuesAnswerDetailActivity.this);
                    return;
                }
                favReverse("answer",comment.getId());
            }
        });

        mDelegation.setShareListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShare(comment.getQuestionTitle(), comment.getContent(), comment.getHref());
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));
/*
        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    Intent intent = new Intent(QuesAnswerDetailActivity.this, UserSelectFriendsActivity.class);
                    startActivityForResult(intent, TweetPublishFragment.REQUEST_CODE_SELECT_FRIENDS);
                } else
                    LoginActivity.show(QuesAnswerDetailActivity.this);
            }
        });
*/
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mDelegation.getBottomSheet().getCommentText();
                if (TextUtils.isEmpty(content.replaceAll("[ \\s\\n]+", ""))) {
                    Toast.makeText(QuesAnswerDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(QuesAnswerDetailActivity.this);
                    return;
                }


                OSChinaApi.publishComment(comment.getId(), 0, 0, 0, OSChinaApi.COMMENT_QUESTION, content, onSendCommentHandler);
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (reply == null) return false;
                    reply = null;
                    mDelegation.setCommentHint("我要回复");
                    mDelegation.getBottomSheet().getEditText().setHint("我要回复");
                }
                return false;
            }
        });


    }

    private void fillWebView() {
        if (TextUtils.isEmpty(comment.getContent())) return;
        if (mWebView != null)
            mWebView.loadDetailDataAsync(comment.getContent(), new Runnable() {
                @Override
                public void run() {

                }
            });
    }

    @SuppressWarnings("deprecation")
    private void appendComment(int i, Reply reply) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_list_comment, mLayoutContainer, false);
        QuestionCommentAdapter.CommentHolderView holder = new QuestionCommentAdapter.CommentHolderView(view);
        holder.tvName.setText(reply.getAuthor().getName());
        holder.ivPortrait.setup(reply.getAuthor());
        //holder.identityView.setup(reply.getAuthor());
        holder.tvTime.setText( StringUtils.formatSomeAgo(reply.getPubDate()));
        CommentsUtil.formatHtml(getResources(), holder.tvContent, reply.getContent());
        //holder.btnReply.setTag(reply);
        //holder.btnReply.setOnClickListener(getOnReplyButtonClickListener());
        mLayoutContainer.addView(view, 0);
    }
/*
    private View.OnClickListener getOnReplyButtonClickListener() {
        if (onReplyButtonClickListener == null) {
            onReplyButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reply reply = (Reply) v.getTag();

                    mDelegation.setCommentHint("回复 @" + reply.getAuthor().getName() + " : ");

                    QuesAnswerDetailActivity.this.reply = reply;
                }
            };
        }
        return onReplyButtonClickListener;
    }
    */

    protected void initData() {
        onSendCommentHandler = new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDelegation == null) return;
                mDelegation.getBottomSheet().dismiss();
                mDelegation.setCommitButtonEnable(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToastShort("评论失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Reply> result = AppOperator.createGson().fromJson(
                        responseString,
                        new TypeToken<ResultBean<Reply>>() {
                        }.getType()
                );
                if (result.isSuccess()) {
                    replies.add(result.getResult());
                    tvCmnCount.setText(replies.size() + " 回复");
                    reply = null;
                    mDelegation.setCommentHint("我要回复");
                    mDelegation.getBottomSheet().getEditText().setHint("我要回复");
                    mDelegation.getBottomSheet().getEditText().setText("");
                    mDelegation.getBottomSheet().getBtnCommit().setTag(null);
                    appendComment(replies.size() - 1, result.getResult());
                } else {
                    AppContext.showToastShort(result.getMessage());
                }
                mDelegation.getBottomSheet().dismiss();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mDelegation == null) return;
                mDelegation.setCommitButtonEnable(true);
                mDelegation.getBottomSheet().dismiss();
            }
        };

        OSChinaApi.getAnswerDetail(comment.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String respStr, Throwable throwable) {
                AppContext.showToastShort("请求失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String respStr) {
                ResultBean<Answer> result = AppOperator.createGson().fromJson(respStr,
                        new TypeToken<ResultBean<Answer>>() {
                        }.getType());
                if (result.isSuccess()) {
                    Answer cmn = result.getResult();
                    if (cmn != null && cmn.getId() > 0) {
                        showGetDetailSuccess(cmn);
                        return;
                    }
                }
                AppContext.showToastShort("请求失败");
            }
        });
    }

    public void showGetDetailSuccess(Answer bean) {
        comment = bean;
        // portrait
        ivPortrait.setup(bean.getAuthor());
        //identityView.setup(comment.getAuthor().getIdentity());
        // nick
        tvNick.setText(bean.getAuthor().getName());

        // publish time
        if (!TextUtils.isEmpty(bean.getPubDate()))
            tvTime.setText(StringUtils.formatSomeAgo(bean.getPubDate()));

        // vote state
        if (bean.getVoteState() == 1) {
            ivVoteUp.setSelected(true);
        }

        // vote count
        tvVoteCount.setText(String.valueOf(bean.getVoteUp()));

        tvCmnCount.setText( bean.getReply().length + " 回复");

        mDelegation.setFavDrawable(bean.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);

        Reply[] reply = bean.getReply();
        if (reply != null) {
            mLayoutContainer.removeAllViews();
            replies.clear();
            Collections.addAll(replies, bean.getReply());
            Collections.reverse(replies); // 反转集合, 最新的评论在集合后面
            for (int i = 0; i < replies.size(); i++) {
                appendComment(i, replies.get(i));
            }
        }

        fillWebView();
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.layout_vote)
    void onClickVote() {

        if (ivVoteUp.isSelected()) {
            AppContext.showToastShort("你已经投票过了");
            return;
        }

        OSChinaApi.answerVote(sid, comment.getId(), 1, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToastShort("操作失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Comment> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<Comment>>() {
                        }.getType());
                if (result.isSuccess()) {
                    comment.setVoteState(result.getResult().getVoteState());
                    comment.setVoteUp((int) result.getResult().getVoteUp());

                    tvVoteCount.setText(String.valueOf(result.getResult().getVoteUp()));
                    ivVoteUp.setSelected(!ivVoteUp.isSelected());

                    AppContext.showToastShort("操作成功");
                } else {
                    AppContext.showToastShort(TextUtils.isEmpty(result.getMessage())
                            ? "操作失败" : result.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        final OWebView webView = mWebView;
        if (webView != null) {
            mWebView = null;
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAlertDialog == null)
            return;
        mAlertDialog.hideProgressDialog();
    }



    public void favReverse(String type, long id) {

        OSChinaApi.getFavReverse(id, type, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToastShort(getResources().getString(R.string.tip_network_error));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Collection>>() {
                    }.getType();
                    ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        Collection collection = resultBean.getResult();
                        comment.setFavorite(collection.isFavorite());
                        mDelegation.setFavDrawable(comment.isFavorite() ? R.drawable.ic_faved : R.drawable.ic_fav);

                        if(collection.isFavorite()){
                            AppContext.showToastShort(getResources().getString(R.string.add_favorite_success));
                        }else{
                            AppContext.showToastShort(getResources().getString(R.string.del_favorite_success));
                        }


                    } else {
                        AppContext.showToastShort("收藏失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
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
            mAlertDialog = new ShareDialog(QuesAnswerDetailActivity.this)
                    .title(title)
                    .content(content)
                    .imageUrl(imageUrl)//如果没有图片，即url为null，直接加入app默认分享icon
                    .url(url).with();
        }
        mAlertDialog.show();

        return true;
    }
}
