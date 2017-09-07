package net.translives.app.topic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.bean.TopicActive;
import net.translives.app.util.TLog;

public class CommentSimpleView extends LinearLayout{

    public CommentSimpleView(Context context) {
        super(context);
        init();
    }

    public CommentSimpleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentSimpleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.lay_topic_comments_simple, this, true);
    }


    public void loadMore(){

    }

    public void initComments(final TopicActive.Comment[] comments) {

        LinearLayout mLayComments = (LinearLayout) findViewById(R.id.lay_detail_comment);
        mLayComments.removeAllViews();
        if (comments != null && comments.length > 0) {

            for (final TopicActive.Comment comment : comments) {
                final ViewGroup lay = insertComment(comment);
                mLayComments.addView(lay);
            }
        }

        TextView mSeeMore = (TextView) findViewById(R.id.tv_see_more_comment);

        if(comments != null && comments.length >= 0){
            if (comments.length >= 3) {
                mSeeMore.setVisibility(View.VISIBLE);
                mSeeMore.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadMore();
                    }
                });
            }else{
                mSeeMore.setVisibility(View.GONE);
            }
        }else{
            setVisibility(View.GONE);
        }
    }

    @SuppressLint("DefaultLocale")
    private ViewGroup insertComment(final TopicActive.Comment comment) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup lay = (ViewGroup) inflater.inflate(R.layout.lay_topic_comment_item, null, false);

        String str ="<font color='#39a7e3'>"+comment.getAuthorFrom()+"</font>";
        if (comment.getReplyTo() != ""){
            str+="回复<font color='#39a7e3'>"+comment.getReplyTo()+"</font>";
        }

        str+=": "+comment.getContent();

        ((TextView)lay.findViewById(R.id.tv_content)).setText(Html.fromHtml(str));

        return lay;
    }
}



