package net.translives.app.topic;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.comment.Comment;
import net.translives.app.behavior.CommentBar;
import net.translives.app.user.OtherUserHomeActivity;
import net.translives.app.util.StringUtils;
import net.translives.app.widget.PortraitView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by fei
 * on 2016/11/21.
 * desc:
 */
public class CommentAdapter extends BaseRecyclerAdapter<Comment> {

    private static final int VIEW_TYPE_DATA_FOOTER = 2000;

    public CommentAdapter(final Context context,  int mode) {
        super(context, mode);
    }

    @Override
    protected CommentHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
        if (holder instanceof CommentHolder) {
            ((CommentHolder) holder).addComment(item);
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

    private boolean isRealDataFooter(int position) {
        return getIndex(position) == getCount() - 1;
    }

    static class CommentHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_avatar)
        PortraitView mIvAvatar;

        @Bind(R.id.tv_name)
        TextView mName;
        @Bind(R.id.tv_pub_date)
        TextView mPubDate;


        @Bind(R.id.tv_content)
        TextView mTweetTextView;

        CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /**
         * add comment
         *
         * @param comment comment
         */
        @SuppressLint("DefaultLocale")
        void addComment( final Comment comment) {

            mIvAvatar.setup(comment.getAuthor());
            String name = comment.getAuthor().getName();
            if (TextUtils.isEmpty(name))
                name = mName.getResources().getString(R.string.martian_hint);
            mName.setText(name);
            mPubDate.setText(String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));

            mTweetTextView.setText(comment.getContent());
        }


    }


}
