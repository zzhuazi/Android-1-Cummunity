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
 * Created by Administrator on 2017/11/28.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private List<Article> mArticleList;
    private OnItemClickLitener onItemClickLitener = null;

    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener){
        this.onItemClickLitener = onItemClickLitener;
    }

    public ArticlesAdapter(List<Article> mArticleList) {
        this.mArticleList = mArticleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_article_item,null);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mArticleList.size() != 0){
            Article article = mArticleList.get(position);
            holder.articleTitle.setText(article.getTitle());
        }
        if (onItemClickLitener != null){
            holder.articleTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemClickLitener.onItemClick(holder.articleTitle, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView articleTitle;
        View articleView;

        public ViewHolder(View itemView) {
            super(itemView);
            articleView = itemView;
            articleTitle = itemView.findViewById(R.id.tv_articleTitle);
        }
    }
}
