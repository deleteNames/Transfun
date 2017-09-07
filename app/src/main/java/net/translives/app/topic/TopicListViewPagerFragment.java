package net.translives.app.topic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.translives.app.R;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.base.fragments.BaseViewPagerNoTitleFragment;
import net.translives.app.bean.Topic;
import net.translives.app.bean.TopicActive;
import net.translives.app.interf.OnTabReselectListener;
import net.translives.app.question.QuestionFragment;



public class TopicListViewPagerFragment extends BaseViewPagerNoTitleFragment implements OnTabReselectListener {

    protected Topic topic;
    protected FragmentStatePagerAdapter mAdapter;

    public static TopicListViewPagerFragment instantiate(Topic topic) {
        TopicListViewPagerFragment fragment = new TopicListViewPagerFragment();
        fragment.topic = topic;
        return fragment;
    }

    private Bundle getBundle(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(TopicListFragment.BUNDLE_KEY_REQUEST_CATALOG, catalog);
        return bundle;
    }

    private Bundle getQuestionBundle(Topic topic) {
        Bundle bundle = new Bundle();
        bundle.putInt(QuestionFragment.BUNDLE_KEY_REQUEST_CATALOG, QuestionFragment.CATALOG_TOPIC);
        bundle.putSerializable("topic", topic);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic_viewpager;
    }

    @Override
    protected PagerInfo[] getPagers() {
        return new PagerInfo[]{
                new PagerInfo("最新", TopicListFragment.class, getBundle(TopicListFragment.CATALOG_NEW)),
                new PagerInfo("热门", TopicListFragment.class, getBundle(TopicListFragment.CATALOG_HOT)),
                new PagerInfo("问答", QuestionFragment.class, getQuestionBundle(topic)),
        };
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
}
