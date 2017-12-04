package com.ljh.community;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.community.model.Article;
import com.ljh.community.util.HttpUtil;
import com.ljh.community.util.LogUtil;
import com.ljh.community.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ArticleActivity extends AppCompatActivity {
    private static final String TAG = ArticleActivity.class.getSimpleName();
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView articleContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        articleContentTv = findViewById(R.id.article_content_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initData();
    }

    private void initData() {
        //获取intent
        Intent intent = getIntent();
        final String artilceId = intent.getStringExtra("articleId");
        //数据库中根据articleId查找该文章
        final List<Article> articles = DataSupport.where("articleId=?", artilceId).find(Article.class);
        if (!articles.isEmpty()){
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
                    result = Utility.handleArticlesResponse(responseText,"single");
                    if (result == 1){
                        final List<Article> articles = DataSupport.where("articleId=?", artilceId).find(Article.class);
                        if (articles.isEmpty()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArticleActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
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

    private void setViewData(List<Article> articles) {
        Article article = articles.get(0);
        //设置伸缩标题内容及文章内容
        collapsingToolbarLayout.setTitle(article.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getBaseContext(), R.color.ActicleActivity_Coll_black));
        articleContentTv.setText(article.getContent());
    }

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
