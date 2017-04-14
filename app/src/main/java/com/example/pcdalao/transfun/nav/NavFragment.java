package com.example.pcdalao.transfun.nav;

/*
import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
*/
import android.view.View;



import com.example.pcdalao.transfun.R;
//import net.oschina.app.improve.account.AccountHelper;
import com.example.pcdalao.transfun.base.fragments.BaseFragment;

import com.example.pcdalao.transfun.tabs.DynamicTabFragment;

/*import net.oschina.app.improve.main.tabs.ExploreFragment;
import net.oschina.app.improve.main.tabs.TweetViewPagerFragment;
import net.oschina.app.improve.notice.NoticeBean;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.tweet.activities.TweetPublishActivity;
import net.oschina.app.improve.user.activities.UserFansActivity;
import net.oschina.app.improve.user.activities.UserMessageActivity;
import net.oschina.app.improve.user.fragments.UserInfoFragment;
import net.oschina.common.widget.drawable.shape.BorderShape;
import java.util.List;
*/

//import butterknife.Bind;
//import butterknife.OnClick;

public class NavFragment extends BaseFragment{

    //@Bind(R.id.nav_item_news)
    NavigationButton mNavNews;
    //@Bind(R.id.nav_item_tweet)
    NavigationButton mNavTweet;
    //@Bind(R.id.nav_item_explore)
    NavigationButton mNavExplore;
    //@Bind(R.id.nav_item_me)
    NavigationButton mNavMe;
  /*
    private Context mContext;
    private int mContainerId;
    private FragmentManager mFragmentManager;
    private NavigationButton mCurrentNavButton;
    private OnNavigationReselectListener mOnNavigationReselectListener;
*/

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nav;
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
/*
        ShapeDrawable lineDrawable = new ShapeDrawable(new BorderShape(new RectF(0, 1, 0, 0)));
        lineDrawable.getPaint().setColor(getResources().getColor(R.color.list_divider_color));
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                new ColorDrawable(getResources().getColor(R.color.white)),
                lineDrawable
        });
        root.setBackgroundDrawable(layerDrawable);
*/

        mNavNews.init(R.drawable.tab_icon_new,
                R.string.main_tab_name_news,
                ListPageFragment.class);

        mNavTweet.init(R.drawable.tab_icon_tweet,
                R.string.main_tab_name_tweet,
                TweetViewPagerFragment.class);

        mNavExplore.init(R.drawable.tab_icon_explore,
                R.string.main_tab_name_explore,
                ExploreFragment.class);

        mNavMe.init(R.drawable.tab_icon_me,
                R.string.main_tab_name_my,
                UserInfoFragment.class);

    }
}
