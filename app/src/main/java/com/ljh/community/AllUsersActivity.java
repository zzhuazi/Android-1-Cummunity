package com.ljh.community;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.community.adapter.AllUsersAdapter;
import com.ljh.community.adapter.UsersAdapter;
import com.ljh.community.model.User;
import com.ljh.community.util.HttpUtil;
import com.ljh.community.util.LogUtil;
import com.ljh.community.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AllUsersActivity extends AppCompatActivity {
    private static final String TAG = AllUsersActivity.class.getSimpleName();
    private RecyclerView rvAllUser;
    private List<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        rvAllUser = findViewById(R.id.rv_alluser);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        rvAllUser.setLayoutManager(layoutManager);

        mUserList = new ArrayList<>();

        initData();
    }

    private void initData() {
        DownAllUsersTask downAllUsersTask = new DownAllUsersTask();
        downAllUsersTask.execute();
        mUserList = DataSupport.findAll(User.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AllUsersAdapter adapter = new AllUsersAdapter(mUserList);
                adapter.setOnItemClickListener(new AllUsersAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //开启单个用户信息actionvy
                        TextView textView = (TextView) view;
                        String message = (String) textView.getText();
                        Toast.makeText(AllUsersActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
                rvAllUser.setAdapter(adapter);
            }
        });
    }

    public class DownAllUsersTask extends AsyncTask<Void, Void, List<User>> {
        /**
         * 开启新线程请求数据前的准备
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 新线程处理 请求数据
         * @param voids
         * @return
         */
        @Override
        protected List<User> doInBackground(Void... voids) {
            String address = "http://192.168.137.1/usersAndroid";
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("currentPage", 1);
            HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.i(TAG, "Fail");
                    //通过runOnUIThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.i(TAG, "response");
                    String responseText = response.body().string();
                    int result = Utility.handleAllUsersResponse(responseText);
                    //如果回传成功，根据type，调用对应的处理方法
                    if (result == 0) {
                        //没有数据，则提示没有数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AllUsersActivity.this, "暂无文章，请查看其他板块！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            return null;
        }

        /**
         * 数据请求完后 页面处理
         * @param users
         */
        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
        }
    }

}
