package com.ljh.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.community.R;
import com.ljh.community.model.User;

import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.ViewHolder> {
    private static final String TAG = AllUsersAdapter.class.getSimpleName();
    private List<User> mUserList;
    private OnItemClickListener onItemClickListener = null;

    public AllUsersAdapter(List<User> mUserList) {
        this.mUserList = mUserList;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_allusers_item, null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mUserList.size() != 0){
            User user = mUserList.get(position);
            holder.userNameTv.setText(user.getEmail());
        }
        if (onItemClickListener != null){
            holder.userNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.userNameTv, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userNameTv;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userNameTv = itemView.findViewById(R.id.tv_userName);
        }
    }
}
