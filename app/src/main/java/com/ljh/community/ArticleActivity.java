package com.ljh.community;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.community.adapter.CommentsAdapter;
import com.ljh.community.model.Article;
import com.ljh.community.model.Comment;
import com.ljh.community.util.HttpUtil;
import com.ljh.community.util.LogUtil;
import com.ljh.community.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ArticleActivity extends AppCompatActivity {
    private static final String TAG = ArticleActivity.class.getSimpleName();
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView articleContentTv;
    private CardView cardView;
    private RecyclerView rvComment;
    private List<Comment> mCommentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        articleContentTv = findViewById(R.id.article_content_text);
        cardView = findViewById(R.id.article_comment_card_view);
        rvComment = findViewById(R.id.article_activity_comment_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        rvComment.setLayoutManager(layoutManager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCommentsList = new ArrayList<>();
        initData();
    }

    private void initData() {
        //获取intent
        Intent intent = getIntent();
        final String artilceId = intent.getStringExtra("articleId");
        //数据库中根据articleId查找该文章
        queryArticle(artilceId);
        queryComments(artilceId);
    }

    /**
     * 查找文章
     * @param artilceId
     */
    private void queryArticle(final String artilceId) {
        final List<Article> articles = DataSupport.where("articleId=?", artilceId).find(Article.class);
        List<Comment> comments = DataSupport.where("articleId=?", artilceId).find(Comment.class);
        if (!articles.isEmpty()){
            if (comments.isEmpty()){
                cardView.setVisibility(View.GONE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setViewData(articles);
                }
            });
        }else{
            String address = "http://192.168.137.1/articleAndroid?articleId=" + artilceId;
            HttpUtil.sendOkHttpRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.i(TAG, "Fail");
                    //通过runOnUIThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    int result = -1; //默认为网络错误
                    result = Utility.handleArticlesResponse(responseText);
                    if (result == 1){
                        final List<Article> articles = DataSupport.where("articleId=?", artilceId).find(Article.class);
                        List<Comment> comments = DataSupport.where("articleId=?", artilceId).find(Comment.class);
                        if (articles.isEmpty()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArticleActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if (!articles.isEmpty() && comments.isEmpty()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cardView.setVisibility(View.GONE);
                                    setViewData(articles);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setViewData(articles);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void queryComments(String artilceId){
        mCommentsList = DataSupport.where("articleId=?", artilceId).order("publishTime desc").find(Comment.class);
        if (mCommentsList.isEmpty()){
            //到服务中查找
            String address ="http://192.168.137.1/commentsAndroid?articleId=" + artilceId;
            queryFromServer(address);
        }else{
            Log.i(TAG, "queryComments: " + mCommentsList.size());
            //设置数据适配器
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCommentsAdapter();
                }
            });
        }
    }

    private void setCommentsAdapter() {
        Log.i(TAG, "setCommentsAdapter: " + mCommentsList.size());
        CommentsAdapter adapter = new CommentsAdapter(mCommentsList);
        adapter.setOnItemClickListener(new CommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //头像点击事件，跳转userActivity
                Comment comment = mCommentsList.get(position);
                Integer userId = comment.getUserId();
                Intent intent = new Intent(ArticleActivity.this, UserActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        }, new CommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //内容点击事件，弹出回复框
            }
        });
        rvComment.setAdapter(adapter);
    }

    private void queryFromServer(String address) {
        Log.d(TAG, "queryFromServer: ");
        //发送访问请求，并获得回传信息
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG, "Fail");
                //通过runOnUIThread()方法回到主线程处理逻辑
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.i(TAG, "response");
                String responseText = response.body().string();
                int result = -1; //默认为网络错误
                //处理回传信息
                result = Utility.handleCommentsResponse(responseText);
                //如果回传成功，根据type，调用对应的处理方法
                if (result == 1) {
                        //设置数据适配器，可调用querySections，重复处理
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCommentsAdapter();
                        }
                    });
                }else if(result == 0){
                    //没有数据，则提示没有数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ArticleActivity.this, "暂无文章，请查看其他板块！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
            }
        });
    }

    /**
     * 设置标题及文章内容
     * @param articles
     */
    private void setViewData(List<Article> articles) {
        Article article = articles.get(0);
        //设置伸缩标题内容及文章内容
        collapsingToolbarLayout.setTitle(article.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getBaseContext(), R.color.ActicleActivity_Coll_black));
        articleContentTv.setText(article.getContent());
    }

    /**
     * 设置toolbar中的返回键
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
