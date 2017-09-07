package net.translives.app.news;

import android.view.View;

import net.translives.app.R;
import net.translives.app.base.fragments.BaseTitleFragment;


public class NewsPageFragment extends BaseTitleFragment {

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_news_pager;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_news;
    }

}
