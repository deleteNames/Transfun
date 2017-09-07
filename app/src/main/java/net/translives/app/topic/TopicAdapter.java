package net.translives.app.topic;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.translives.app.OSCApplication;
import net.translives.app.R;
import net.translives.app.base.adapter.BaseGeneralRecyclerAdapter;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Topic;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;

import java.util.List;

public class TopicAdapter extends BaseGeneralRecyclerAdapter<Topic> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {


    public TopicAdapter(Callback callback, int mode ) {
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
        return new TopicViewHolder(mInflater.inflate(R.layout.item_list_topic, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Topic item, int position) {
        TopicViewHolder vh = (TopicViewHolder) holder;

        Resources resources = mContext.getResources();

        vh.tv_title.setTextColor(TDevice.getColor(resources, R.color.text_title_color));

        //vh.tv_follow.setText(String.valueOf(item.getStatistics().getFollow()) + " 人关注");
        //vh.tv_active.setText(String.valueOf(item.getStatistics().getActive()) + " 篇文章");
        vh.tv_title.setText(item.getTitle());
/*
        if(item.isFollow()){
            vh.tv_follow_btn.setText("已关注");
            vh.tv_follow_btn.setTextColor(TDevice.getColor(resources,R.color.done_text_color_disabled));
            vh.tv_follow_btn.setSelected(true);
        }else{
            vh.tv_follow_btn.setText("+ 关注");
            vh.tv_follow_btn.setTextColor(TDevice.getColor(resources,(R.color.main_green)));
            vh.tv_follow_btn.setSelected(false);
        }

        vh.tv_follow_btn.setOnClickListener(onClickListener);
        vh.tv_follow_btn.setTag(position);
*/

        if(item.getStatistics().getUpdate() > 0){
            vh.tv_update.setText("有 "+item.getStatistics().getUpdate()+ " 条更新");
            vh.tv_update.setVisibility(View.VISIBLE);
        }else{
            vh.tv_update.setVisibility(View.GONE);
        }


        List<Topic.Image> images = item.getImages();
        if (images != null && images.size() > 0) {
            mCallBack.getImgLoader()
                    .load(images.get(0).getHref())
                    .placeholder(R.mipmap.ic_split_graph)
                    .into(vh.iv_pic);
        } else {
            vh.iv_pic.setImageResource(R.mipmap.ic_split_graph);
        }
    }

    private static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;//,  tv_follow,tv_active;
        TextView tv_update;
        ImageView iv_pic;
        //Button tv_follow_btn;

        public TopicViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            //tv_update = (TextView) itemView.findViewById(R.id.tv_update);
            //tv_follow = (TextView) itemView.findViewById(R.id.tv_follow);
            //tv_follow_btn = (Button) itemView.findViewById(R.id.tv_follow_btn);
            //tv_active = (TextView) itemView.findViewById(R.id.tv_active);
            iv_pic = (ImageView) itemView.findViewById(R.id.iv_pic);
        }
    }
}
