package net.translives.app.attent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Attent;
import net.translives.app.bean.simple.Author;
import net.translives.app.util.StringUtils;
import net.translives.app.widget.PortraitView;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class AttentAdapter extends BaseRecyclerAdapter<Attent> {
    public AttentAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new CollectionViewHolder(mInflater.inflate(R.layout.item_list_attent, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Attent item, int position) {
        CollectionViewHolder h = (CollectionViewHolder) holder;
        h.mContent.setText(item.getContent());


        Author user = item.getAuthor();
        h.mUserName.setText(user != null ? user.getName() : "匿名");
        h.mAvatar.setup(user);
        h.mTime.setText(StringUtils.formatSomeAgo(item.getPubDate()));
    }

    private class CollectionViewHolder extends RecyclerView.ViewHolder {
        private TextView mTime, mContent,mUserName;
        private PortraitView mAvatar;
        public CollectionViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.tv_content);
            mTime = (TextView) itemView.findViewById(R.id.tv_time);
            mUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            mAvatar = (PortraitView) itemView.findViewById(R.id.iv_user_avatar);
        }
    }
}
