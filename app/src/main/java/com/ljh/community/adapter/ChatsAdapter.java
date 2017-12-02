package com.ljh.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljh.community.R;
import com.ljh.community.model.Chat;
import com.ljh.community.model.User;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2017/12/2.
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private List<Chat> chatList;
    private OnItemClickListener onItemClickListener = null;

    public ChatsAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_chat_item, null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (chatList.size() != 0){
            Chat chat = chatList.get(position);
            Integer userId = chat.getReceiverId();
            List<User> users = DataSupport.where("userId=?", userId.toString()).find(User.class);
            User user = users.get(0);
            holder.nameTv.setText(user.getName());
            holder.contentTv.setText(chat.getLastActiveContent());
        }
        if (onItemClickListener != null){
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.view, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        TextView contentTv;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            nameTv = view.findViewById(R.id.tv_chat_name);
            contentTv = view.findViewById(R.id.tv_chat_content);
        }
    }
}
