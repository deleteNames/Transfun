package net.translives.app.base;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public interface BaseView<Presenter extends BasePresenter> {

    void setPresenter(Presenter presenter);

    void showNetworkError(int strId);
}
