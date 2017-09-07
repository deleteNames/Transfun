package net.translives.app.util;

import android.util.Log;

import net.translives.app.BuildConfig;


public class TLog {
    private static final String LOG_TAG = "OSChinaLog";
    private static boolean DEBUG = BuildConfig.DEBUG;

    private TLog() {
    }

    public static void error(String log) {
        if (DEBUG) {
            if(log != null && log.length() > 4000) {
                for(int i=0;i<log.length();i+=4000){
                    if(i+4000<log.length())
                        Log.e(LOG_TAG +"_" + i, "" + log.substring(i, i+4000));
                    else
                        Log.e(LOG_TAG +"_" + i, "" + log.substring(i, log.length()));
                }
            } else {
                Log.e(LOG_TAG, "" + log);
            }
        }
    }

    public static void log(String log) {

        if (DEBUG) {
            if(log.length() > 4000) {
                for(int i=0;i<log.length();i+=4000){
                    if(i+4000<log.length())
                        Log.i(LOG_TAG +"_" + i, "" + log.substring(i, i+4000));
                    else
                        Log.i(LOG_TAG +"_" + i, "" + log.substring(i, log.length()));
                }
            } else {
                Log.i(LOG_TAG, log);
            }
        }
    }

    public static void log(String tag, String log) {
        if (DEBUG) Log.i(tag, log);
    }

    public static void d(String tag, String log) {
        if (DEBUG) Log.d(tag, log);
    }

    public static void e(String tag, String log) {
        if (DEBUG) Log.e(tag, log);
    }

    public static void i(String tag, String log) {
        if (DEBUG) Log.i(tag, log);
    }
}
