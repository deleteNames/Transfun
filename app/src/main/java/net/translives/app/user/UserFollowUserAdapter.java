package net.translives.app.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.User;
import net.translives.app.widget.PortraitView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserFollowUserAdapter extends BaseRecyclerAdapter<User> implements View.OnClickListener{

    private FollowBtnClickListen mCallBack;

    public UserFollowUserAdapter(Context context,FollowBtnClickListen callBack) {
        super(context, ONLY_FOOTER);

        mCallBack = callBack;
    }

    @Override
    public void onClick(View v) {
        mCallBack.FollowBtnClick(v);
    }

    public interface FollowBtnClickListen{
        public void FollowBtnClick(View view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_user_follow_user, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, User item, int position) {
        ViewHolder holder = (ViewHolder) h;

        holder.ivPortrait.setup(item);
        holder.mName.setText(item.getName());
        holder.mSuffix.setText(item.getSuffix());

        switch (item.getRelation()) {
            case User.RELATION_TYPE_BOTH:
            case User.RELATION_TYPE_ONLY_FANS_HIM:
            case User.RELATION_TYPE_ONLY_FANS_ME:
            case User.RELATION_TYPE_NULL:
                holder.mFollowBtn.setText("已关注");
                holder.mFollowBtn.setTextColor(mContext.getResources().getColor(R.color.done_text_color_disabled));
                holder.mFollowBtn.setSelected(true);
                break;
            default:
                holder.mFollowBtn.setText("+ 关注");;
                holder.mFollowBtn.setTextColor(mContext.getResources().getColor(R.color.main_green));
        }

        holder.mFollowBtn.setOnClickListener(this);
        holder.mFollowBtn.setTag(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        PortraitView ivPortrait;

        @Bind(R.id.tv_suffix)
        TextView mSuffix;
        @Bind(R.id.tv_name)
        TextView mName;

        @Bind(R.id.tv_follow_btn)
        TextView mFollowBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
