package net.translives.app.news;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.translives.app.OSCApplication;
import net.translives.app.R;
import net.translives.app.base.adapter.BaseGeneralRecyclerAdapter;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.News;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;

import java.util.List;

/**
 * 新版新闻订阅栏目
 * Created by haibin
 * on 2016/10/26.
 */

public class NewsAdapter extends BaseGeneralRecyclerAdapter<News> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {
    private OSCApplication.ReadState mReadState;

    public NewsAdapter(Callback callback, int mode) {
        super(callback, mode);
        mReadState = OSCApplication.getReadState("news_list");
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
        return new NewsViewHolder(mInflater.inflate(R.layout.item_list_news, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, News item, int position) {
        NewsViewHolder vh = (NewsViewHolder) holder;

        Resources resources = mContext.getResources();

        if (mReadState.already(item.getKey())) {
            vh.tv_title.setTextColor(TDevice.getColor(resources, R.color.text_desc_color));
        } else {
            vh.tv_title.setTextColor(TDevice.getColor(resources, R.color.text_title_color));
        }

        vh.tv_view.setText(String.valueOf(item.getStatistics().getView()) + " 浏览");
        vh.tv_comment.setText(String.valueOf(item.getStatistics().getComment()) + " 评论");
        vh.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate().trim()));
        vh.tv_title.setText(item.getTitle());

        List<News.Image> images = item.getImages();
        if (images != null && images.size() > 0) {
            mCallBack.getImgLoader()
                    .load(images.get(0).getHref())
                    .placeholder(R.mipmap.ic_split_graph)
                    .into(vh.iv_pic);
        } else {
            vh.iv_pic.setImageResource(R.mipmap.ic_split_graph);
        }
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_time, tv_view,tv_comment;
        ImageView iv_pic;

        public NewsViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_view = (TextView) itemView.findViewById(R.id.tv_view);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            iv_pic = (ImageView) itemView.findViewById(R.id.iv_pic);
        }
    }
}
