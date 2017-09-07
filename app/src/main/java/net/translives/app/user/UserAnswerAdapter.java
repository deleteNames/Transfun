package net.translives.app.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Answer;
import net.translives.app.bean.comment.Comment;
import net.translives.app.bean.comment.Image;
import net.translives.app.util.StringUtils;
import net.translives.app.widget.DetailPicturesLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
//import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thanatos on 16/8/17.
 */
public class UserAnswerAdapter extends BaseRecyclerAdapter<Answer> {

    public UserAnswerAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_user_answer, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, Answer item, int position) {
        ViewHolder holder = (ViewHolder) h;

        holder.mViewTitle.setText(item.getQuestionTitle());
        holder.mViewContent.setText(item.getContent());
        Image[] images = item.getImages();
        if(images != null && images.length> 0){
            holder.mLayoutFlow.setImage(images);
            holder.mLayoutFlow.setVisibility(View.VISIBLE);
        }else{
            holder.mLayoutFlow.setVisibility(View.GONE);
        }

        holder.mTime.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        holder.mReply.setText(String.format("%d 回复",item.getReplyCount()));
        holder.mVote.setText(String.format("%d 赞同",item.getVoteUp()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_question_title)
        TextView mViewTitle;
        @Bind(R.id.tv_answer_content)
        TextView mViewContent;
        @Bind(R.id.tv_time)
        TextView mTime;
        @Bind(R.id.tv_vote_count)
        TextView mVote;
        @Bind(R.id.tv_comment_count)
        TextView mReply;

        @Bind(R.id.fl_image)
        DetailPicturesLayout mLayoutFlow;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
