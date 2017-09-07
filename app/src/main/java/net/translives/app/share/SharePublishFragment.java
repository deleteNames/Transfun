package net.translives.app.share;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.translives.app.AppContext;
import net.translives.app.AppOperator;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.api.OSChinaApi;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.base.fragments.BaseFragment;
import net.translives.app.bean.Attach;
import net.translives.app.bean.base.ResultBean;
import net.translives.app.media.SelectImageActivity;
import net.translives.app.media.SelectOptions;
import net.translives.app.util.BitmapUtil;
import net.translives.app.util.DialogHelper;
import net.translives.app.util.PicturesCompressor;
import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;
import net.translives.app.widget.PicturesPreviewer;
import net.translives.app.widget.richedit.RichEditText;
import net.translives.app.widget.richedit.TextWatcherAdapter;

import java.io.File;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;



/**
 * 发布动弹界面实现
 */
@SuppressWarnings("WeakerAccess")
public class SharePublishFragment extends BaseFragment implements View.OnClickListener {

    public static final int MAX_TITLE_LENGTH = 50;
    public static final int MAX_TEXT_LENGTH = 2000;
    public static final int REQUEST_CODE_SELECT_FRIENDS = 0x0001;
    public static final int REQUEST_CODE_SELECT_TOPIC = 0x0002;

    ProgressDialog dialog;

    @Bind(R.id.edit_title)
    EditText mEditTitle;

    @Bind(R.id.edit_content)
    RichEditText mEditContent;

    //@Bind(R.id.recycler_images)
    //PicturesPreviewer mLayImages;

    @Bind(R.id.txt_title_tip)
    TextView mTitleTip;

    @Bind(R.id.txt_content_tip)
    TextView mContentTip;

    @Bind(R.id.icon_back)
    View mIconBack;

    @Bind(R.id.icon_send)
    View mIconSend;


    private boolean titleCheck;
    private boolean contentCheck;
    //private EmojiKeyboardFragment mEmojiKeyboard;

    public SharePublishFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_share_publish;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        titleCheck = false;
        contentCheck = false;

        mTitleTip.setVisibility(View.GONE);
        mContentTip.setVisibility(View.GONE);


        // add text change listener
        mEditTitle.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final int len = s.length();
                final int surplusLen = len - MAX_TITLE_LENGTH;

                // checkShare the indicator state
                //String content = s.toString();
                //content = content.trim();

                if(len < 3){

                    // show
                    if (mTitleTip.getVisibility() != View.VISIBLE) {
                        mTitleTip.setVisibility(View.VISIBLE);
                    }

                    mTitleTip.setText("输入字数太少");
                    mTitleTip.setTextColor(getResources().getColor(R.color.tweet_indicator_text_color_error));

                    titleCheck = false;
                }else{
                    if (surplusLen > 0) {

                        // show
                        if (mTitleTip.getVisibility() != View.VISIBLE) {
                            mTitleTip.setVisibility(View.VISIBLE);
                        }

                        mTitleTip.setText("超出字数限制"+surplusLen+"个字");
                        //noinspection deprecation
                        mTitleTip.setTextColor(getResources().getColor(R.color.tweet_indicator_text_color_error));

                        titleCheck = false;
                    } else {
                        // hide
                        if (mTitleTip.getVisibility() != View.GONE) {
                            mTitleTip.setVisibility(View.GONE);
                        }

                        titleCheck = true;
                    }
                }

                // set the send icon state
                setSendIconStatus();
            }
        });

        // add text change listener
        mEditContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final int len = s.length();
                final int surplusLen = len - MAX_TEXT_LENGTH;

                // checkShare the indicator state
                if (surplusLen > 0) {

                    // show
                    if (mContentTip.getVisibility() != View.VISIBLE) {
                        mContentTip.setVisibility(View.VISIBLE);
                    }

                    mContentTip.setText("超出字数限制"+surplusLen+"个字");
                    //noinspection deprecation
                    mContentTip.setTextColor(getResources().getColor(R.color.tweet_indicator_text_color_error));

                    contentCheck = false;
                } else {
                    // hide
                    if (mContentTip.getVisibility() != View.GONE) {
                        mContentTip.setVisibility(View.GONE);
                    }

                    contentCheck = true;
                }

                // set the send icon state
                setSendIconStatus();
            }
        });

        // Show keyboard
        //showSoftKeyboard(mEditTitle);
        //showSoftKeyboard(mEditContent);
    }

    private void setSendIconStatus() {
        if (contentCheck && titleCheck) {
            mIconSend.setEnabled(true);
        }
    }


    // 用于拦截后续的点击事件
    private long mLastClickTime;

    @OnClick({ R.id.icon_back,
            R.id.icon_send, R.id.edit_content,R.id.iv_picture})//R.id.iv_picture, R.id.iv_mention, R.id.iv_tag,R.id.iv_emoji,
    @Override
    public void onClick(View v) {
        // 用来解决快速点击多个按钮弹出多个界面的情况
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 1000)
            return;
        mLastClickTime = nowTime;

        try {
            switch (v.getId()) {
                case R.id.iv_picture:
                    hideSoftKeyboard();

                    SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                            .setHasCam(true)
                            .setSelectCount(1)
                            .setCallback(new SelectOptions.Callback() {
                                @Override
                                public void doSelected(String[] images) {
                                    String path = images[0];
                                    uploadAttach(path);
                                }
                            }).build());
                    break;
                case R.id.icon_back:
                    finish();
                    break;
                case R.id.icon_send:
                    publish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FRIENDS:
                    // Nun Do handleSelectFriendsResult(data);
                    break;
                case REQUEST_CODE_SELECT_TOPIC:
                    // Nun Do handleSelectTopicResult(data);
                    break;
            }
        }

    }

    // Max upload 860KB
    private static final long MAX_UPLOAD_LENGTH = 860 * 1024;

    /**
     * 保存文件到缓存中
     *
     * @param path    原始路径
     * @return 转存后的路径
     */
    private String saveImageToCache( String path) {
        String tempFile = null;


        byte[] buffer = new byte[BitmapUtil.DEFAULT_BUFFER_SIZE];
        BitmapFactory.Options options = BitmapUtil.createOptions();

        String ext = null;
        try {
            int lastDotIndex = path.lastIndexOf(".");
            if (lastDotIndex != -1)
                ext = path.substring(lastDotIndex + 1).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(ext)) {
            ext = "jpg";
        }

        String cacheDir = String.format("%s/Pictures", getContext().getCacheDir().getAbsolutePath());
        try {
            tempFile = String.format("%s/IMG_%s.%s", cacheDir, SystemClock.currentThreadTimeMillis(), ext);
            if (PicturesCompressor.compressImage(path, tempFile, MAX_UPLOAD_LENGTH,
                    80, 1280, 1280 * 6, buffer, options, true)) {
                TLog.log("OPERATOR doImage:" + tempFile + " " + new File(tempFile).length());
                // verify the picture ext.
                tempFile = PicturesCompressor.verifyPictureExt(tempFile);
            }

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        TLog.log("OPERATOR compressImage error:" + path);

        return tempFile;
    }

    private void uploadAttach(String file_path) {

        String new_file = saveImageToCache(file_path);
        File file = new File(new_file);

        // 获取头像缩略图
        if (file == null || !file.exists() || file.length() == 0) {
            AppContext.showToast(getString(R.string.title_icon_null));
        } else {
            OSChinaApi.updateAttach(file, new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    notifyMsg(R.string.attach_uploading);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    setError(R.string.attach_upload_failed,responseString);
                    TLog.log(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        Type type = new TypeToken<ResultBean<Attach>>() {
                        }.getType();

                        TLog.log(responseString);
                        ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                        if (resultBean.isSuccess()) {
                            Attach attach = (Attach) resultBean.getResult();
                            insertAttach(attach);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(statusCode, headers, responseString, e);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                }
            });
        }

    }

    /**
     * 插入图片
     */
    private void insertAttach(final Attach attach){

        Glide.with(getContext())
                .load(attach.getHref())
                .asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                ImageSpan imageSpan;

                float screenWidth = TDevice.getScreenWidth() - TDevice.dp2px(32);
                if(bitmap.getWidth() > screenWidth){
                    int width = (int)screenWidth;
                    int height = (int)(bitmap.getHeight()* width /bitmap.getWidth());
                    Bitmap mbitmap = BitmapUtil.scaleBitmap(bitmap,width,height,true);

                    imageSpan = new ImageSpan(getContext(), mbitmap);
                }else{
                    // 根据Bitmap对象创建ImageSpan对象
                    imageSpan = new ImageSpan(getContext(), bitmap);
                }
                // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                String tempUrl = "<p><img src=\"" + attach.getHref() + "\" /></p>";
                SpannableString spannableString = new SpannableString(tempUrl);
                // 用ImageSpan对象替换你指定的字符串
                spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 将选择的图片追加到EditText中光标所在位置
                int index = mEditContent.getSelectionStart(); // 获取光标所在位置
                Editable edit_text = mEditContent.getEditableText();
                if (index < 0 || index >= edit_text.length()) {
                    edit_text.append("\r\n");
                    edit_text.append(spannableString);
                    edit_text.append("\r\n");
                } else {
                    edit_text.insert(index, spannableString);
                }
            }
        });

    }

    private void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEditContent.getWindowToken(), 0);
    }


    public void publish() {

        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(mContext);
            return;
        }

        String title = mEditTitle.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(title.trim())) {
            AppContext.showToastShort(R.string.tip_title_empty);
            return;
        }

        if (title.length() > SharePublishFragment.MAX_TITLE_LENGTH) {
            AppContext.showToastShort(R.string.tip_title_too_long);
            return;
        }

        String content = mEditContent.getText().toString();
       if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
            AppContext.showToastShort(R.string.tip_content_empty);
            return;
        }

        if (content.length() > SharePublishFragment.MAX_TEXT_LENGTH) {
            AppContext.showToastShort(R.string.tip_content_too_long);
            return;
        }

        OSChinaApi.pubQuestion(1,title,content, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                notifyMsg(R.string.tweet_publishing);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String error = "";
                String response = responseString == null ? "" : responseString;
                if (throwable != null) {
                    throwable.printStackTrace();
                    error = throwable.getMessage();
                }

                TLog.error(String.format("Publish tweet onFailure, statusCode:[%s] responseString:%s throwable:%s",
                        statusCode, response, error));
                setError(R.string.tweet_publish_failed);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        setSuccess();
                    } else {
                        TLog.error(resultBean.getMessage());
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    TLog.error("response parse error「" + responseString + "」");
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });

        // Toast
        //AppContext.showToast(R.string.tweet_publishing_toast);

    }


    public void notifyMsg(int resId, Object... values){

        if(dialog == null) {
            dialog = DialogHelper.getProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
            dialog.setCanceledOnTouchOutside(false);
        }

        if(dialog.isShowing()){
            String message = getActivity().getString(resId);
            dialog.setMessage(message);
        }else{
            dialog.show();
        }
    }

    public void setSuccess(){
        dialog.dismiss();

        String message = getActivity().getString(R.string.tweet_publish_success);
        AlertDialog.Builder alertDialog = DialogHelper.getMessageDialog(getActivity(),message);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){

            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialog.show();
    }

    public void setError(int resId, Object... values){
        dialog.dismiss();

        String message = getActivity().getString(resId);
        //AlertDialog.Builder alertDialog = DialogHelper.getMessageDialog(getActivity(),message);
        //alertDialog.show();

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void finish() {

        // finish
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseBackActivity) {
            ((BaseBackActivity) activity).onSupportNavigateUp();
        }
    }
}
