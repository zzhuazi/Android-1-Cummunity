package com.ljh.community.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ljh.community.util.HttpUtil;
import com.ljh.community.util.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ArticleService extends Service {

    public ArticleService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                int section_id = sharedPreferences.getInt("Section_id", 15);
                //到服务器中查询数据
                String address = "http://192.168.137.1/sectionJSON?sectionId=" + section_id;
                HttpUtil.sendOkHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //通过runOnUIThread()方法回到主线程处理逻辑
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getBaseContext(), "网络错误，加载失败", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                        stopSelf();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        stopSelf();
                    }
                });
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
