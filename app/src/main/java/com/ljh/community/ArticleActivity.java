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

import com.ljh.community.model.Article;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ArticleActivity extends AppCompatActivity {
    private static final String TAG = ArticleActivity.class.getSimpleName();
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView articleContentTv;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        articleContentTv = findViewById(R.id.article_content_text);
        Intent intent = getIntent();
        String artilceId = intent.getStringExtra("articleId");
        Log.i(TAG, "onCreate: " + artilceId);
        final List<Article> articles = DataSupport.where("articleId=?", artilceId).find(Article.class);
        Article article = articles.get(0);
        collapsingToolbarLayout.setTitle(article.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getBaseContext(), R.color.ActicleActivity_Coll_black));
        articleContentTv.setText(article.getContent());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
}
