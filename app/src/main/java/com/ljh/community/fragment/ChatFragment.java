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
import android.widget.Toast;

import com.ljh.community.LoginActivity;
import com.ljh.community.R;

import com.ljh.community.BaseFragment.BaseFragment;
import com.ljh.community.adapter.ChatsAdapter;
import com.ljh.community.model.Chat;
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

public class ChatFragment extends BaseFragment {
    private static final String TAG = ChatFragment.class.getSimpleName();
    private List<Chat> chatsList;
    private RecyclerView rvChats;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Integer userId;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_chat, null);
        rvChats = view.findViewById(R.id.rv_chats);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_chat_fragment);
        chatsList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvChats.setLayoutManager(layoutManager);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //数据库中无数据，到网络中请求
                String address = "http://192.168.137.1/chatsAndroid";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userId", userId);
                queryFromServer(address, hashMap);
            }
        });
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        queryChats();
    }

    /**
     * 查询所有私信列表信息，优先从数据库中查询，如果没有查询则再去服务器上查询
     */
    private void queryChats() {
        //获得当前用户的id
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String loginEmail = sharedPreferences.getString("LoginEmail", null);

        if (!loginEmail.isEmpty()) {
            //当前用户已登录
            List<User> users = DataSupport.where("email=?", loginEmail).find(User.class);
            User user = users.get(0);
            userId = user.getUserId();
            //从数据库中查询数据
            Log.i(TAG, "queryChats: ");
            chatsList = DataSupport.where("userId=?", userId.toString()).find(Chat.class);
            if (chatsList.isEmpty()) {
                //数据库中无数据，到网络中请求
                String address = "http://192.168.137.1/chatsAndroid";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userId", userId);
                queryFromServer(address, hashMap);
            } else {
                setChatsAdapter();
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "请登录后查看", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    /**
     * 从网络中请求数据，并调用对应方法进行处理
     *
     * @param address
     * @param hashMap
     */
    private void queryFromServer(String address, HashMap<String, Object> hashMap) {
        Log.i(TAG, "queryFromServer: ");
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
                String responseText = response.body().string();
                Log.i(TAG, "onResponse: responseText:" + responseText);
                int result = -1; //默认为网络错误
                result = Utility.handleChatsResponse(responseText);
                if (result == 1) {
                    //设置数据适配器
                    chatsList = DataSupport.where("userId=?", userId.toString()).find(Chat.class);
                    Log.i(TAG, "onResponse: chatsLists?" + chatsList.isEmpty());
                    setChatsAdapter();
                } else if (result == 0) {
                    //没有数据，则提示没有数据
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "暂无私信！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //没有数据，则提示没有数据
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void setChatsAdapter() {
        final ChatsAdapter adapter = new ChatsAdapter(chatsList);
        adapter.setOnItemClickListener(new ChatsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(mContext, "here " + position, Toast.LENGTH_SHORT).show();
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                rvChats.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
