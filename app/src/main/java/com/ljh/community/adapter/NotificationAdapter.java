package com.ljh.community.adapter;

import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.community.R;
import com.ljh.community.model.Notification;

import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> mNotificationList;

    public NotificationAdapter(List<Notification> mNotificationList) {
        this.mNotificationList = mNotificationList;
    }

    private OnItemClickListener onItemClickListener = null;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_notification_item, null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (!mNotificationList.isEmpty()){
            Notification notification = mNotificationList.get(position);
            holder.notificationTv.setText(notification.getContent());
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
        return mNotificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTv;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            notificationTv = view.findViewById(R.id.tv_notificationContent);
        }
    }
}
