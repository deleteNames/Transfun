package net.translives.app.event;

import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.fragments.BaseFragment;
import net.translives.app.bean.Event;
import net.translives.app.util.TLog;
import net.translives.app.widget.ScreenView;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailFragment extends BaseFragment implements  View.OnClickListener {

    @Bind(R.id.iv_event_img)
    ImageView mImageEvent;

    @Bind(R.id.tv_event_title)
    TextView mTextTitle;

    @Bind(R.id.tv_event_cost_desc)
    TextView mTextCostDesc;

    @Bind(R.id.tv_event_status)
    TextView mTextStatus;

    @Bind(R.id.tv_event_start_date)
    TextView mTextStartDate;

    @Bind(R.id.tv_event_location)
    TextView mTextLocation;

    protected int CACHE_CATALOG;
    protected NestedScrollView mViewScroller;

    public static EventDetailFragment newInstance() {
        return new EventDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mViewScroller = (NestedScrollView) mRoot.findViewById(R.id.lay_nsv);
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_EVENT;
    }

    @SuppressLint("SetTextI18n")
    public void showGetDetailSuccess(Event bean) {

        mTextTitle.setText(bean.getTitle());
        TLog.log(bean.getTitle());
    }

    @Override
    public void onClick(View v) {

    }
}
