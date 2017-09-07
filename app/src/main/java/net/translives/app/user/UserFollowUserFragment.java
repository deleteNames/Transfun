package net.translives.app.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.adapter.BaseRecyclerAdapter;
import net.translives.app.base.fragments.BaseRecyclerViewFragment;
import net.translives.app.bean.Follow;
import net.translives.app.bean.User;
import net.translives.app.bean.base.PageBean;
import net.translives.app.bean.base.ResultBean;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * @author thanatosx
 */
public class UserFollowUserFragment extends BaseRecyclerViewFragment<User> implements UserFollowUserAdapter.FollowBtnClickListen{

    private long userId;

    public static Fragment instantiate(int authorId) {
        UserFollowUserFragment fragment = new UserFollowUserFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", authorId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userId = bundle.getLong("user_id", 0);
    }

    @Override
    protected void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getUserFollowUserList(token, userId, mHandler);
    }

    @Override
    protected BaseRecyclerAdapter<User> getRecyclerAdapter() {
        return new UserFollowUserAdapter(getContext(),this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<User>>>() {
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
        User user = mAdapter.getItem(position);
        if (user == null)
            return;

        OtherUserHomeActivity.show(getContext(), user);
    }

    TextView mBtnFollow;
    @Override
    public void FollowBtnClick(View view) {
        User user = (User) view.getTag();

        mBtnFollow = (TextView) view;

        if(AccountHelper.getUserId() == user.getId()){
            Toast.makeText(getContext(), "不能关注自己", Toast.LENGTH_SHORT).show();
            return;
        }
        //if (mFollowMenu == null) return;
        OSChinaApi.getFollowReverse(user.getId(),"user", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString
                    , Throwable throwable) {
                Toast.makeText(getContext(), "操作失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Follow> result = AppOperator.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<Follow>>() {
                        }.getType());
                if (result.isSuccess()) {
                    boolean isFollow = result.getResult().isFollow();
                    if(isFollow){
                        mBtnFollow.setText("已关注");
                        mBtnFollow.setTextColor(getResources().getColor(R.color.done_text_color_disabled));
                        mBtnFollow.setSelected(true);
                    }else{
                        mBtnFollow.setText("+ 关注");
                        mBtnFollow.setTextColor(getResources().getColor(R.color.main_green));
                        mBtnFollow.setSelected(false);
                    }

                } else {
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
    }
}
