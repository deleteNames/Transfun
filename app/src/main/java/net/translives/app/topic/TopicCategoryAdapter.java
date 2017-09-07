package net.translives.app.topic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.media.ImageLoaderListener;
import net.translives.app.bean.Topic;

import java.util.List;


/**
 * 图片列表界面适配器
 */
public class TopicCategoryAdapter extends BaseRecyclerAdapter<Topic> {
    private ImageLoaderListener loader;

    public TopicCategoryAdapter(Context context, ImageLoaderListener loader) {
        super(context, NEITHER);
        this.loader = loader;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof ImageViewHolder) {
            ImageViewHolder h = (ImageViewHolder) holder;
            Glide.clear(h.mImageView);
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ImageViewHolder(mInflater.inflate(R.layout.item_list_topic, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Topic item, int position) {
        if (item.getId() != 0) {
            ImageViewHolder h = (ImageViewHolder) holder;

            List<Topic.Image> images = item.getImages();
            loader.displayImage(h.mImageView, images.get(0).getHref());

            h.mTitle.setText(item.getTitle());
        }
    }


    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTitle;

        ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
            mTitle= (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
