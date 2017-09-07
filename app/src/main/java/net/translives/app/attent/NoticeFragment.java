package net.translives.app.attent;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import net.translives.app.MainActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.Notice;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.comment.QuesAnswerDetailActivity;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.share.ShareDetailActivity;
import net.translives.app.topic.TopicActiveDetailActivity;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/12/30.
 */

public class NoticeFragment extends BaseRecyclerViewFragment<Notice> {

    private MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @Override
    public void onRequestSuccess(int code){
        super.onRequestSuccess(code);
        if (activity != null && isRefreshing) activity.clearSpecificNotice(2);
    }

    @Override
    protected void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getNoticeList(token, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<Notice> getRecyclerAdapter() {
        return new NoticeAdapter(mContext);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Notice>>>() {
        }.getType();
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    public void onItemClick(int position, long itemId) {

        Notice notice = mAdapter.getItem(position);
        if (notice == null)
            return;

        int type = notice.getSource().getType();
        switch (type){
            case Notice.SOURCE_TYPE_ANSWER:
                QuesAnswerDetailActivity.show(getActivity(),notice.getSource().getId(),false);
                break;
            case Notice.SOURCE_TYPE_QUESTION:
                QuestionDetailActivity.show(getActivity(),notice.getSource().getId());
                break;
            case Notice.SOURCE_TYPE_TOPIC:
                TopicActiveDetailActivity.show(getActivity(),notice.getSource().getId());
                break;
            case Notice.SOURCE_TYPE_COMMENT:
                break;
            case Notice.SOURCE_TYPE_SHARE:
                ShareDetailActivity.show(getActivity(),notice.getSource().getId());
                break;
        }

    }
}
