package net.translives.app.account;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;


import net.translives.app.AppContext;
import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.api.OSChinaApi;
import net.translives.app.bean.User;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.media.SelectImageActivity;
import net.translives.app.media.SelectOptions;
import net.translives.app.ui.SimpleBackActivity;
import net.translives.app.ui.empty.EmptyLayout;
import net.translives.app.user.SettingActivity;
import net.translives.app.util.DialogHelper;
import net.translives.app.util.ImageLoader;
import net.translives.app.util.ImageUtils;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TLog;
import net.translives.app.widget.PortraitView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by fei
 * on 2016/10/14.
 * desc:
 */

public class UserUploadActivity extends AccountBaseActivity implements View.OnClickListener ,EasyPermissions.PermissionCallbacks{

    @Bind(R.id.iv_avatar)
    PortraitView mUserFace;

    @Bind(R.id.tv_name)
    TextView mName;

    @Bind(R.id.tv_gender)
    TextView mGender;

    @Bind(R.id.tv_suffix)
    TextView mSuffix;


    @Bind(R.id.tv_join_time)
    TextView mJoinTime;

    @Bind(R.id.tv_location)
    TextView mFrom;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    private AlertDialog.Builder mGenderDialogBuilder;

    private AlertDialog.Builder mNameDialogBuilder;
    private AlertDialog mNameDialog;

    private AlertDialog.Builder mSuffixDialogBuilder;
    private AlertDialog mSuffixDialog;

    private boolean mIsUploadIcon;
    private ProgressDialog mDialog;

    private File mCacheFile;
    private User userInfo;

    String[] genderItems = {
            "保密",
            "顺性女 - Cis Female", //顺性女。出生时生物性别是女性，自己也觉得自己是女性。
            "顺性男 - Cis Male", //出生时生物性别是男性，自己也觉得自己是男性。
            "跨性女 - Trans Female (MTF)", //出生时是男性，但现在自我认同女性。
            "跨性男 - Trans Male (FTM)", //出生时是女性，但现在自我认同男性。
            "易性 - Autogynephilia",
            "泛性别 - Pangender", //认为自己是各种性别特质的混合体，每样都有一点儿。
            "无性别 - Agender", // 没有发育性别、或者没有感觉到自己有任何强烈性别归属的人。他们不见得认为自己没有性别，但可能觉得性别不是自己的核心特质。
            "两性 - Androgyne", //拥有混合特征或者两种特征都很强烈的人。更强调对内的自我认同。
            "双性 - Bigender", //自我性别认定可以在两种之间切换的人。两种性别未必是男和女，可以是这里提到的许多种其它非传统性别。
            "间性 - Intersex", //由于染色体或发育异常而拥有男女双方性征的人。
            "非常规性别 - Gender Nonconforming", //拒绝接受传统性别二元区分的人。事实上这里的56种性别里很多都不是二元区分——但是选择这一选项的人，强调的是自己的拒绝特征：我不属于传统二元，但我也不会去精确定位自己的位置。下面有好几个选项和它意思类似。
            "性别存疑 - Gender Questioning", //对自己的性别归属不完全确定、还没有找到最适合自己的性别认同标签的人。
    };


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
                Toast.makeText(UserUploadActivity.this, R.string.title_update_fail_status, Toast.LENGTH_SHORT).show();
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

                    ImageLoader.loadImage(getImageLoader(), mUserFace, userInfo.getPortrait(), R.mipmap.widget_default_face);

                    //缓存用户信息
                    AccountHelper.updateUserCache(userInfo);
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

    public static void show(Context context, User user) {
        Intent intent = new Intent(context, UserUploadActivity.class);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_main_register_user_upload;
    }

    @Override
    public void initWidget(){
        super.initWidget();

    }

    @Override
    public boolean initBundle(Bundle bundle) {
        super.initBundle(bundle);
        userInfo = (User)bundle.getSerializable("user");
        return true;
    }

    @Override
    public void initData() {
        if (userInfo == null || userInfo.getId() != AccountHelper.getUserId()) {
            return;
        }

        mUserFace.setup(userInfo);

        mName.setText(getText(userInfo.getName()));
        mGender.setText(getText(userInfo.getGenderText()));
        mSuffix.setText(getText(userInfo.getSuffix()));
        mJoinTime.setText(getText(StringUtils.formatYearMonthDayNew(userInfo.getMore().getJoinDate())));
        mFrom.setText(getText(userInfo.getMore().getCity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsUploadIcon = false;
    }


    @SuppressWarnings("deprecation")
    @OnClick({
            R.id.iv_avatar,
            R.id.ly_name,
            R.id.ly_gender,
            R.id.ly_suffix
    })
    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.iv_avatar:
                showAvatarOperation();
                break;
            case R.id.ly_name:
                editNickNameDialog();
                break;
            case R.id.ly_gender:
                editGenderDialog();
                break;
            case R.id.ly_suffix:
                editSuffixDialog();
                break;
        }

    }

    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(this, message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    private String getText(String text) {
        if (text == null || text.equalsIgnoreCase("null"))
            return "<无>";
        else return text;
    }



    /**
     * 更换头像 or 查看头像
     */
    private void showAvatarOperation() {

        DialogHelper.getSelectDialog(this,
                getString(R.string.action_select),
                getResources().getStringArray(R.array.avatar_option), "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                SelectImageActivity.show(UserUploadActivity.this, new SelectOptions.Builder()
                                        .setSelectCount(1)
                                        .setHasCam(true)
                                        .setCrop(700, 700)
                                        .setCallback(new SelectOptions.Callback() {
                                            @Override
                                            public void doSelected(String[] images) {
                                                String path = images[0];
                                                uploadNewPhoto(new File(path));
                                            }
                                        }).build());
                                break;
                        }
                    }
                }).show();

    }


    @SuppressLint("InflateParams")
    private void editNickNameDialog(){

        LayoutInflater inflater = LayoutInflater.from(UserUploadActivity.this);
        final View mNameDialogView = inflater.inflate(R.layout.dialog_profile_nickname, null, false);

        mNameDialogBuilder = DialogHelper.getDialog(UserUploadActivity.this);
        mNameDialogBuilder.setView(mNameDialogView);
        mNameDialogBuilder.setCancelable(false);
        mNameDialogBuilder.setTitle(R.string.edit_nickname);
        mNameDialogBuilder.setPositiveButton("确定", null);
        mNameDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });

        mNameDialog = mNameDialogBuilder.create();
        mNameDialog.setCancelable(false);
        mNameDialog.show();

        mNameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText nickname = (EditText) mNameDialogView.findViewById(R.id.nickname);

                OSChinaApi.uploadNickName(nickname.getText().toString(), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        AppContext.showToastShort("操作失败");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        ResultBean<User> result = AppOperator.createGson().fromJson(
                                responseString, new TypeToken<ResultBean<User>>() {
                                }.getType());
                        if (result.isSuccess()) {
                            AppContext.showToastShort("操作成功");

                            User userInfo = result.getResult();

                            mName.setText(getText(userInfo.getName()));
                            AccountHelper.updateUserCache(userInfo);

                            mNameDialog.cancel();
                        } else {
                            AppContext.showToastShort(TextUtils.isEmpty(result.getMessage())
                                    ? "操作失败" : result.getMessage());
                        }

                    }
                });
            }
        });
    }

    @SuppressLint("InflateParams")
    private void editGenderDialog(){


        mGenderDialogBuilder = DialogHelper.getSelectDialog(UserUploadActivity.this, genderItems,"取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                OSChinaApi.uploadGander(which, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        AppContext.showToastShort("操作失败");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        ResultBean<User> result = AppOperator.createGson().fromJson(
                                responseString, new TypeToken<ResultBean<User>>() {
                                }.getType());
                        if (result.isSuccess()) {
                            AppContext.showToastShort("操作成功");

                            User userInfo = result.getResult();

                            mGender.setText(getText(userInfo.getGenderText()));
                            AccountHelper.updateUserCache(userInfo);

                        } else {
                            AppContext.showToastShort(TextUtils.isEmpty(result.getMessage())
                                    ? "操作失败" : result.getMessage());
                        }

                    }
                });
            }
        });

        mGenderDialogBuilder.setCancelable(false);
        mGenderDialogBuilder.setTitle(R.string.edit_gender);
        mGenderDialogBuilder.show();

    }


    @SuppressLint("InflateParams")
    private void editSuffixDialog(){

        LayoutInflater inflater = LayoutInflater.from(UserUploadActivity.this);
        final View mSuffixDialogView = inflater.inflate(R.layout.dialog_profile_suffix, null, false);

        mSuffixDialogBuilder = DialogHelper.getDialog(UserUploadActivity.this);
        mSuffixDialogBuilder.setView(mSuffixDialogView);
        mSuffixDialogBuilder.setCancelable(false);
        mSuffixDialogBuilder.setTitle(R.string.edit_suffix);
        mSuffixDialogBuilder.setPositiveButton("确定", null);
        mSuffixDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });

        mSuffixDialog = mSuffixDialogBuilder.create();
        mSuffixDialog.setCancelable(false);
        mSuffixDialog.show();

        mSuffixDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText suffix = (EditText) mSuffixDialogView.findViewById(R.id.suffix);

                OSChinaApi.uploadSuffix(suffix.getText().toString(), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        AppContext.showToastShort("操作失败");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        ResultBean<User> result = AppOperator.createGson().fromJson(
                                responseString, new TypeToken<ResultBean<User>>() {
                                }.getType());
                        if (result.isSuccess()) {
                            AppContext.showToastShort("操作成功");

                            User userInfo = result.getResult();

                            mSuffix.setText(getText(userInfo.getSuffix()));
                            AccountHelper.updateUserCache(userInfo);

                            mSuffixDialog.cancel();
                        } else {
                            AppContext.showToastShort(TextUtils.isEmpty(result.getMessage())
                                    ? "操作失败" : result.getMessage());
                        }

                    }
                });
            }
        });
    }

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

    /**
     * take photo
     */
    private void startTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }


    /**
     * update the new picture
     */
    private void uploadNewPhoto(File file) {
        // 获取头像缩略图
        if (file == null || !file.exists() || file.length() == 0) {
            AppContext.showToast(getString(R.string.title_icon_null));
        } else {
            mIsUploadIcon = true;
            this.mCacheFile = file;
            OSChinaApi.updateUserIcon(file, requestUserInfoHandler);
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        try {
            startTakePhoto();
        } catch (Exception e) {
            Toast.makeText(this, R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
