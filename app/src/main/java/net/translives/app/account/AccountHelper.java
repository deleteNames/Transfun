package net.translives.app.account;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;

import net.translives.app.api.ApiHttpClient;
import net.translives.app.bean.Constants;
import net.translives.app.bean.User;
import net.translives.app.notice.NoticeManager;

import net.translives.app.util.TLog;
import net.translives.app.cache.SharedPreferencesHelper;

import cz.msebera.android.httpclient.Header;

/**
 * 账户辅助类，
 * 用于更新用户信息和保存当前账户等操作
 */
public final class AccountHelper {
    private User user;
    private Application application;
    @SuppressLint("StaticFieldLeak")
    private static AccountHelper instances;

    private AccountHelper(Application application) {
        this.application = application;
    }

    public static void init(Application application) {
        instances = new AccountHelper(application);
    }

    public static boolean isLogin() {
        return getUserId() > 0 && !TextUtils.isEmpty(getCookie());
    }

    public static String getCookie() {
        String cookie = getUser().getCookie();
        return cookie == null ? "" : cookie;
    }

    public static long getUserId() {
        return getUser().getId();
    }

    public synchronized static User getUser() {
        if (instances == null) {
            TLog.error("AccountHelper instances is null, you need call init() method.");
            return new User();
        }
        if (instances.user == null) {
            instances.user = SharedPreferencesHelper.loadFormSource(instances.application, User.class);
            TLog.d("getUser","from SharedPreferencesHelper.loadFormSource");
        }
        if (instances.user == null) {
            instances.user = new User();
            TLog.d("getUser","from new User");
        }
        return instances.user;
    }

    public static void updateUserCache(User user) {
        if (user == null)
            return;
        // 保留Cookie信息
        if (TextUtils.isEmpty(user.getCookie()) && instances.user != user)
            user.setCookie(instances.user.getCookie());

        //TLog.d("updateUserCache->",user.getCookie());
        instances.user = user;
        SharedPreferencesHelper.save(instances.application, user);
    }

    private static void clearUserCache() {
        instances.user = null;
        SharedPreferencesHelper.remove(instances.application, User.class);
    }

    public static void login(User user, Header[] headers) {
        // 更新Cookie
        String cookie = ApiHttpClient.getCookie(headers);
        user.setCookie(cookie);
        ApiHttpClient.setCookieHeader(cookie);

        // 保存缓存
        updateUserCache(user);
        // 登陆成功,重新启动消息服务
        NoticeManager.init(instances.application);
    }

    /**
     * 退出登陆操作需要传递一个View协助完成延迟检测操作
     *
     * @param view     View
     * @param runnable 当清理完成后回调方法
     */
    public static void logout(final View view, final Runnable runnable) {
        // 清除用户缓存
        clearUserCache();
        // 等待缓存清理完成
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.removeCallbacks(this);
                User user = SharedPreferencesHelper.load(instances.application, User.class);
                // 判断当前用户信息是否清理成功
                if (user == null || user.getId() <= 0) {
                    clearAndPostBroadcast(instances.application);
                    runnable.run();
                } else {
                    view.postDelayed(this, 200);
                }
            }
        }, 200);

    }

    public static void logout() {
        clearUserCache();
        clearAndPostBroadcast(instances.application);
    }

    /**
     * 当前用户信息清理完成后调用方法清理服务等信息
     *
     * @param application Application
     */
    private static void clearAndPostBroadcast(Application application) {
        // 清理网络相关
        ApiHttpClient.destroyAndRestore(application);

        // 用户退出时清理红点并退出服务
        NoticeManager.clear(application, NoticeManager.FLAG_CLEAR_ALL);
        NoticeManager.exitServer(application);

        // 清理动弹对应数据
        //CacheManager.deleteObject(application, QuestionFragment.CACHE_USER_TWEET);

        // Logout 广播
        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(application).sendBroadcast(intent);
    }
}
