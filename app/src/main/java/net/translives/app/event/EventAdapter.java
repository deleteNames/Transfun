package net.translives.app.event;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.base.adapter.BaseGeneralRecyclerAdapter;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.bean.Event;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TDevice;

public class EventAdapter extends BaseGeneralRecyclerAdapter<Event> implements BaseRecyclerAdapter.OnLoadingHeaderCallBack {

    public EventAdapter(Callback callback, int mode ) {
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
        return new EventViewHolder(mInflater.inflate(R.layout.item_list_event, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Event item, int position) {
        EventViewHolder vh = (EventViewHolder) holder;
        vh.tv_event_title.setText(item.getTitle());

        mCallBack.getImgLoader()
                .load(item.getImg())
                .placeholder(R.mipmap.event_cover_default)
                .into(vh.iv_event);

        Resources resources = mContext.getResources();

        vh.tv_event_pub_date.setText(StringUtils.getDateString(item.getEndDate()));

        switch (getExtraInt(item.getStatus())) {
            case Event.STATUS_END:
                setText(vh.tv_event_state, R.string.event_status_end, R.drawable.bg_event_end, 0x1a000000);
                setTextColor(vh.tv_event_title, TDevice.getColor(resources, R.color.light_gray));
                break;
            case Event.STATUS_ING:
                setText(vh.tv_event_state, R.string.event_status_ing, R.drawable.bg_event_ing, 0xFF24cf5f);
                break;
            case Event.STATUS_SING_UP:
                setText(vh.tv_event_state, R.string.event_status_sing_up, R.drawable.bg_event_end, 0x1a000000);
                setTextColor(vh.tv_event_title, TDevice.getColor(resources, R.color.light_gray));
                break;
        }
        vh.tv_event_type.setText(item.getType());

    }

    private void setText(TextView tv, int textRes, int bgRes, int textColor) {
        tv.setText(textRes);
        tv.setVisibility(View.VISIBLE);
        tv.setBackgroundResource(bgRes);
        tv.setTextColor(textColor);
    }

    private void setTextColor(TextView tv, int textColor) {
        tv.setTextColor(textColor);
        tv.setVisibility(View.VISIBLE);
    }

    private static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tv_event_title, tv_event_pub_date, tv_event_state, tv_event_type;
        ImageView iv_event;

        EventViewHolder(View itemView) {
            super(itemView);
            tv_event_title = (TextView) itemView.findViewById(R.id.tv_event_title);
            tv_event_state = (TextView) itemView.findViewById(R.id.tv_event_state);
            tv_event_type = (TextView) itemView.findViewById(R.id.tv_event_type);
            tv_event_pub_date = (TextView) itemView.findViewById(R.id.tv_event_pub_date);
            iv_event = (ImageView) itemView.findViewById(R.id.iv_event);
        }
    }

    private int getExtraInt(Object object) {
        return object == null ? 0 : Double.valueOf(object.toString()).intValue();
    }
}
