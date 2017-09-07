package net.translives.app.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.translives.app.AppContext;
import net.translives.app.AppOperator;
import net.translives.app.api.OSChinaApi;
import net.translives.app.bean.SimpleBackPage;
import net.translives.app.news.NewsDetailActivity;
import net.translives.app.question.QuestionDetailActivity;
import net.translives.app.ui.SimpleBackActivity;

/**
 * Native 工具类
 * <p>
 * Author: JuQiu qiujuer@live.cn
 */
public final class AppHelper {


    /**
     * 清除app缓存
     */
    public static void clearAppCache(boolean showToast) {
        final Handler handler = showToast ? new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    AppContext.showToastShort("缓存清除成功");
                } else {
                    AppContext.showToastShort("缓存清除失败");
                }
            }
        } : null;
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    AppContext.getInstance().clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                if (handler != null)
                    handler.sendMessage(msg);
            }
        });
    }

    @SuppressLint("PackageManagerGetSignatures")
    public static String getSignature(Application application) {
        PackageManager pm = application.getPackageManager();
        String packageName = application.getPackageName();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Signature[] signatures = packageInfo.signatures;
        return signatures[0].toCharsString();
    }


    /**
     * show detail  method
     *
     * @param context context
     * @param type    type
     * @param id      id
     */
    public static void showDetail(Context context, int type, long id, String href) {
        switch (type) {
            case OSChinaApi.CATALOG_ALL:
                showUrlRedirect(context, id, href);
                break;
            case OSChinaApi.CATALOG_QUESTION:
                //问答
                QuestionDetailActivity.show(context, id);
                break;
            default:
                //6.资讯
                NewsDetailActivity.show(context, id);
                break;
        }
    }

    private static void showUrlRedirect(Context context, long id, String url) {
        URLUtils.parseUrl(context, url);
    }

    /**
     * 显示用户的问答列表
     *
     * @param context context
     * @param uid     authorId
     */
    public static void showUserQuestion(Context context, long uid) {
        Bundle args = new Bundle();
        args.putLong("user_id", uid);
        showSimpleBack(context, SimpleBackPage.MY_QUESTION, args);
    }

    public static void showUserCollection(Context context, long uid) {
        Bundle args = new Bundle();
        args.putLong("user_id", uid);
        showSimpleBack(context, SimpleBackPage.MY_COllECTION, args);
    }

    public static void showUserAnswer(Context context, long uid) {
        Bundle args = new Bundle();
        args.putLong("user_id", uid);
        showSimpleBack(context, SimpleBackPage.MY_ANSWER, args);
    }

    public static void showUserFollow(Context context, long uid) {
        Bundle args = new Bundle();
        args.putLong("user_id", uid);
        showSimpleBack(context, SimpleBackPage.MY_FOLLOW, args);
    }

    public static void showUserTopic(Context context, long uid) {
        Bundle args = new Bundle();
        args.putLong("user_id", uid);
        showSimpleBack(context, SimpleBackPage.MY_TOPIC, args);
    }

    public static void showSimpleBack(Context context, SimpleBackPage page) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void showSimpleBack(Context context, SimpleBackPage page,
                                      Bundle args) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 打开外置的浏览器
     *
     * @param context
     * @param url
     */
    public static void openExternalBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(Intent.createChooser(intent, "选择打开的应用"));
    }
}
