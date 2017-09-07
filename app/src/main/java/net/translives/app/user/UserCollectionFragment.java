package net.translives.app.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import net.translives.app.AppContext;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseGeneralRecyclerFragment;
import net.translives.app.bean.Collection;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.comment.QuesAnswerDetailActivity;
import net.translives.app.news.NewsDetailActivity;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.share.ShareDetailActivity;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.util.DialogHelper;

import java.lang.reflect.Type;


public class UserCollectionFragment extends BaseGeneralRecyclerFragment<Collection> implements BaseRecyclerAdapter.OnItemLongClickListener {

    public long userId;

    public static Fragment instantiate(int authorId) {
        UserAnswerFragment fragment = new UserAnswerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", authorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initData() {
        super.initData();
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getLong("user_id");
    }

    /**
     * fragment被销毁的时候重新调用，初始化保存的数据
     *
     * @param bundle onSaveInstanceState
     */
    @Override
    protected void onRestartInstance(Bundle bundle) {
        super.onRestartInstance(bundle);
        userId = bundle.getLong("user_id");
    }


    @Override
    protected void requestData() {
        super.requestData();

        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getUserCollectionList(token, userId, mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        Collection item = mAdapter.getItem(position);

        switch (item.getType()) {
            case Collection.TYPE_SHARE:
                ShareDetailActivity.show(mContext, item.getId(), true);
                break;
            case Collection.TYPE_ANSWER:
                QuesAnswerDetailActivity.show(mContext, item.getId(), true);
                break;
            case Collection.TYPE_NEWS:
                NewsDetailActivity.show(mContext, item.getId(), true);
                break;
            default:
                break;
        }

        return;
    }

    @Override
    protected BaseRecyclerAdapter<Collection> getRecyclerAdapter() {
        return new UserCollectionAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Collection>>>() {
        }.getType();
    }

    @Override
    protected Class<Collection> getCacheClass() {
        return Collection.class;
    }

    public boolean isNeedCache(){
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("user_id", userId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 1) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            onRefreshing();
        }
    }


    @Override
    public void onLongClick(final int position, long itemId) {
        final Collection collection = mAdapter.getItem(position);
        if (collection == null)
            return;
        DialogHelper.getConfirmDialog(mContext, "删除收藏", "是否确认删除该内容吗？", "确认", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFavReverse(collection, position);
            }
        }).show();
    }

    public void getFavReverse(final Collection collection, final int position) {
        OSChinaApi.getFavReverse(collection.getId(), collection.getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mAdapter.setState(BaseRecyclerAdapter.STATE_INVALID_NETWORK, true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (!collection.isFavorite()) {
                    showGetFavSuccess(position);
                }
            }
        });
    }

    public void showGetFavSuccess(int position) {
        mAdapter.removeItem(position);
        AppContext.showToastShort("操作成功");
    }
}
