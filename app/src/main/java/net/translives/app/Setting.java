package net.translives.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import net.translives.app.BuildConfig;

import net.translives.app.util.TDevice;
import net.translives.app.util.TLog;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public final class Setting {
    public static final String KEY_SEVER_URL = "serverUrl";
    public static final String KEY_VERSION_CODE = "versionCode";
    public static final String KEY_APP_UNIQUE_ID = "appUniqueID";
    public static final String KEY_SYSTEM_CONFIG_TIMESTAMP = "systemConfigTimeStamp";
    public static final String KEY_LOCATION_INFO = "locationInfo";

    public static SharedPreferences getSettingPreferences(Context context) {
        return context.getSharedPreferences(Setting.class.getName(), Context.MODE_PRIVATE);
    }

    public static boolean checkIsNewVersion(Context context) {
        int saveVersionCode = getSaveVersionCode(context);
        int currentVersionCode = TDevice.getVersionCode();
        if (saveVersionCode < currentVersionCode) {
            updateSaveVersionCode(context, currentVersionCode);
            return true;
        }
        return false;
    }

    public static int getSaveVersionCode(Context context) {
        SharedPreferences sp = getSettingPreferences(context);
        return sp.getInt(KEY_VERSION_CODE, 0);
    }

    private static int updateSaveVersionCode(Context context, int version) {
        SharedPreferences sp = getSettingPreferences(context);
        SharedPreferences.Editor editor = sp.edit().putInt(KEY_VERSION_CODE, version);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        return version;
    }

    public static String getServerUrl(Context context) {
        SharedPreferences sp = getSettingPreferences(context);
        String url = sp.getString(KEY_SEVER_URL, null);
        TLog.error(url);

        if (TextUtils.isEmpty(url)) {
            url = BuildConfig.API_SERVER_URL;
            updateServerUrl(context, BuildConfig.API_SERVER_URL);
        }
        return url;
    }

    public static void updateServerUrl(Context context, String url) {
        SharedPreferences sp = getSettingPreferences(context);
        SharedPreferences.Editor editor = sp.edit().putString(KEY_SEVER_URL, url);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static void updateSystemConfigTimeStamp(Context context) {
        SharedPreferences sp = getSettingPreferences(context);
        SharedPreferences.Editor editor = sp.edit().putLong(KEY_SYSTEM_CONFIG_TIMESTAMP,
                System.currentTimeMillis());
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static long getSystemConfigTimeStamp(Context context) {
        SharedPreferences sp = getSettingPreferences(context);
        return sp.getLong(KEY_SYSTEM_CONFIG_TIMESTAMP, 0);
    }

    public static void updateLocationInfo(Context context, boolean hasLocation) {
        SharedPreferences sp = getSettingPreferences(context);
        SharedPreferences.Editor editor = sp.edit().putBoolean(KEY_LOCATION_INFO, hasLocation);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static boolean hasLocation(Context context) {
        SharedPreferences sp = getSettingPreferences(context);
        return sp.getBoolean(KEY_LOCATION_INFO, false);
    }
}
