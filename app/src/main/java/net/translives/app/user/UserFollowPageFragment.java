package net.translives.app.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.base.fragments.BaseViewPagerFragment;
import net.translives.app.base.fragments.BaseViewPagerNoTitleFragment;
import net.translives.app.interf.OnTabReselectListener;

/**
 * @author thanatosx
 */
public class UserFollowPageFragment extends BaseViewPagerNoTitleFragment implements OnTabReselectListener {

    private long userId;

    public static Fragment instantiate(int authorId) {
        UserFollowPageFragment fragment = new UserFollowPageFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", authorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getLong("user_id", 0);
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", userId);
        return bundle;
    }

    @Override
    protected PagerInfo[] getPagers() {
        return new PagerInfo[]{
                new PagerInfo("问答", UserFollowQuestionFragment.class,getBundle()),
                new PagerInfo("用户", UserFollowUserFragment.class,getBundle()),
        };
    }

    @Override
    public void onTabReselect() {
        if (mBaseViewPager != null) {
            BaseViewPagerFragment.BaseViewPagerAdapter pagerAdapter = (BaseViewPagerFragment.BaseViewPagerAdapter) mBaseViewPager.getAdapter();
            Fragment fragment = pagerAdapter.getCurFragment();
            if (fragment != null) {
                if (fragment instanceof BaseGeneralRecyclerFragment)
                    ((BaseGeneralRecyclerFragment) fragment).onTabReselect();
            }
        }
    }
}
