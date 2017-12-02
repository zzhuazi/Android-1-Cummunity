package com.ljh.community.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/11/27.
 * http工具类
 */

public class HttpUtil {
    private static final String TAG = HttpUtil.class.getSimpleName();

    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendOkHttpPostRequest(String address, HashMap<String, Object> paramsMap, Callback callback){
        CookieManager cookieManager = new CookieManager();
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();

        //创建一个FormBody.Builder
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            //追加表单信息
            Log.i(TAG, "sendOkHttpPostRequest: key: " + key.toString());
            builder.add(key, paramsMap.get(key).toString());
        }
        //生成表单实体对象
        RequestBody formBody = builder.build();
        Log.i(TAG, "sendOkHttpPostRequest: formBody " + formBody.toString());
        Request requestPost = new Request.Builder()
                .url(address)
                .post(formBody)
                .build();
        Log.i(TAG, "sendOkHttpPostRequest: requestPost" + requestPost.toString());
        client.newCall(requestPost).enqueue(callback);
    }



    private static class CookieManager implements CookieJar{
//        Context mContext;
//
//        public CookieManager(Context mContext) {
//            this.mContext = mContext;
//            Log.i(TAG, "CookieManager: Context" + this.mContext);
//        }

        private final PersistentCookieStore cookieStore = new PersistentCookieStore(LitePalApplication.getContext());

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    Log.i(TAG, "saveFromResponse: cookie:" + item.toString());
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }

}
