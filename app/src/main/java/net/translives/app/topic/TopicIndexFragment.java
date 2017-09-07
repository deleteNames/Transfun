package net.translives.app.topic;

import net.translives.app.R;
import net.translives.app.base.fragments.BaseTitleFragment;


public class TopicIndexFragment extends BaseTitleFragment {

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_topic_index;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_faxian;
    }

}
