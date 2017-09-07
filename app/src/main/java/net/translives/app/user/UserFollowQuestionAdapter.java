package net.translives.app.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Question;
import net.translives.app.util.StringUtils;
import net.translives.app.widget.PortraitView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos on 16/8/17.
 */
public class UserFollowQuestionAdapter extends BaseRecyclerAdapter<Question> {

    public UserFollowQuestionAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_user_follow_question, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, Question item, int position) {
        ViewHolder holder = (ViewHolder) h;

        holder.ivPortrait.setup(item.getAuthor());
        holder.mName.setText(item.getAuthor().getName());
        holder.mTypeView.setText("的提问");
        holder.mViewTitle.setText(item.getTitle());
        holder.mContent.setText(item.getBody());
        holder.mViewInfoCmm.setText(String.format("%d 回答",item.getStatistics().getComment()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        PortraitView ivPortrait;

        @Bind(R.id.tv_qa_type)
        TextView mTypeView;
        @Bind(R.id.tv_name)
        TextView mName;

        @Bind(R.id.tv_question_title)
        TextView mViewTitle;

        @Bind(R.id.tv_content)
        TextView mContent;
        @Bind(R.id.tv_comment_count)
        TextView mViewInfoCmm;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
