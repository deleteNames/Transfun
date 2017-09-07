package net.translives.app.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Notice;
import net.translives.app.bean.TopicComment;
import net.translives.app.bean.comment.Image;
import net.translives.app.bean.simple.Author;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TLog;
import net.translives.app.widget.DetailPicturesLayout;
import net.translives.app.widget.PortraitView;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class UserCommentAdapter extends BaseRecyclerAdapter<TopicComment> {
    public UserCommentAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new CollectionViewHolder(mInflater.inflate(R.layout.item_list_user_topic_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, TopicComment item, int position) {
        CollectionViewHolder h = (CollectionViewHolder) holder;

        h.mCategory.setText(item.getActive().getTopic().getTitle());
        h.mContent.setText(item.getContent());

        Image[] images = item.getImages();
        if(images != null && images.length> 0){
            h.mLayoutFlow.setImage(images);
            h.mLayoutFlow.setVisibility(View.VISIBLE);
        }else{
            h.mLayoutFlow.setVisibility(View.GONE);
        }

        h.mSource.setText(item.getActive().getTitle());
        h.mSource.setVisibility(View.VISIBLE);
        h.mTime.setText(StringUtils.formatSomeAgo(item.getPubDate()));
    }

    private class CollectionViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent,mSource,mCategory,mTime;
        private DetailPicturesLayout mLayoutFlow;
        public CollectionViewHolder(View itemView) {
            super(itemView);
            mCategory = (TextView) itemView.findViewById(R.id.tv_category);
            mTime = (TextView) itemView.findViewById(R.id.tv_time);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mLayoutFlow = (DetailPicturesLayout) itemView.findViewById(R.id.fl_image);
            mSource = (TextView) itemView.findViewById(R.id.ll_source);
        }
    }
}
