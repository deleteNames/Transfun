package net.translives.app.attent;

import com.google.gson.reflect.TypeToken;

import net.translives.app.api.OSChinaApi;

import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.Attent;

import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/12/30.
 */

public class AttentFragment extends BaseRecyclerViewFragment<Attent>{

    @Override
    protected void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getAttentList(token, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<Attent> getRecyclerAdapter() {
        return new AttentAdapter(mContext);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Attent>>>() {
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
        return;
    }
}
