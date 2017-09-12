package net.translives.app.message;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;
import net.translives.app.account.AccountHelper;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.Message;
import net.translives.app.bean.User;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;

import java.lang.reflect.Type;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class MessageFragment extends BaseRecyclerViewFragment<Message> {

    public long authorId;

    //private UserMessageActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //if (context != null && context instanceof UserMessageActivity) {
        //    activity = (UserMessageActivity) context;
       // }
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        //if (activity != null && isRefreshing) activity.onRequestSuccess(2);
    }

    @Override
    public void initData() {
        super.initData();
        authorId = AccountHelper.getUserId();
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getUserMessageList(isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Message message = mAdapter.getItem(position);
        if (message == null)
            return;
        User sender = message.getSender();
        if (sender != null)
            SendMessageActivity.show(getContext(), message.getSender());
    }

    @Override
    protected BaseRecyclerAdapter<Message> getRecyclerAdapter() {
        return new MessageAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Message>>>() {
        }.getType();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
