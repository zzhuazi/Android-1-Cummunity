package com.ljh.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dd.CircularProgressButton;
import com.ljh.community.AllUsersActivity;
import com.ljh.community.R;
import com.ljh.community.model.Comment;
import com.ljh.community.model.User;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by Administrator on 2017/12/5.
 */

public class CommentsAdapter extends android.support.v7.widget.RecyclerView.Adapter<CommentsAdapter.ViewHolder>{
    private static final String TAG = CommentsAdapter.class.getSimpleName();
    private List<Comment> mCommentList;
    private OnItemClickListener onItemImageClickListener = null;
    private OnItemClickListener onItemTextClickListener = null;

    public CommentsAdapter(List<Comment> mCommentList) {
        this.mCommentList = mCommentList;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemImageClickListener, OnItemClickListener onItemTextClickListener){
        this.onItemImageClickListener = onItemImageClickListener;
        this.onItemTextClickListener = onItemTextClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_comment_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (!mCommentList.isEmpty()){
            Log.i(TAG, "onCreateViewHolder: mCommentsList.size" + mCommentList.size());
            Log.i(TAG, "onCreateViewHolder: position:" + position);
            Comment comment = mCommentList.get(position);
            Integer userId = comment.getUserId();
            List<User> users = DataSupport.where("userId=?", userId.toString()).find(User.class);
            if (!users.isEmpty()){
                Glide.with(holder.view.getContext()).load("http://192.168.137.1/"+ users.get(0).getAvatar()).into(holder.userImage);
            }
            holder.commentContent.setText(comment.getContent());
        }
        if (onItemImageClickListener != null){
            holder.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemImageClickListener.onItemClick(holder.userImage, pos);
                }
            });
        }
        if (onItemTextClickListener != null){
            holder.commentContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemTextClickListener.onItemClick(holder.commentContent, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView commentContent;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userImage = view.findViewById(R.id.article_activity_imageView);
            commentContent = view.findViewById(R.id.article_comment_text);
        }
    }
}
