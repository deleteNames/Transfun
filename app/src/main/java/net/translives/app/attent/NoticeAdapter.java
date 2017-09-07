package net.translives.app.attent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Notice;
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

public class NoticeAdapter extends BaseRecyclerAdapter<Notice> {
    public NoticeAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new CollectionViewHolder(mInflater.inflate(R.layout.item_list_notice, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Notice item, int position) {
        CollectionViewHolder h = (CollectionViewHolder) holder;

        Author user = item.getAuthor();
        TLog.error(user.toString());
        h.mAvatar.setup(user);
        h.mUserName.setText(user.getName());

        h.mType.setText(item.getAction());

        switch (item.getType()) {
            case Notice.TYPE_LIKE:
            case Notice.TYPE_REPLY:
            case Notice.TYPE_COMMENT:

                h.mContent.setText(item.getContent());

                Image[] images = item.getImages();
                if(images != null && images.length> 0){
                    h.mLayoutFlow.setImage(images);
                    h.mLayoutFlow.setVisibility(View.VISIBLE);
                }else{
                    h.mLayoutFlow.setVisibility(View.GONE);
                }

                h.mSource.setText(item.getSource().getTitle());
                h.mSource.setVisibility(View.VISIBLE);
                break;
            case Notice.TYPE_POST:

                h.mContent.setText(item.getSource().getTitle());
                h.mLayoutFlow.setVisibility(View.GONE);
                h.mSource.setVisibility(View.GONE);
                break;
        }

    }

    private class CollectionViewHolder extends RecyclerView.ViewHolder {
        private TextView mContent,mUserName, mType,mSource;
        private PortraitView mAvatar;
        private DetailPicturesLayout mLayoutFlow;
        public CollectionViewHolder(View itemView) {
            super(itemView);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            mAvatar = (PortraitView) itemView.findViewById(R.id.iv_user_avatar);
            mLayoutFlow = (DetailPicturesLayout) itemView.findViewById(R.id.fl_image);
            mSource = (TextView) itemView.findViewById(R.id.ll_source);
        }
    }
}
