package net.translives.app.question;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Size;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import net.translives.app.BuildConfig;
import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.base.BaseBackActivity;

import net.translives.app.util.CollectionUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class QuestionPublishActivity extends BaseBackActivity {

    public static void show(Context context) {
        show(context, null);
    }

    public static void show(Context context, View view) {
        show(context, view, null);
    }

    public static void show(Context context, View view, String defaultContent) {
        int[] location = new int[]{0, 0};
        int[] size = new int[]{0, 0};

        if (view != null) {
            view.getLocationOnScreen(location);
            size[0] = view.getWidth();
            size[1] = view.getHeight();
        }

        show(context, location, size, defaultContent);
    }


    public static void show(Context context, @Size(2) int[] viewLocationOnScreen,
                            @Size(2) int[] viewSize, String defaultContent) {
        // Check login before show
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(context);
            return;
        }

        Intent intent = new Intent(context, QuestionPublishActivity.class);

        if (viewLocationOnScreen != null) {
            intent.putExtra("location", viewLocationOnScreen);
        }
        if (viewSize != null) {
            intent.putExtra("size", viewSize);
        }
        if (defaultContent != null) {
            intent.putExtra("defaultContent", defaultContent);
        }

        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        // hide the software
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return R.layout.activity_question_publish;
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked", "ResultOfMethodCallIgnored"})
    @Override
    protected void initWidget() {
        super.initWidget();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle == null) bundle = new Bundle();
        // Read other data
        readFastShareByOther(bundle, intent);

        QuestionPublishFragment fragment = new QuestionPublishFragment();
        // init the args bounds
        fragment.setArguments(bundle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_question_publish, fragment);
        trans.commit();
    }

    /**
     * 读取快速分享到当前界面的内容
     *
     * @param intent 需要写入源
     */
    private void readFastShareByOther(Bundle bundle, Intent intent) {
        // Check
        if (intent == null)
            return;
        String type = intent.getType();
        if (TextUtils.isEmpty(type))
            return;

        //判断当前分享的内容是文本，还是图片
        if ("text/plain".equals(type)) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            bundle.putString("defaultContent", text);
        } else if (type.startsWith("image/")) {
            ArrayList<String> uris = new ArrayList<>();
            Object obj = intent.getExtras().get(Intent.EXTRA_STREAM);
            if (obj instanceof Uri) {
                Uri uri = (Uri) obj;
                String decodePath = decodePath(uri);
                if (decodePath != null)
                    uris.add(decodePath);
            } else {
                try {
                    @SuppressWarnings("unchecked")
                    ArrayList<Uri> list = (ArrayList<Uri>) obj;
                    //大于9张图片的分享，直接只使用前9张
                    if (list != null && list.size() > 0) {
                        for (int i = 0, len = list.size(); i < len; i++) {
                            if (i > 9) {
                                break;
                            }
                            String decodePath = decodePath(list.get(i));
                            if (decodePath != null)
                                uris.add(decodePath);
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace();
                }
            }
            if (uris.size() > 0) {
                String[] paths = CollectionUtil.toArray(uris, String.class);
                bundle.putStringArray("defaultImages", paths);
            }
        }
    }

    /**
     * 通过uri当中的唯一id搜索本地相册图片，是否真的存在。然后返回真实的path路径
     *
     * @param uri rui
     * @return path
     */
    private String decodePath(Uri uri) {
        String decodePath = null;
        String uriPath = uri.toString();

        if (uriPath != null && uriPath.startsWith("content://")) {

            int id = Integer.parseInt(uriPath.substring(uriPath.lastIndexOf("/") + 1, uriPath.length()));

            Uri tempUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media.DATA};
            String selection = MediaStore.Images.Media._ID + "=?";
            String[] selectionArgs = {id + ""};

            Cursor cursor = getContentResolver().query(tempUri, projection, selection, selectionArgs, null);
            try {
                while (cursor != null && cursor.moveToNext()) {
                    String temp = cursor.getString(0);
                    File file = new File(temp);
                    if (file.exists()) {
                        decodePath = temp;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

        } else {
            return uriPath;
        }
        return decodePath;
    }

    @Override
    protected void initData() {
        super.initData();
        // before the fragment show
        //registerPublishStateReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //暂不处理已在当前界面下的分享
    }

    @Override
    protected void onDestroy() {
        //unRegisterPublishStateReceiver();
        super.onDestroy();
    }
/*
    private void registerPublishStateReceiver() {
        if (mPublishStateReceiver != null)
            return;
        IntentFilter intentFilter = new IntentFilter(PublishService.ACTION_RECEIVER_SEARCH_FAILED);
        BroadcastReceiver receiver = new SearchReceiver();
        registerReceiver(receiver, intentFilter);
        mPublishStateReceiver = receiver;

        // start search
        PublishService.startActionSearchFailed(this);
    }

    private void unRegisterPublishStateReceiver() {
        final BroadcastReceiver receiver = mPublishStateReceiver;
        mPublishStateReceiver = null;
        if (receiver != null)
            unregisterReceiver(receiver);
    }

    private BroadcastReceiver mPublishStateReceiver;

    private class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PublishService.ACTION_RECEIVER_SEARCH_FAILED.equals(intent.getAction())) {
                String[] ids = intent.getStringArrayExtra(PublishService.EXTRA_IDS);
                if (ids == null || ids.length == 0)
                    return;
                //PublishQueueActivity.show(QuestionPublishActivity.this, ids);
            }
        }
    }
*/
}
