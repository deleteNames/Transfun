package net.translives.app.util;

import android.content.Context;
import net.translives.app.AppContext;
import net.translives.app.BuildConfig;
import net.translives.app.api.ApiHttpClient;

import java.util.UUID;

public class SecureUtil {

    final static String TAG = "SecureUtil";

    static {
        System.loadLibrary("native-lib");
    }

    public static byte[] encryptData(byte[] data) {
        return encryptData(AppContext.getInstance(), data);
    }

    public static byte[] decryptData(byte[] data) {
        return decryptData(AppContext.getInstance(), data);
    }

    public static String getSign(String data) {
        return getSign(AppContext.getInstance(),data);
    }

    public static String getDeviceId() {
        return ApiHttpClient.getDeviceId(AppContext.getInstance());
    }

    public static String getAppVersion() {
        return "1.0";
    }

    public static String getChannel() {
        return "translives";
    }

    public static void showToast(String tips) {
        TLog.e(TAG,tips);
        //Toast.makeText(AppContext.getInstance(), tips, Toast.LENGTH_SHORT).show();
    }

    native private static byte[] encryptData(Context context, byte[] data);
    native private static byte[] decryptData(Context context, byte[] data);
    native private static String getSign(Context context, String data);


}
