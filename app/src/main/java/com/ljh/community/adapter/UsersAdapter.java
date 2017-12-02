package com.ljh.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.community.R;
import com.ljh.community.model.Article;

import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{
    private List<String> mStringList;
    private OnItemClickLitener onItemClickListener = null;

    public UsersAdapter(List<String> mStringList) {
        this.mStringList = mStringList;
    }

    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener){
        this.onItemClickListener = onItemClickLitener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_user_item,null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mStringList.size() != 0){
            String string = mStringList.get(position);
            holder.buttonTv.setText(string);
        }
        if (onItemClickListener != null){
            holder.buttonTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.buttonTv, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mStringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView buttonTv;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            buttonTv = itemView.findViewById(R.id.tv_user);
        }
    }
}
