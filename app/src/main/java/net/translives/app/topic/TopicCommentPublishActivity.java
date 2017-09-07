package net.translives.app.topic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import net.translives.app.R;
import net.translives.app.account.AccountHelper;
import net.translives.app.account.LoginActivity;
import net.translives.app.base.BaseBackActivity;
import net.translives.app.bean.Topic;
import net.translives.app.bean.TopicActive;

import java.io.File;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class TopicCommentPublishActivity extends BaseBackActivity {

    public static void show(Context context,TopicActive active) {
        // Check login before show
        if (!AccountHelper.isLogin()) {
            LoginActivity.show(context);
            return;
        }

        Intent intent = new Intent(context, TopicCommentPublishActivity.class);

        intent.putExtra("active", active);

        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        // hide the software
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return R.layout.activity_topic_comment_publish;
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked", "ResultOfMethodCallIgnored"})
    @Override
    protected void initWidget() {
        super.initWidget();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle == null) bundle = new Bundle();

        TopicActive active = (TopicActive)intent.getSerializableExtra("active");
        bundle.putSerializable("active", active);

        TopicCommentPublishFragment fragment = new TopicCommentPublishFragment();
        // init the args bounds
        fragment.setArguments(bundle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_topic_comment_publish, fragment);
        trans.commit();
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
