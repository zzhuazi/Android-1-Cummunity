package com.ljh.community.fragment;

import android.view.View;

import com.ljh.community.R;

import com.ljh.community.BaseFragment.BaseFragment;

public class ChatFragment extends BaseFragment {

    @Override
    protected View initView() {
        View view = View.inflate(mContext,R.layout.fragment_chat,null);
        return view;
    }
}
