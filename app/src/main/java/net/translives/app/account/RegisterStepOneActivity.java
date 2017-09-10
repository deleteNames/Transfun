package net.translives.app.account;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;


import net.translives.app.AppContext;
import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.bean.RegisterCheck;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.util.TDevice;
import net.translives.app.util.parser.RichTextParser;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class RegisterStepOneActivity extends AccountBaseActivity implements View.OnClickListener, View.OnFocusChangeListener{

    @Bind(R.id.ll_register_email)
    LinearLayout mLlRegisterEmail;
    @Bind(R.id.et_register_email)
    EditText mEtRegisterEmail;
    @Bind(R.id.iv_register_email_del)
    ImageView mIvRegisterEmailDel;

    @Bind(R.id.ll_register_pwd)
    LinearLayout mLlRegisterPwd;
    @Bind(R.id.et_register_pwd)
    EditText mEtRegisterPwd;
    @Bind(R.id.iv_register_pwd_del)
    ImageView mIvRegisterPwdDel;

    @Bind(R.id.bt_register_submit)
    Button mBtRegisterSubmit;

    private boolean mMachEmail;

    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            showWaitDialog(R.string.state_submit);
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

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            requestFailureHint(throwable);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {


                Type phoneType = new TypeToken<ResultBean<RegisterCheck>>() {
                }.getType();

                ResultBean<RegisterCheck> ResultBean = AppOperator.createGson().fromJson(responseString, phoneType);
                if (ResultBean.isSuccess()) {
                    RegisterCheck registerResult = ResultBean.getResult();
                    RegisterStepTwoActivity.show(RegisterStepOneActivity.this, registerResult.getUid());
                } else {
                    showToastForKeyBord(ResultBean.getMessage());
                }

        }
    };

    /**
     * show the register activity
     *
     * @param context context
     */
    public static void show(Context context) {
        Intent intent = new Intent(context, RegisterStepOneActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_register_step_one;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mEtRegisterEmail.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int length = s.length();
                        if (length > 0) {
                            mIvRegisterEmailDel.setVisibility(View.VISIBLE);
                        } else {
                            mIvRegisterEmailDel.setVisibility(View.INVISIBLE);
                        }
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public void afterTextChanged(Editable s) {
                        setInputStatus();
                    }
                }

        );
        mEtRegisterEmail.setOnFocusChangeListener(this);

    }

    public void setInputStatus(){
        String input_email= mEtRegisterEmail.getText().toString().trim();
        String input_pwd= mEtRegisterPwd.getText().toString().trim();
        if (!TextUtils.isEmpty(input_email) && !TextUtils.isEmpty(input_pwd)) {
            mBtRegisterSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtRegisterSubmit.setTextColor(getResources().getColor(R.color.white));
        } else {
            mBtRegisterSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtRegisterSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color));
        }
    }


    @Override
    protected void initData() {
        super.initData();//必须要调用,用来注册本地广播
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyBoard(getCurrentFocus().getWindowToken());
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick({R.id.iv_register_email_del,R.id.iv_register_pwd_del,R.id.bt_register_submit, R.id.lay_register_one_container})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_register_email_del:
                mEtRegisterEmail.setText(null);
                setInputStatus();
                break;
            case R.id.iv_register_pwd_del:
                mEtRegisterPwd.setText(null);
                setInputStatus();
                break;
            case R.id.bt_register_submit:
                requestRegister();
                // RegisterStepTwoActivity.show(this,null);
                break;
            case R.id.lay_register_one_container:
                hideKeyBoard(getCurrentFocus().getWindowToken());
                break;
            default:
                break;
        }

    }

    private void requestRegister() {

        String input_email= mEtRegisterEmail.getText().toString().trim();
        String input_pwd= mEtRegisterPwd.getText().toString().trim();

        if (TextUtils.isEmpty(input_email) || TextUtils.isEmpty(input_pwd)) {
            //showToastForKeyBord(R.string.hint_username_ok);
            return;
        }

        if (!TDevice.hasInternet()) {
            showToastForKeyBord(R.string.tip_network_error);
            return;
        }

        OSChinaApi.validateRegister(input_email, input_pwd, mHandler);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id) {
            case R.id.et_register_email:
                if (hasFocus) {
                    mLlRegisterEmail.setActivated(true);
                }
                break;
            case R.id.et_register_pwd:
                if (hasFocus) {
                    mLlRegisterPwd.setActivated(true);
                }
                break;
            default:
                break;
        }
    }

}
