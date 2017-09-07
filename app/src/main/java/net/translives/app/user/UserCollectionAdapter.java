package net.translives.app.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Collection;
import net.translives.app.widget.PortraitView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class UserCollectionAdapter extends BaseRecyclerAdapter<Collection> {
    public UserCollectionAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new CollectionViewHolder(mInflater.inflate(R.layout.item_list_collection_qa, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, Collection item, int position) {
        CollectionViewHolder vh = (CollectionViewHolder) h;

        if(item.getType().equals(Collection.TYPE_NEWS)){
            vh.mTypeView.setText("资讯");
            vh.ivPortrait.setVisibility(View.GONE);
            vh.mName.setVisibility(View.GONE);
        }else{
            vh.ivPortrait.setup(item.getAuthor());
            vh.mName.setText(item.getAuthor().getName());
            if(item.getType().equals(Collection.TYPE_QUESTION)){
                vh.mTypeView.setText("的提问");
            }else if(item.getType().equals(Collection.TYPE_ANSWER)){
                vh.mTypeView.setText("的回答");
            }else if(item.getType().equals(Collection.TYPE_SHARE)){
                vh.mTypeView.setText("的分享");
            }
        }

        vh.mViewTitle.setText(item.getTitle());

        if (!TextUtils.isEmpty(item.getContent())) {
            String content = item.getContent().replaceAll("[\n\\s]+", " ");
            vh.mViewContent.setText(content);
        }else{
            vh.mViewContent.setVisibility(View.GONE);
        }
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        PortraitView ivPortrait;

        @Bind(R.id.tv_title)
        TextView mViewTitle;
        @Bind(R.id.tv_content)
        TextView mViewContent;
        @Bind(R.id.tv_qa_type)
        TextView mTypeView;
        @Bind(R.id.tv_name)
        TextView mName;

        public CollectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
