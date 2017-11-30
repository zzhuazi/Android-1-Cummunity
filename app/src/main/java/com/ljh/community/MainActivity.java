package com.ljh.community;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import com.ljh.community.BaseFragment.BaseFragment;
import com.ljh.community.fragment.ArticleFragment;
import com.ljh.community.fragment.ChatFragment;
import com.ljh.community.fragment.NotificationFragment;
import com.ljh.community.fragment.UserFragment;


public class MainActivity extends AppCompatActivity {

    private RadioGroup mRg_main;
    private List<BaseFragment> mBaseFragment;
    //选中的fragment的对应位置
    private int position;
    //上次切换的Fragment
    private Fragment mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化view
        initView();
        //初始化Fragment
        initFragment();
        //设置radioGroup的监听
        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        mRg_main.setOnCheckedChangeListener(new MyOnCheckedListener());
        //设置默认选择acticleFragment
        mRg_main.check(R.id.rb_article_frame);
    }

    private void initFragment() {
        mBaseFragment = new ArrayList<>();
        mBaseFragment.add(new ArticleFragment());//文章Fragment
        mBaseFragment.add(new NotificationFragment()); //通知Fragment
        mBaseFragment.add(new ChatFragment()); //私信Fragment
        mBaseFragment.add(new UserFragment()); //用户Fragment
    }

    /**
     * 初始化view
     */
    private void initView() {
        mRg_main = findViewById(R.id.rg_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    /**
     * 根据位置得到对应的Fragment
     * @return
     */
    public BaseFragment getFragment() {
        BaseFragment fragment = mBaseFragment.get(position);
        return fragment;
    }

    /**
     * 选择监听
     */
    private class MyOnCheckedListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i) {
                case R.id.rb_article_frame://article
                    position = 0;
                    break;
                case R.id.rb_notification://notification
                    position = 1;
                    break;
                case R.id.rb_chat://chat
                    position = 2;
                    break;
                case R.id.rb_user://user
                    position = 3;
                    break;
                default:
                    position = 0;
                    break;
            }

            //根据当前位置获得对应的Fragment
            BaseFragment to = getFragment();

            //替换
            switchFragment(mContext, to);
        }
    }

    /**
     * 替换Fragment
     * @param from 刚才显示的Fragment,马上就要被隐藏
     * @param to 马上要切换到的Fragment
     */
    private void switchFragment(Fragment from, BaseFragment to) {
        if (from != to){
            mContext = to;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //判断有没有被添加
            if (!to.isAdded()){
                //没有添加
                //from隐藏
                if (from != null){
                    fragmentTransaction.hide(from);
                }
                //添加to
                if (to != null){
                    fragmentTransaction.add(R.id.fl_content, to).commit();
                }
            }else{
                //to已被添加
                //from隐藏
                if (from != null){
                    fragmentTransaction.hide(from);
                }
                //显示to
                if (to != null){
                    fragmentTransaction.show(to).commit();
                }
            }
        }
    }
}