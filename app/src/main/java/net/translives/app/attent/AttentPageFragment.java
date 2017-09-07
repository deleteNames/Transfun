package net.translives.app.attent;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.translives.app.R;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.base.fragments.BaseViewPagerFragment;
import net.translives.app.interf.OnTabReselectListener;
import net.translives.app.notice.NoticeBean;
import net.translives.app.notice.NoticeManager;
import net.translives.app.util.TLog;


public class AttentPageFragment extends BaseViewPagerFragment implements OnTabReselectListener {

    //private NoticeBean mNotice;
    //private Runnable mNotifyAction;

    //private int mStatusLoading = 0X000;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        //mNotice = NoticeManager.getNotice();

        //NoticeManager.bindNotify(this);

    }

    /*
    private NoticeBean getNotice() {
        if (mNotice == null) return new NoticeBean();
        return mNotice;
    }
    */

    @Override
    protected PagerInfo[] getPagers() {
        return new PagerInfo[]{
                new PagerInfo("通知", NoticeFragment.class,new Bundle()),
                new PagerInfo("系统", AttentFragment.class,new Bundle()),
        };
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_attent_pager;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_attent;
    }

    @Override
    public void onTabReselect() {
        if (mBaseViewPager != null) {
            BaseViewPagerAdapter pagerAdapter = (BaseViewPagerAdapter) mBaseViewPager.getAdapter();
            Fragment fragment = pagerAdapter.getCurFragment();

            if (fragment != null) {
                if (fragment instanceof BaseGeneralRecyclerFragment)
                    ((BaseGeneralRecyclerFragment) fragment).onTabReselect();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
