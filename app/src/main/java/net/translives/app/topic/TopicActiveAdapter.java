package net.translives.app.topic;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseGeneralRecyclerAdapter;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.TopicActive;
import net.translives.app.bean.comment.Image;
import net.translives.app.bean.simple.Author;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;
import net.translives.app.widget.DetailPicturesLayout;
import net.translives.app.widget.PortraitView;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TopicActiveAdapter extends BaseGeneralRecyclerAdapter<TopicActive> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {

    public TopicActiveAdapter(Callback callback, int mode ) {
        super(callback, mode);
        setOnLoadingHeaderCallBack(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderViewHolder(mHeaderView);
    }

    @Override
    public void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TopicActiveViewHolder(mInflater.inflate(R.layout.item_list_topic_active, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, TopicActive item, int position) {
        TopicActiveViewHolder vh = (TopicActiveViewHolder) holder;

        Author author = item.getAuthor();
        vh.iv_avatar.setup(author);

        vh.tv_name.setText(author.getName());

        String content = item.getBody().replaceAll("[\n\\s]+", " ");

        if (!TextUtils.isEmpty(item.getTitle())) {
            vh.tv_title.setText(item.getTitle());
            vh.tv_content.setText(content);
            vh.tv_content.setVisibility(View.VISIBLE);
        }else{
            vh.tv_title.setText(content);
            vh.tv_content.setVisibility(View.GONE);
        }

        vh.tv_comment_count.setText(String.valueOf(item.getStatistics().getComment()));
        vh.tv_like_count.setText(String.valueOf(item.getStatistics().getView()));

        Image[] images = item.getImages();
        if(images != null && images.length> 0){
            vh.mLayoutFlow.setImage(images);
            vh.mLayoutFlow.setVisibility(View.VISIBLE);
        }else{
            vh.mLayoutFlow.setVisibility(View.GONE);
        }

        vh.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));


        TopicActive.Comment[] comments = item.getComments();
        vh.commentView.initComments(comments);
    }

    public static class TopicActiveViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        PortraitView iv_avatar;
        @Bind(R.id.tv_name)
        TextView  tv_name;
        @Bind(R.id.tv_title)
        TextView  tv_title;
        @Bind(R.id.tv_content)
        TextView  tv_content;
        @Bind(R.id.tv_time)
        TextView  tv_time;
        @Bind(R.id.tv_comment_count)
        TextView  tv_comment_count;
        @Bind(R.id.tv_like_count)
        TextView  tv_like_count;
        @Bind(R.id.fl_image)
        DetailPicturesLayout mLayoutFlow;
        @Bind(R.id.comment_container)
        CommentSimpleView commentView;

        private TopicActiveViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
