package net.translives.app.account;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.translives.app.util.TLog;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import net.translives.app.AppContext;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.bean.User;
import net.translives.app.bean.base.ResultBean;
//import net.translives.app.user.helper.SyncFriendHelper;
import net.translives.app.open.constants.OpenConstant;
import net.translives.app.open.factory.OpenBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;


/**
 * Created by fei on 2016/10/14.
 * desc:
 */

public class LoginActivity extends AccountBaseActivity implements View.OnClickListener, IUiListener, View.OnFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener {

    public static final String HOLD_USERNAME_KEY = "holdUsernameKey";

    @Bind(R.id.ly_retrieve_bar)
    LinearLayout mLayBackBar;

    @Bind(R.id.iv_login_logo)
    ImageView mIvLoginLogo;

    @Bind(R.id.ll_login_options)
    LinearLayout mLlLoginOptions;

    @Bind(R.id.ib_login_weibo)
    ImageView mIbLoginWeiBo;
    //@Bind(R.id.ib_login_wx)
    //ImageView mIbLoginWx;
    @Bind(R.id.ib_login_qq)
    ImageView mImLoginQq;

    private int openType;
    private SsoHandler mSsoHandler;
    private Tencent mTencent;


    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            showFocusWaitDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            TLog.error(responseString);
            requestFailureHint(throwable);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {

            Type type = new TypeToken<ResultBean<User>>() {
            }.getType();
            TLog.log(responseString);
            GsonBuilder gsonBuilder = new GsonBuilder();
            ResultBean<User> resultBean = gsonBuilder.create().fromJson(responseString, type);
            if (resultBean.isSuccess()) {
                User user = resultBean.getResult();
                AccountHelper.login(user, headers);
//                hideKeyBoard(getCurrentFocus().getWindowToken());
                AppContext.showToast(R.string.login_success_hint);
                setResult(RESULT_OK);
                sendLocalReceiver();

                //后台异步同步数据
                //SyncFriendHelper.load(null);

                MobclickAgent.onProfileSignIn(user.getMore().getPlatform(),user.getId()+"");

            } else {
                showToastForKeyBord(resultBean.getMessage());
            }

        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideWaitDialog();
        }

        @Override
        public void onCancel() {
            super.onCancel();
            hideWaitDialog();
        }
    };
    private int mLogoHeight;
    private int mLogoWidth;

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * show the login activity
     *
     * @param context context
     */
    public static void show(Activity context, int requestCode) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * show the login activity
     *
     * @param fragment fragment
     */
    public static void show(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        TextView label = (TextView) mLayBackBar.findViewById(R.id.tv_navigation_label);
        label.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void initData() {
        super.initData();//必须要,用来注册本地广播
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //hideKeyBoard(getCurrentFocus().getWindowToken());
        mLayBackBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.ib_navigation_back,R.id.ib_login_weibo, R.id.ib_login_qq})//, R.id.ib_login_wx
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.ib_login_weibo:
                weiBoLogin();
                break;
            //case R.id.ib_login_wx:
            //    //微信登录
            //    wechatLogin();
             //   break;
            case R.id.ib_login_qq:
                //QQ登录
                tencentLogin();
                break;
            default:
                break;
        }

    }

    /**
     * login tencent
     */
    private void tencentLogin() {
        showWaitDialog(R.string.login_tencent_hint);
        openType = OpenConstant.TENCENT;
        mTencent = OpenBuilder.with(this)
                .useTencent(OpenConstant.QQ_APP_ID)
                .login(this, new OpenBuilder.Callback() {
                    @Override
                    public void onFailed() {
                        hideWaitDialog();
                    }

                    @Override
                    public void onSuccess() {
                        //hideWaitDialog();
                    }
                });
    }

    /**
     * login wechat
     */
    private void wechatLogin() {
        showWaitDialog(R.string.login_wechat_hint);
        openType = OpenConstant.WECHAT;
        OpenBuilder.with(this)
                .useWechat(OpenConstant.WECHAT_APP_ID)
                .login(new OpenBuilder.Callback() {
                    @Override
                    public void onFailed() {
                        hideWaitDialog();
                        AppContext.showToast(R.string.login_hint);
                    }

                    @Override
                    public void onSuccess() {
                        //hideWaitDialog();
                    }
                });
    }

    /**
     * login weiBo
    */
    private void weiBoLogin() {
        showWaitDialog(R.string.login_weibo_hint);
        openType = OpenConstant.SINA;
        mSsoHandler = OpenBuilder.with(this)
                .useWeibo(OpenConstant.WB_APP_KEY)
                .login(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        hideWaitDialog();
                        Oauth2AccessToken oauth2AccessToken = Oauth2AccessToken.parseAccessToken(bundle);

                        if (oauth2AccessToken.isSessionValid()) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("openid", oauth2AccessToken.getUid());
                                jsonObject.put("expires_in", oauth2AccessToken.getExpiresTime());
                                jsonObject.put("refresh_token", oauth2AccessToken.getRefreshToken());
                                jsonObject.put("access_token", oauth2AccessToken.getToken());

                                OSChinaApi.openLogin(OSChinaApi.LOGIN_WEIBO, jsonObject.toString(), mHandler);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        e.printStackTrace();
                        hideWaitDialog();
                    }

                    @Override
                    public void onCancel() {
                        hideWaitDialog();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        tencentOnActivityResult(data);
        weiBoOnActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * weiBo Activity Result
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */

    private void weiBoOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (openType == OpenConstant.SINA) {
            // SSO 授权回调
            // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
            if (mSsoHandler != null)
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * tencent Activity Result
     *
     * @param data data
     */
    @SuppressWarnings("deprecation")
    private void tencentOnActivityResult(Intent data) {
        if (openType == OpenConstant.TENCENT) {
            // 对于tencent
            // 注：在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。
            if (mTencent != null) {
                mTencent.handleLoginData(data, this);
            }
        }
    }

    /**
     * tencent callback
     *
     * @param o json
     */
    @Override
    public void onComplete(Object o) {
        JSONObject jsonObject = (JSONObject) o;
        OSChinaApi.openLogin(OSChinaApi.LOGIN_QQ, jsonObject.toString(), mHandler);
        hideWaitDialog();
    }

    /**
     * tencent callback
     *
     * @param uiError uiError
     */
    @Override
    public void onError(UiError uiError) {
        hideWaitDialog();
    }


    /**
     * tencent callback
     */
    @Override
    public void onCancel() {
        hideWaitDialog();
    }


    @Override
    public void onGlobalLayout() {

        final ImageView ivLogo = this.mIvLoginLogo;
        Rect KeypadRect = new Rect();

        mLayBackBar.getWindowVisibleDisplayFrame(KeypadRect);

        int screenHeight = mLayBackBar.getRootView().getHeight();

        int keypadHeight = screenHeight - KeypadRect.bottom;

        if (keypadHeight > 0) {
            updateKeyBoardActiveStatus(true);
        } else {
            updateKeyBoardActiveStatus(false);
        }
        if (keypadHeight > 0 && ivLogo.getTag() == null) {
            final int height = ivLogo.getHeight();
            final int width = ivLogo.getWidth();
            this.mLogoHeight = height;
            this.mLogoWidth = width;
            ivLogo.setTag(true);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                }
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();


        } else if (keypadHeight == 0 && ivLogo.getTag() != null) {
            final int height = mLogoHeight;
            final int width = mLogoWidth;
            ivLogo.setTag(null);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                }
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();

        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
