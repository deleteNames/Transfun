package net.translives.app.vote;

import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.fragments.BaseFragment;
import net.translives.app.bean.Event;
import net.translives.app.bean.Vote;
import net.translives.app.util.TLog;
import net.translives.app.widget.MatchSupportProgressBar;
import net.translives.app.widget.OWebView;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class VoteDetailFragment extends BaseFragment implements  View.OnClickListener {

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

    MatchSupportProgressBar mspb;
    protected OWebView mWebView;
    protected Vote mBean;
    protected int CACHE_CATALOG;

    protected NestedScrollView mViewScroller;

    public static VoteDetailFragment newInstance() {
        return new VoteDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_vote_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mspb = (MatchSupportProgressBar) mRoot.findViewById(R.id.progressbar_match_support);

        mspb.setOnRightTextClickListener(new MatchSupportProgressBar.OnRightTextClickListener() {

            @Override
            public void onClick(int position) {
                mspb.setPercentState(getPercentList("10","90","100"), getCheckList(position), true, true);
            }
        });

        mWebView = (OWebView) mRoot.findViewById(R.id.webView);

        mViewScroller = (NestedScrollView) mRoot.findViewById(R.id.lay_nsv);
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_EVENT;
    }

    @SuppressLint("SetTextI18n")
    public void showGetDetailSuccess(Vote bean) {
        this.mBean = bean;

        if (mContext == null) return;
        mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);

        mTextTitle.setText(bean.getTitle());
        TLog.log(bean.getTitle());
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * 获取选中的progress的位置
     * @param spf
     * @return
     */
    private ArrayList<Boolean> getCheckList(int spf) {
        ArrayList<Boolean> isCheckList = new ArrayList<Boolean>();
        switch (spf) {
            case 0:
                isCheckList.add(true);
                isCheckList.add(false);
                isCheckList.add(false);
                break;
            case 1:
                isCheckList.add(false);
                isCheckList.add(true);
                isCheckList.add(false);
                break;
            case 2:
                isCheckList.add(false);
                isCheckList.add(false);
                isCheckList.add(true);
                break;
        }
        return isCheckList;
    }

    /**
     * 获取百分比字符串集合
     * @param win
     * @param drawn
     * @param lost
     */
    private ArrayList<String> getPercentList(String win, String drawn, String lost) {
        ArrayList<String> percentList = new ArrayList<String>();
        percentList.add(win);
        percentList.add(drawn);
        percentList.add(lost);
        return percentList;

    }
}
