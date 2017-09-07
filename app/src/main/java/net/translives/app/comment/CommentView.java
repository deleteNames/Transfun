package net.translives.app.comment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppContext;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.AppOperator;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.Answer;

import net.translives.app.user.OtherUserHomeActivity;
import net.translives.app.util.DialogHelper;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;

import net.translives.app.util.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CommentView extends LinearLayout implements View.OnClickListener {

    private long mId;
    private int mType;
    private TextView mTitle;
    private TextView mSeeMore;
    private LinearLayout mLayComments;

    public CommentView(Context context) {
        super(context);
        init();
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lay_detail_comment_layout, this, true);
        mTitle = (TextView) findViewById(R.id.tv_blog_detail_comment);
        mLayComments = (LinearLayout) findViewById(R.id.lay_detail_comment);
        mSeeMore = (TextView) findViewById(R.id.tv_see_more_comment);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    /**
     * @return TypeToken
     */
    Type getCommentType() {
        return new TypeToken<ResultBean<PageBean<Answer>>>() {
        }.getType();
    }

    public void init(long id, final int type, int order, final int commentCount, final RequestManager imageLoader) {
        this.mId = id;
        this.mType = type;

        mSeeMore.setVisibility(View.VISIBLE);
        setVisibility(GONE);

        OSChinaApi.getAnswers(id, type,order, "", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
               // try {

                    ResultBean<PageBean<Answer>> resultBean = AppOperator.createGson().fromJson(responseString, getCommentType());
                    if (resultBean.isSuccess()) {

                        List<Answer> comments = resultBean.getResult().getItems();

                        int size = comments.size();
                        TLog.log("comment size: " + size);

                        /*
                        if (size > commentCount ) {
                            mSeeMore.setVisibility(VISIBLE);
                            mSeeMore.setOnClickListener(CommentView.this);
                        }*/

                        Answer[] array = CollectionUtil.toArray(comments, Answer.class);
                        initComment(array, imageLoader);
                    }

             //   } catch (Exception e) {
            //        onFailure(statusCode, headers, responseString, e);
            //    }
            }
        });
    }

    private void initComment(final Answer[] comments, final RequestManager imageLoader) {
        if (mLayComments != null)
            mLayComments.removeAllViews();
        if (comments != null && comments.length > 0) {
            if (getVisibility() != VISIBLE) {
                setVisibility(VISIBLE);
            }
            TLog.log("--- initComment ---");
            for (int i = 0, len = comments.length; i < len; i++) {
                final Answer comment = comments[i];
                if (comment != null) {
                    final ViewGroup lay = insertComment(true, comment, imageLoader);
                    lay.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           QuesAnswerDetailActivity.show(lay.getContext(), comment, mId, mType);
                        }
                    });

                    mLayComments.addView(lay);
                    if (i == len - 1) {
                        lay.findViewById(R.id.line).setVisibility(GONE);
                    } else {
                        lay.findViewById(R.id.line).setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            TLog.log("--- !comments || comments.length = 0 ---");
            setVisibility(View.GONE);
        }
    }


    @SuppressLint("DefaultLocale")
    private ViewGroup insertComment(final boolean first, final Answer comment, final RequestManager imageLoader) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.item_list_answer, null, false);

        ImageView ivAvatar = (ImageView) lay.findViewById(R.id.iv_avatar);
        imageLoader.load(comment.getAuthor().getPortrait()).error(R.mipmap.widget_default_face)
                .into(ivAvatar);
        ivAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherUserHomeActivity.show(getContext(), comment.getAuthor().getId());
            }
        });
        final TextView tvVoteCount = (TextView) lay.findViewById(R.id.tv_vote_count);
        tvVoteCount.setText(String.format("%d 个赞同", comment.getVoteUp()));
        tvVoteCount.setVisibility(View.VISIBLE);

        //final ImageView ivVoteStatus = (ImageView) lay.findViewById(R.id.iv_vote_state);
        //ivVoteStatus.setVisibility(View.VISIBLE);

        //ivVoteStatus.setImageResource(R.mipmap.ic_thumb_normal);
        //ivVoteStatus.setTag(null);

        final TextView tvCommentCount = (TextView) lay.findViewById(R.id.tv_comment_count);
        tvCommentCount.setText(String.format("%d 个回复",comment.getReplyCount()));
        tvCommentCount.setVisibility(View.VISIBLE);

        //final ImageView ivCommentStatus = (ImageView) lay.findViewById(R.id.iv_comment);
        //ivCommentStatus.setVisibility(View.VISIBLE);


        String name = comment.getAuthor().getName();

        ((TextView) lay.findViewById(R.id.tv_name)).setText(name);

        ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));

        TextView content = ((TextView) lay.findViewById(R.id.tv_content));
        content.setText(comment.getContent());

        if (!first) {
            addView(lay, 0);
        }

        return lay;
    }

    @Override
    public void onClick(View v) {
        if (mId != 0 && mType != 0)
            CommentsActivity.show((AppCompatActivity) getContext(), mId, mType, OSChinaApi.COMMENT_NEW_ORDER);
    }
}



