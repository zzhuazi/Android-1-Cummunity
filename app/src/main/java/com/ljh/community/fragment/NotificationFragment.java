package com.ljh.community.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.community.ArticleActivity;
import com.ljh.community.R;

import com.ljh.community.BaseFragment.BaseFragment;
import com.ljh.community.adapter.NotificationAdapter;
import com.ljh.community.adapter.NotificationTypeAdapter;
import com.ljh.community.model.Article;
import com.ljh.community.model.Notification;
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

public class NotificationFragment extends BaseFragment {
    private static final String TAG = NotificationFragment.class.getSimpleName();
    private List<Notification> mNotificationList;
    private List<String> mTypeList;
    private RecyclerView rvTypeNotification;
    private RecyclerView rvNotifications;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Integer type;
    private Integer userId;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_notification, null);
        rvTypeNotification = view.findViewById(R.id.rv_type_notification);
        rvNotifications = view.findViewById(R.id.rv_notifications_notifications);
        mNotificationList = new ArrayList<>();
        mTypeList = new ArrayList<>();
        //设置layoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTypeNotification.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(layoutManager1);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_notification_fragment);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String address = "http://192.168.137.1/notificationsAndroid";
                HashMap<String, Object> hashMap = new HashMap<>();
                Log.i(TAG, "queryNotification: userId:::::" + userId);
                hashMap.put("userId", userId);
                hashMap.put("type", type);
                queryFromServer(address, hashMap);
            }
        });
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        mTypeList.add("所有通知");
        mTypeList.add("未读通知");
        mTypeList.add("已读通知");
        setNotificationTypeAdapter();
        queryNotification();
    }

    private void setNotificationTypeAdapter() {
        final NotificationTypeAdapter adapter = new NotificationTypeAdapter(mTypeList);
        adapter.setOnItemClickListener(new NotificationTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Boolean> isClicks = adapter.getIsClicks();
                for (int i = 0; i < isClicks.size(); i++) {
                    isClicks.set(i, false);
                }
                isClicks.set(position, true);
                adapter.notifyDataSetChanged();

                type = position;//0为全部，1为未读，2为已读
                //设置通知列表为空
                rvNotifications.setAdapter(null);
                //查找通知
                if (type == 0) {
                    queryNotification();
                } else if (type == 1) {
                    mNotificationList.clear();
                    mNotificationList = DataSupport.where("receiverId=? and isReaded='false'", userId.toString()).find(Notification.class);
                    if (mNotificationList.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "暂时无数据", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        setNotificationsAdapter();
                    }
                } else if (type == 2) {
                    mNotificationList.clear();
                    mNotificationList = DataSupport.where("receiverId=? and isReaded='true'", userId.toString()).find(Notification.class);
                    if (mNotificationList.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "暂时无数据", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        setNotificationsAdapter();
                    }
                }

            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvTypeNotification.setAdapter(adapter);
            }
        });
        type = 0;//设置type为所有通知
    }


    /**
     * 查询当前登录用户的所有通知，优先从数据库中查询，如果数据库中无数据则从服务器中查询
     */
    private void queryNotification() {
        //获得当前用户的Id
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String loginEmail = sharedPreferences.getString("LoginEmail", null);
        Log.i(TAG, "queryNotification: loginEmail:" + loginEmail);
        if (!loginEmail.isEmpty()) {
            List<User> users = DataSupport.where("email=?", loginEmail).find(User.class);
            User user = users.get(0);
            userId = user.getUserId();
            //查找all notification，
            mNotificationList = DataSupport.where("receiverId=?", userId.toString()).find(Notification.class);
            Log.i(TAG, "queryNotification: notifications isEm?" + mNotificationList.isEmpty());
            if (mNotificationList.isEmpty()) {
                //if(为空)
                //无数据，则到网络中请求
                String address = "http://192.168.137.1/notificationsAndroid";
                HashMap<String, Object> hashMap = new HashMap<>();
                Log.i(TAG, "queryNotification: userId:::::" + userId);
                hashMap.put("userId", userId);
                hashMap.put("type", type);
                queryFromServer(address, hashMap);
            } else {
                //设置Adapter
                setNotificationsAdapter();
            }
        } else {
            //没有登录，没有通知数据
        }
    }

    private void setNotificationsAdapter() {
        final NotificationAdapter adapter = new NotificationAdapter(mNotificationList);
        adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final TextView textView = (TextView) view;
                Notification notification = mNotificationList.get(position);
                Integer articleId = notification.getArticleId();
                Intent intent = new Intent();
                intent.putExtra("articleId", articleId.toString());
                intent.setClass(getContext(), ArticleActivity.class);
                Log.i(TAG, "onItemClick: " + intent.getStringExtra("articleId"));
                startActivity(intent);
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvNotifications.setAdapter(adapter);
            }
        });
    }

    /**
     * 从网络中请求数据，并调用对应方法进行处理
     *
     * @param address
     */
    private void queryFromServer(String address, HashMap<String, Object> hashMap) {
        Log.d(TAG, "queryFromServer: ");
        //发送访问请求，并获得回传信息
        HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG, "Fail");
                //通过runOnUIThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: ");
                String responseText = response.body().string();
                Log.i(TAG, "onResponse: responseText:" + responseText);
                int result = -1; //默认为网络错误
                //处理回传信息
                result = Utility.handleNotificationsResponse(responseText);
                //如果回传成功，根据type，调用对应的处理方法
                if (result == 1) {
                    //设置数据适配器，可调用querySections，重复处理
                    mNotificationList = DataSupport.where("receiverId=?", userId.toString()).find(Notification.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvNotifications.setAdapter(null);
                            setNotificationsAdapter();

                        }
                    });
                } else if (result == 0) {
                    //没有数据，则提示没有数据
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "暂无通知，请查看其他板块！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
}
