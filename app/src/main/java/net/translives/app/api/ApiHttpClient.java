package net.translives.app.api;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.translives.app.AppContext;
import net.translives.app.Setting;
import net.translives.app.account.AccountHelper;
import net.translives.app.util.SecureUtil;
import net.translives.app.util.StringUtils;
import net.translives.app.util.TLog;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.protocol.HttpContext;

@SuppressWarnings("WeakerAccess")
public class ApiHttpClient {
    public final static String HOST = "api.translives.net";

    public static String API_URL;
    public static String DEVICE_ID;

    private static AsyncHttpClient CLIENT;

    private ApiHttpClient() {
    }

    /**
     * 初始化网络请求，包括Cookie的初始化
     *
     * @param context AppContext
     */
    public static void init(Application context) {
        API_URL = Setting.getServerUrl(context) + "%s";
        DEVICE_ID = getDeviceId(context);
        CLIENT = null;
        AsyncHttpClient client = new AsyncHttpClient();
        //client.setCookieStore(new PersistentCookieStore(context));
        // Set
        ApiHttpClient.setHttpClient(client, context);
        // Set Cookie
        setCookieHeader(AccountHelper.getCookie());
    }

    public static AsyncHttpClient getHttpClient() {
        return CLIENT;
    }
/*
    public static void delete(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.delete(getAbsoluteApiUrl(partUrl), handler);
        log("DELETE " + partUrl);
    }
*/
    public static RequestParams putSystemParams(RequestParams params) {

        long time = System.currentTimeMillis();
        params.put("_time",time);
        params.put("_uuid",DEVICE_ID);
        params.put("_signature",SecureUtil.getSign(String.valueOf(time)));

        return params;
    }

    public static void get(String partUrl, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params = putSystemParams(params);
        get(getAbsoluteApiUrl(partUrl),params, handler);
    }

    public static void get(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        if(!params.has("_signature")){
            params = putSystemParams(params);
        }

        CLIENT.get(getAbsoluteApiUrl(partUrl), params, handler);
        log("GET " + partUrl+ "?" + params.toString());
    }

    public static String getAbsoluteApiUrl(String partUrl) {
        String url = partUrl;
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) {
            url = String.format(API_URL, partUrl);
        }
        //log("request:" + url);
        return url;
    }

    public static void getDirect(String url, AsyncHttpResponseHandler handler) {
        CLIENT.get(url, handler);
        log("GET " + url);
    }

    public static void log(String log) {
        TLog.log("ApiHttpClient", log);
    }

    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params = putSystemParams(params);
        post(getAbsoluteApiUrl(partUrl),params, handler);
    }

    public static void post(String partUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        if(!params.has("_signature")){
            params = putSystemParams(params);
        }

        CLIENT.post(getAbsoluteApiUrl(partUrl), params, handler);
        log("POST " + partUrl + "?" + params);
    }
/*
    public static void put(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.put(getAbsoluteApiUrl(partUrl), handler);
        log("PUT " + partUrl);
    }

    public static void put(String partUrl, RequestParams params,
                           AsyncHttpResponseHandler handler) {
        CLIENT.put(getAbsoluteApiUrl(partUrl), params, handler);
        log("PUT " + partUrl + "?" + params.toString());
    }
*/
    public static void setHttpClient(AsyncHttpClient c, Application application) {
        c.addHeader("Accept-Language", Locale.getDefault().toString());
        c.addHeader("Host", HOST);
        c.addHeader("Connection", "Keep-Alive");
        c.addHeader("X-Requested-With", "XMLHttpRequest");

        //noinspection deprecation
        c.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        // setUserAgent
        c.setUserAgent(getUserAgent(AppContext.getInstance()));
        CLIENT = c;
        initSSL(CLIENT);
    }

    public static void setCookieHeader(String cookie) {
        if (!TextUtils.isEmpty(cookie))
            CLIENT.addHeader("Cookie", cookie);
        log("setCookieHeader:" + cookie);
    }

    /**
     * 销毁当前AsyncHttpClient 并重新初始化网络参数，初始化Cookie等信息
     *
     * @param appContext AppContext
     */
    public static void destroyAndRestore(Application appContext) {
        cleanCookie();
        CLIENT = null;
        init(appContext);
    }

    public static void cleanCookie() {
        // first clear store
        // new PersistentCookieStore(AppContext.getInstance()).clear();
        // clear header
        AsyncHttpClient client = CLIENT;
        if (client != null) {
            HttpContext httpContext = client.getHttpContext();
            CookieStore cookies = (CookieStore) httpContext
                    .getAttribute(HttpClientContext.COOKIE_STORE);
            // 清理Async本地存储
            if (cookies != null) {
                cookies.clear();
            }
            // 清理当前正在使用的Cookie
            client.removeHeader("Cookie");
        }
        log("cleanCookie");
    }

    /**
     * 从AsyncHttpClient自带缓存中获取CookieString
     *
     * @param client AsyncHttpClient
     * @return CookieString
     */
    private static String getClientCookie(AsyncHttpClient client) {
        String cookie = "";
        if (client != null) {
            HttpContext httpContext = client.getHttpContext();
            CookieStore cookies = (CookieStore) httpContext
                    .getAttribute(HttpClientContext.COOKIE_STORE);

            if (cookies != null && cookies.getCookies() != null && cookies.getCookies().size() > 0) {
                for (Cookie c : cookies.getCookies()) {
                    cookie += (c.getName() + "=" + c.getValue()) + ";";
                }
            }
        }
        log("getClientCookie:" + cookie);
        return cookie;
    }

    /**
     * 得到当前的网络请求Cookie，
     * 登录后触发
     *
     * @param headers Header
     */
    public static String getCookie(Header[] headers) {
        String cookie = getClientCookie(ApiHttpClient.getHttpClient());
        if (TextUtils.isEmpty(cookie)) {
            cookie = "";
            if (headers != null) {
                for (Header header : headers) {
                    String key = header.getName();
                    String value = header.getValue();
                    if (key.contains("Set-Cookie"))
                        cookie += value + ";";
                }
                if (cookie.length() > 0) {
                    cookie = cookie.substring(0, cookie.length() - 1);
                }
            }
        }

        log("getCookie:" + cookie);
        return cookie;
    }

    public static String getDeviceId(Application appContext) {

        SharedPreferences sp = Setting.getSettingPreferences(appContext);
        String uniqueID = sp.getString(Setting.KEY_APP_UNIQUE_ID, null);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sp.edit().putString(Setting.KEY_APP_UNIQUE_ID, uniqueID);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
        return uniqueID;
    }

    /**
     * 获得请求的服务端数据的userAgent
     * 客户端唯一标识
     *
     * @param appContext
     * @return
     */
    static String getUserAgent(Application appContext) {
        // WebSettings.getDefaultUserAgent(appContext)

        String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
        String osVer = version.length() > 0 ? version : "1.0";

        String model = Build.MODEL;
        String id = Build.ID; // "MASTER" or "M4-rc20"
        if (id.length() > 0) {
            model += " Build/" + id;
        }

        String format = "Translives.NET/1.0 ( Android %s; %s; %s)";
        String ua = String.format(format, osVer, model, DEVICE_ID);
        ApiHttpClient.log("getUserAgent:" + ua);
        return ua;
    }

    private static void initSSL(AsyncHttpClient client) {
        try {
            /// We initialize a default Keystore
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // We load the KeyStore
            trustStore.load(null, null);
            // We initialize a new SSLSocketFacrory
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            // We set that all host names are allowed in the socket factory
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // We set the SSL Factory
            client.setSSLSocketFactory(socketFactory);
            // We initialize a GET http request
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        @SuppressWarnings("WeakerAccess")
        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
