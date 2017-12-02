package com.ljh.community.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.community.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */

public class NotificationTypeAdapter extends RecyclerView.Adapter<NotificationTypeAdapter.ViewHolder> {
    private List<String> mTypeList;
    private OnItemClickListener onItemClickListener = null;
    private List<Boolean> isClicks;//控件是否被点击,默认为false，如果被点击，改变值，控件根据值改变自身颜色

    public List<Boolean> getIsClicks() {
        return isClicks;
    }

    public void setIsClicks(List<Boolean> isClicks) {
        this.isClicks = isClicks;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public NotificationTypeAdapter(List<String> mTypeList) {
        this.mTypeList = mTypeList;
        isClicks = new ArrayList<>();
        //置所有元素为false，未点击
        for (int i = 0; i < mTypeList.size(); i++) {
            isClicks.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_notification_type_item,null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (!mTypeList.isEmpty()){
            holder.notificationTv.setText(mTypeList.get(position));
            if (isClicks.get(position)) {
                holder.notificationTv.setTextColor(ContextCompat.getColor(holder.notificationTv.getContext(), R.color.ActicleFragmentAdapter_tv_red));
            } else {
                holder.notificationTv.setTextColor(ContextCompat.getColor(holder.notificationTv.getContext(), R.color.ActicleFragmentAdapter_tv_black));
            }
        }
        if (onItemClickListener != null){
            holder.notificationTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.notificationTv, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTypeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView notificationTv;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            notificationTv = view.findViewById(R.id.tv_notificationType);
        }
    }
}
