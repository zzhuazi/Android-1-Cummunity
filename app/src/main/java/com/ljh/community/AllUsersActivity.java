package com.ljh.community;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        rvAllUser = findViewById(R.id.rv_alluser);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_all_users_activity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        rvAllUser.setLayoutManager(layoutManager);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //网络请求
                DownAllUsersTask downAllUsersTask = new DownAllUsersTask();
                downAllUsersTask.execute();
            }
        });
        mUserList = new ArrayList<>();
        initData();
    }

    private void initData() {
        mUserList = DataSupport.findAll(User.class);
        if (mUserList.size() <= 1){
            //只有当前用户，到网络中请求
            DownAllUsersTask downAllUsersTask = new DownAllUsersTask();
            downAllUsersTask.execute();
        }else{
            //设置数据适配器
            setAdapter();
        }
    }

    private void setAdapter() {
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
                        User user = mUserList.get(position);
                        Intent intent = new Intent(AllUsersActivity.this, UserActivity.class);
                        intent.putExtra("userId", user.getUserId());
                        startActivity(intent);
                    }
                });
                rvAllUser.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 下载所有用户的信息
     */
    public class DownAllUsersTask extends AsyncTask<Void, Void, List<User>> {

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
                    String responseText = response.body().string();
                    int result = Utility.handleAllUsersResponse(responseText);
                    //如果回传成功，根据type，调用对应的处理方法
                    if (result == 0 || result == -1) {
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
            List<User> users = DataSupport.findAll(User.class);
            return users;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            mUserList = users;
            super.onPostExecute(users);
            setAdapter();
        }
    }

}
