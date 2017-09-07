package net.translives.app.bean;

import net.translives.app.R;
import net.translives.app.user.AboutUsFragment;
import net.translives.app.user.MyInformationFragmentDetail;
import net.translives.app.user.UserAnswerFragment;
import net.translives.app.user.UserCollectionFragment;
import net.translives.app.user.UserFollowPageFragment;
import net.translives.app.user.UserQuestionFragment;
import net.translives.app.user.UserTopicFragment;

public enum SimpleBackPage {

    ABOUT_US(17, R.string.about, AboutUsFragment.class),
    MY_COllECTION(10, R.string.favorite, UserCollectionFragment.class),
    MY_ANSWER(11, R.string.answer, UserAnswerFragment.class),
    MY_QUESTION(12, R.string.question, UserQuestionFragment.class),
    MY_FOLLOW(13, R.string.follow, UserFollowPageFragment.class),
    MY_TOPIC(14, R.string.topic, UserTopicFragment.class),
    MY_INFORMATION_DETAIL(20, R.string.actionbar_title_my_information, MyInformationFragmentDetail.class);


    private int title;
    private Class<?> clz;
    private int value;

    private SimpleBackPage(int value, int title, Class<?> clz) {
        this.value = value;
        this.title = title;
        this.clz = clz;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static SimpleBackPage getPageByValue(int val) {
        for (SimpleBackPage p : values()) {
            if (p.getValue() == val)
                return p;
        }
        return null;
    }


}
