package net.translives.app.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.translives.app.MainActivity;
import net.translives.app.account.LoginActivity;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.SimpleBackPage;
import net.translives.app.bean.Version;
import net.translives.app.util.AppHelper;
import net.translives.app.util.BlurImageview;
import net.translives.app.util.TLog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.account.AccountHelper;
import net.translives.app.AppOperator;
import net.translives.app.base.fragments.BaseFragment;
import net.translives.app.bean.User;
import net.translives.app.bean.base.ResultBean;

import net.translives.app.util.DialogHelper;
import net.translives.app.util.UiUtil;
import net.translives.app.util.TDevice;

import java.io.File;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import net.translives.app.widget.PortraitView;

/**
 * Created by fei on 2016/8/15.
 * <p>
 * 用户个人界面
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener{
    @Bind(R.id.iv_portrait)
    PortraitView mCirclePortrait;

    //@Bind(R.id.iv_gender)
    //ImageView mIvGander;

    @Bind(R.id.user_info_icon_container)
    FrameLayout mFlUserInfoIconContainer;

    @Bind(R.id.tv_nick)
    TextView mTvName;

    @Bind(R.id.tv_summary)
    TextView mTvSummary;

    //@Bind(R.id.tv_score)
    //TextView mTvScore;
    //@Bind(R.id.user_view_solar_system)
    //SolarSystemView mSolarSystem;
    @Bind(R.id.rl_show_my_info)
    LinearLayout mRlShowInfo;

    @Bind(R.id.user_info_bg)
    FrameLayout mUserInfoBg;

    private Version mVersion;

    private boolean mIsUploadIcon;
    private ProgressDialog mDialog;

    private File mCacheFile;

    private User mUserInfo;

    private TextHttpResponseHandler requestUserInfoHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            //if (mSolarSystem != null) mSolarSystem.accelerate();
            if (mIsUploadIcon) {
                showWaitDialog(R.string.title_updating_user_avatar);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            if (mIsUploadIcon) {
                Toast.makeText(getActivity(), R.string.title_update_fail_status, Toast.LENGTH_SHORT).show();
                deleteCacheImage();
            }

            TLog.log(responseString);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<ResultBean<User>>() {
                }.getType();

                TLog.log(responseString);
                ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    User userInfo = (User) resultBean.getResult();
                    updateView(userInfo);
                    //缓存用户信息
                    AccountHelper.updateUserCache(userInfo);
                }else{
                    hideView();
                    AccountHelper.logout();
                }

                if (mIsUploadIcon) {
                    deleteCacheImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            //if (mSolarSystem != null) mSolarSystem.decelerate();
            if (mIsUploadIcon) mIsUploadIcon = false;
            if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        }
    };


    /**
     * delete the cache image file for upload action
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteCacheImage() {
        File file = this.mCacheFile;
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_user_home;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        measureTitleBarHeight();

        //initSolar();
    }

    @Override
    protected void initData() {
        super.initData();

        requestUserCache();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsUploadIcon = false;

        if (AccountHelper.isLogin()) {
            User user = AccountHelper.getUser();
            updateView(user);

        } else {

            hideView();
        }
    }

    /**
     * if user isLogin,request user cache,
     * And then request user info and update user info
     */
    private void requestUserCache() {
        if (AccountHelper.isLogin()) {
            User user = AccountHelper.getUser();
            updateView(user);
            sendRequestData();
        } else {
            hideView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!AccountHelper.isLogin()) {
            hideView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * update the view
     *
     * @param userInfo userInfo
     */
    private void updateView(User userInfo){

        mCirclePortrait.setDrawingCacheEnabled(true);
        setImageFromNet(mCirclePortrait, userInfo.getPortrait(), R.mipmap.widget_default_face);
        mCirclePortrait.setVisibility(View.VISIBLE);

        mTvName.setText(userInfo.getName());
        mTvName.setVisibility(View.VISIBLE);
        mTvName.setTextSize(20.0f);

        mTvSummary.setText(userInfo.getSuffix());
        mTvSummary.setVisibility(View.VISIBLE);

        /*
        switch (userInfo.getGender()) {
            case 0:
                mIvGander.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.ic_male);
                break;
            case 2:
                mIvGander.setVisibility(View.VISIBLE);
                mIvGander.setImageResource(R.mipmap.ic_female);
                break;
            default:
                break;
        }*/

        /*
        mTvScore.setText(String.format(
                "%s  %s",
                getString(R.string.user_score),
                formatCount(userInfo.getStatistics().getScore()))
        );
        mTvScore.setVisibility(View.VISIBLE);
        */

        Glide.with( getContext())
                .load(userInfo.getPortrait())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        mUserInfoBg.setBackgroundDrawable(BlurImageview.BlurImages(resource, getContext() ));
                    }
                });



        mUserInfo = userInfo;
    }


    /*
    private void setModel() {
        if (null != bd) {
            mUserInfoBg.setBackgroundDrawable(bd);
        }


        //将old_image对象转化为bitmap对象
        mCirclePortrait.buildDrawingCache();
        mBitmap = mCirclePortrait.getDrawingCache();
        mBitmap = ((BitmapDrawable) mCirclePortrait.getDrawable()).getBitmapFromDrawable();


    }*/

    /**
     * requestData
     */
    private void sendRequestData() {
        if (TDevice.hasInternet() && AccountHelper.isLogin())
            OSChinaApi.getProfileInfo(requestUserInfoHandler);
    }

    /**
     *
     */
    private void hideView() {
        mCirclePortrait.setImageResource(R.mipmap.widget_default_face);
        mTvName.setText(R.string.user_hint_login);
        mTvName.setTextSize(16.0f);
        //mIvGander.setVisibility(View.INVISIBLE);
        //mTvScore.setVisibility(View.INVISIBLE);
        mTvSummary.setVisibility(View.INVISIBLE);

        mUserInfoBg.setBackgroundColor(getResources().getColor(R.color.main_green));
    }

    /**
     * measureTitleBarHeight
     */
    private void measureTitleBarHeight() {
        if (mRlShowInfo != null) {
            mRlShowInfo.setPadding(mRlShowInfo.getLeft(),
                    UiUtil.getStatusBarHeight(getContext()),
                    mRlShowInfo.getRight(), mRlShowInfo.getBottom());
        }
    }

    @SuppressWarnings("deprecation")
    @OnClick({
            R.id.iv_portrait,
            R.id.rl_fav, R.id.rl_question, R.id.rl_answer,
            R.id.rl_follow,
            R.id.rl_topic,
            R.id.iv_setting
    })
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.iv_setting) {
            SettingActivity.show(getActivity());
        }else{

            if (!AccountHelper.isLogin()) {
                LoginActivity.show(getActivity());
                return;
            }

            switch (id) {
                case R.id.iv_portrait:
                    //查看头像 or 更换头像
                    //showAvatarOperation();

                    if (mUserInfo != null) {
                        Bundle userBundle = new Bundle();
                        userBundle.putSerializable("user_info", mUserInfo);
                        AppHelper.showSimpleBack(getActivity(), SimpleBackPage.MY_INFORMATION_DETAIL, userBundle);
                    }
                    break;

                case R.id.rl_fav:
                    AppHelper.showUserCollection(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.rl_question:
                    AppHelper.showUserQuestion(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.rl_answer:
                    AppHelper.showUserAnswer(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.rl_follow:
                    AppHelper.showUserFollow(getActivity(), AccountHelper.getUserId());
                    break;
                case R.id.rl_topic:
                    AppHelper.showUserTopic(getActivity(), AccountHelper.getUserId());
                default:
                    break;
            }
        }
    }


    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(getActivity(), message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

}
