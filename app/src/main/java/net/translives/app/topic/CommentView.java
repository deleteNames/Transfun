package net.translives.app.topic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.bean.Answer;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.comment.Reply;
import net.translives.app.bean.simple.Author;
import net.translives.app.comment.CommentsActivity;
import net.translives.app.comment.QuesAnswerDetailActivity;
import net.translives.app.user.OtherUserHomeActivity;
import net.translives.app.util.CollectionUtil;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TLog;
import net.translives.app.widget.OWebView;
import net.translives.app.widget.PortraitView;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CommentView extends LinearLayout implements View.OnClickListener {

    private long mId;
    private int mType;
    private int mOrder;
    private TextView mSeeMore;
    private LinearLayout mLayComments;
    private PageBean<Comment> mPageBean;
    private View.OnClickListener replyClickListener;
    private View.OnClickListener subReplyClickListener;

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
        inflater.inflate(R.layout.lay_topic_comment_layout, this, true);
        mLayComments = (LinearLayout) findViewById(R.id.lay_detail_comment);
        mSeeMore = (TextView) findViewById(R.id.tv_see_more_comment);
        mSeeMore.setVisibility(View.VISIBLE);
        mSeeMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
            }
        });
    }

    /**
     * @return TypeToken
     */
    Type getCommentType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }

    public void setReplyButtonClickListener(View.OnClickListener listener){
        replyClickListener= listener;
    }

    public void setSubReplyButtonClickListener(View.OnClickListener listener){
        subReplyClickListener= listener;
    }


    public void init(long id, final int type, int order) {
        this.mId = id;
        this.mType = type;
        this.mOrder = order;

        OSChinaApi.getTopicComments(id ,order,null , new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, getCommentType());
                    if (resultBean.isSuccess()) {

                        List<Comment> comments = resultBean.getResult().getItems();
                        mPageBean = resultBean.getResult();
                        Comment[] array = CollectionUtil.toArray(comments, Comment.class);
                        initComment(array,true);
                    }
            }

            @Override
            public void onFinish(){
                int totalRecord = mPageBean.getTotalResults();
                int pageSize = mPageBean.getRequestCount();
                int totalPageNum = ( totalRecord +  pageSize  - 1) / pageSize;
                TLog.error("totalRecord="+totalRecord+",pageSize="+pageSize+",totalPageNum="+totalPageNum+",getPrevPageToken="+mPageBean.getPrevPageToken()+",getNextPageToken="+mPageBean.getNextPageToken());
                if(totalPageNum <  Integer.parseInt(mPageBean.getNextPageToken())){
                    mSeeMore.setTextColor(getResources().getColor(R.color.main_gray));
                    mSeeMore.setText("没有更多");
                }else{
                    int less = totalRecord-pageSize*(Integer.parseInt(mPageBean.getPrevPageToken())+1);
                    mSeeMore.setTextColor(getResources().getColor(R.color.light_blue_500));
                    mSeeMore.setText("还有"+ less +"条评论");
                }
            }
        });
    }

    public void loadMore(){

        String page = mPageBean != null? mPageBean.getNextPageToken() : null;

        OSChinaApi.getTopicComments(mId,mOrder,page , new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, getCommentType());
                if (resultBean.isSuccess()) {

                    List<Comment> comments = resultBean.getResult().getItems();
                    mPageBean = resultBean.getResult();

                    Comment[] array = CollectionUtil.toArray(comments, Comment.class);
                    initComment(array, false);

                }
            }

            @Override
            public void onStart(){
                mSeeMore.setText("正在加载...");
            }

            @Override
            public void onFinish(){
                int totalRecord = mPageBean.getTotalResults();
                int pageSize = mPageBean.getRequestCount();
                int totalPageNum = ( totalRecord +  pageSize  - 1) / pageSize;
                TLog.error("totalRecord="+totalRecord+",pageSize="+pageSize+",totalPageNum="+totalPageNum+",getNextPageToken="+mPageBean.getNextPageToken());
                if(totalPageNum <  Integer.parseInt(mPageBean.getNextPageToken())){
                    mSeeMore.setTextColor(getResources().getColor(R.color.main_gray));
                    mSeeMore.setText("没有更多");
                }else{
                    int less = totalRecord-pageSize*(Integer.parseInt(mPageBean.getPrevPageToken())+1);
                    mSeeMore.setTextColor(getResources().getColor(R.color.light_blue_500));
                    mSeeMore.setText("还有"+ less +"条评论");
                }
            }
        });
    }

    private void initComment(final Comment[] comments, boolean clear) {
        if (mLayComments != null && clear)
            mLayComments.removeAllViews();
        if (comments != null && comments.length > 0) {

            for (int i = 0, len = comments.length; i < len; i++) {
                final Comment comment = comments[i];
                if (comment != null) {
                    final ViewGroup lay = insertComment(true, comment);
                    lay.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           //QuesAnswerDetailActivity.show(lay.getContext(), comment, mId, mType);
                        }
                    });

                    mLayComments.addView(lay);
                    lay.findViewById(R.id.line).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void refresh() {
        init(mId,mType,mOrder);
    }


    @SuppressLint("DefaultLocale")
    private ViewGroup insertComment(final boolean first, final Comment comment) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.item_list_topic_comment, null, false);

        PortraitView ivAvatar = (PortraitView) lay.findViewById(R.id.iv_avatar);
        ivAvatar.setup(comment.getAuthor());

        String name = comment.getAuthor().getName();

        ((TextView) lay.findViewById(R.id.tv_name)).setText(name);

        ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));

        OWebView mWebView = ((OWebView) lay.findViewById(R.id.tv_content));
        mWebView.loadDetailDataAsync(comment.getContent(), new Runnable(){

            @Override
            public void run() {

            }
        });

        ImageView reply_btn = (ImageView) lay.findViewById(R.id.tv_reply);
        reply_btn.setTag(comment);
        reply_btn.setOnClickListener(replyClickListener);

        LinearLayout mReplyList= (LinearLayout) lay.findViewById(R.id.reply_list);

        Reply[] replys = comment.getReply();
        if(replys != null && replys.length > 0){

            for (int i = 0, len = replys.length; i < len; i++) {
                final Reply reply = replys[i];
                if (reply != null) {
                    final ViewGroup reply_lay = insertReply(reply);
                    reply_lay.setTag(reply);
                    reply_lay.setOnClickListener(subReplyClickListener);

                    mReplyList.addView(reply_lay);
                }
            }

            mReplyList.setVisibility(VISIBLE);
        }else{
            mReplyList.setVisibility(GONE);
        }

        if (!first) {
            addView(lay, 0);
        }

        return lay;
    }


    @SuppressLint("DefaultLocale")
    private ViewGroup insertReply(final Reply reply) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.item_list_topic_reply, null, false);

        PortraitView ivAvatar = (PortraitView) lay.findViewById(R.id.iv_avatar);
        ivAvatar.setup(reply.getAuthor());

        String name = reply.getAuthor().getName();
        ((TextView) lay.findViewById(R.id.tv_name)).setText(name);

        Author toAuthor = reply.getToAuthor();

        TextView reply_to = ((TextView) lay.findViewById(R.id.tv_reply_to));
        TextView reply_label = ((TextView) lay.findViewById(R.id.tv_reply_label));
        if (toAuthor != null){
            reply_label.setVisibility(VISIBLE);
            reply_label.setText("回复");
            reply_to.setVisibility(VISIBLE);
            reply_to.setText(toAuthor.getName());
        }else{
            reply_label.setVisibility(GONE);
            reply_label.setText("");
            reply_to.setVisibility(GONE);
            reply_to.setText("");
        }

        ((TextView) lay.findViewById(R.id.tv_pub_date)).setText(
                String.format("%s", StringUtils.formatSomeAgo(reply.getPubDate())));

        TextView content = ((TextView) lay.findViewById(R.id.tv_content));
        content.setText(reply.getContent());

        return lay;
    }

    @Override
    public void onClick(View v) {
        //if (mId != 0 && mType != 0)
        //    CommentsActivity.show((AppCompatActivity) getContext(), mId, mType, OSChinaApi.COMMENT_NEW_ORDER);
    }
}



