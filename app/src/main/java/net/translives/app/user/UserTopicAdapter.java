package net.translives.app.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.comment.Image;
import net.translives.app.util.StringUtils;
import net.translives.app.widget.DetailPicturesLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class UserTopicAdapter extends BaseRecyclerAdapter<TopicActive> {

    public UserTopicAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_user_topic, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, TopicActive item, int position) {
        ViewHolder holder = (ViewHolder) h;

        holder.mCategory.setText(item.getTopic().getTitle());

        if(item.getTitle() == ""){
            holder.mViewTitle.setText(item.getBody());
            holder.mViewContent.setVisibility(View.GONE);
        }else{
            holder.mViewTitle.setText(item.getTitle());
            holder.mViewContent.setText(item.getBody());
        }

        Image[] images = item.getImages();
        if(images != null && images.length> 0){
            holder.mLayoutFlow.setImage(images);
            holder.mLayoutFlow.setVisibility(View.VISIBLE);
        }else{
            holder.mLayoutFlow.setVisibility(View.GONE);
        }

        holder.mTime.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        holder.mView.setText(String.valueOf(item.getStatistics().getView()) + " 浏览");
        holder.mReply.setText(String.valueOf(item.getStatistics().getComment()) + " 评论");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.tv_category)
        TextView mCategory;

        @Bind(R.id.tv_title)
        TextView mViewTitle;
        @Bind(R.id.tv_content)
        TextView mViewContent;
        @Bind(R.id.tv_time)
        TextView mTime;
        @Bind(R.id.tv_view_count)
        TextView mView;
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
