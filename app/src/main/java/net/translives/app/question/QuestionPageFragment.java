package net.translives.app.question;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import net.translives.app.R;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.base.fragments.BaseViewPagerFragment;
import net.translives.app.interf.OnTabReselectListener;
import net.translives.app.share.SharePublishActivity;
import net.translives.app.util.TLog;

import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class QuestionPageFragment extends BaseViewPagerFragment implements OnTabReselectListener,RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{

    @Bind(R.id.label_list_sample_rfal)
    RapidFloatingActionLayout rfaLayout;
    @Bind(R.id.label_list_sample_rfab)
    RapidFloatingActionButton rfaButton;
    RapidFloatingActionHelper rfabHelper;

    private Bundle getBundle(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(QuestionFragment.BUNDLE_KEY_REQUEST_CATALOG, catalog);
        return bundle;
    }

    @Override
    protected PagerInfo[] getPagers() {
        return new PagerInfo[]{
                new PagerInfo("最新", QuestionFragment.class,
                        getBundle(QuestionFragment.CATALOG_NEW)),
                new PagerInfo("热门", QuestionFragment.class,
                        getBundle(QuestionFragment.CATALOG_HOT)),
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

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_question_pager;
    }

    @Override
        protected void initWidget(View root) {

        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(mContext);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("分享")
                .setResId(R.mipmap.btn_post_share)
                //.setIconNormalColor(0xffd84315)
                //.setIconPressedColor(0xffbf360c)
                .setWrapper(0)
        );

        items.add(new RFACLabelItem<Integer>()
                .setLabel("提问")
                .setResId(R.mipmap.btn_post_question)
                //.setIconNormalColor(0xff283593)
                //.setIconPressedColor(0xff1a237e)
                //.setLabelColor(0xff283593)
                .setWrapper(1)
        );

        rfaContent.setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(mContext, 5))
                .setIconShadowColor(0xffbbbbbb)
                .setIconShadowDx(ABTextUtil.dip2px(mContext, 2))
                .setIconShadowDy(ABTextUtil.dip2px(mContext, 3));

        rfabHelper = new RapidFloatingActionHelper(
                mContext,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();

        super.initWidget(root);
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_tweet;
    }
/*
    @Override
    protected int getIconRes() {
        return R.mipmap.btn_search_normal;
    }

    @Override
    protected View.OnClickListener getIconClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SearchActivity.show(getContext());
            }
        };
    }
*/
    @Override
    public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {
        switch (i){
            case 0:
                SharePublishActivity.show(mContext);
                break;
            case 1:
                QuestionPublishActivity.show(mContext);
                break;
        }
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {
        switch (i){
            case 0:
                SharePublishActivity.show(mContext);
                break;
            case 1:
                QuestionPublishActivity.show(mContext);
                break;
        }
        rfabHelper.toggleContent();
    }


}
