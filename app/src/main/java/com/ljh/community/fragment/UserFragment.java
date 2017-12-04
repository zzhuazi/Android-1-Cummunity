package com.ljh.community.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.ljh.community.AllUsersActivity;
import com.ljh.community.R;

import com.ljh.community.BaseFragment.BaseFragment;
import com.ljh.community.adapter.ArticlesAdapter;
import com.ljh.community.adapter.UsersAdapter;
import com.ljh.community.model.User;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends BaseFragment {
    private static final String TAG = UserFragment.class.getSimpleName();
    private List<String> buttonList;
    TextView userNameTv;
    TextView userEmailTv;
    CircleImageView circleImageView;
    RecyclerView rvUesrButton;
    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_user,null);
        userNameTv = view.findViewById(R.id.user_fragment_tv_name);
        userEmailTv = view.findViewById(R.id.user_fragment_tv_email);
        circleImageView = view.findViewById(R.id.user_image);
        rvUesrButton = view.findViewById(R.id.rv_user_button);
        buttonList = new ArrayList<>();

        //给recyclerView 设置LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvUesrButton.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userEmail = sharedPreferences.getString("userEmail", null);
        List<User> users = DataSupport.where("email=?",userEmail).find(User.class);
        User user = users.get(0);
        userNameTv.setText(user.getName());
        userEmailTv.setText(user.getEmail());
        String address = "http://192.168.137.1/" + user.getAvatar();
        Glide.with(getContext()).load(address).into(circleImageView);

        buttonList.add("所有用户");
        buttonList.add("用户设置");
        final UsersAdapter usersAdapter = new UsersAdapter(buttonList);
        usersAdapter.setOnItemClickLitener(new UsersAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(getActivity().getBaseContext(), AllUsersActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "用户设置", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
        rvUesrButton.setAdapter(usersAdapter);
    }
}
