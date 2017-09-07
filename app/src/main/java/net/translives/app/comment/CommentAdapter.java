package net.translives.app.comment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppContext;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.AppOperator;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.comment.Vote;
import net.translives.app.behavior.CommentBar;
import net.translives.app.comment.CommentReferView;
import net.translives.app.user.OtherUserHomeActivity;
import net.translives.app.util.DialogHelper;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.widget.TweetTextView;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by fei
 * on 2016/11/21.
 * desc:
 */
public class CommentAdapter extends BaseRecyclerAdapter<Comment> {

    private static final int VIEW_TYPE_DATA_FOOTER = 2000;
    private long mSourceId;

    private int mType;
    private CommentBar delegation;
    private RequestManager mRequestManager;

    public CommentAdapter(final Context context, RequestManager requestManager, int mode) {
        super(context, mode);
        this.mRequestManager = requestManager;
    }

    @Override
    protected CommentHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list_comment, parent, false);
        return new CommentHolder(view, delegation);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
        if (holder instanceof CommentHolder) {
            ((CommentHolder) holder).addComment(mSourceId, mType, item, mRequestManager);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);
        if (type == VIEW_TYPE_NORMAL && isRealDataFooter(position)) {
            return VIEW_TYPE_DATA_FOOTER;
        }
        return type;
    }

    public void setSourceId(long sourceId) {
        this.mSourceId = sourceId;
    }

    public void setCommentType(int Type) {
        this.mType = Type;
    }

    public void setDelegation(CommentBar delegation) {
        this.delegation = delegation;
    }

    private boolean isRealDataFooter(int position) {
        return getIndex(position) == getCount() - 1;
    }

    static class CommentHolder extends RecyclerView.ViewHolder {

        private ProgressDialog mDialog;

        @Bind(R.id.iv_avatar)
        CircleImageView mIvAvatar;

        @Bind(R.id.tv_name)
        TextView mName;
        @Bind(R.id.tv_pub_date)
        TextView mPubDate;


        @Bind(R.id.tv_content)
        TextView mTweetTextView;
        //@Bind(R.id.line)
        //View mLine;

        private CommentBar commentBar;

        CommentHolder(View itemView, CommentBar commentBar) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.commentBar = commentBar;
        }

        /**
         * add comment
         *
         * @param comment comment
         */
        @SuppressLint("DefaultLocale")
        void addComment(final long sourceId, final int commentType, final Comment comment, RequestManager requestManager) {

            requestManager.load(comment.getAuthor().getPortrait()).error(R.mipmap.widget_default_face).into(mIvAvatar);
            mIvAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(mIvAvatar.getContext(), comment.getAuthor().getId());
                }
            });
            String name = comment.getAuthor().getName();
            if (TextUtils.isEmpty(name))
                name = mName.getResources().getString(R.string.martian_hint);
            mName.setText(name);
            mPubDate.setText(String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));

            mTweetTextView.setText(comment.getContent());
        }


    }


}
