package com.ljh.community;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ljh.community.adapter.ArticlesAdapter;
import com.ljh.community.model.Article;
import com.ljh.community.model.User;
import com.ljh.community.util.HttpUtil;
import com.ljh.community.util.LogUtil;
import com.ljh.community.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getSimpleName();
    CircleImageView circleImageView;
    TextView emailTv;
    TextView nameTv;
    RecyclerView rvUserArticles;
    List<Article> mArticlesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        circleImageView = findViewById(R.id.userActivity_image);
        emailTv = findViewById(R.id.user_activity_tv_email);
        nameTv = findViewById(R.id.userActivity_tv_name);
        rvUserArticles = findViewById(R.id.rv_user_article);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvUserArticles.setLayoutManager(layoutManager);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        final Integer userId = intent.getIntExtra("userId", 0);
        List<User> users = DataSupport.where("userId=?", userId.toString()).find(User.class);
        if (!users.isEmpty()){
            User user = users.get(0);
            Glide.with(getApplicationContext()).load("http://192.168.137.1/"+ user.getAvatar()).into(circleImageView);
            emailTv.setText(user.getEmail());
            nameTv.setText(user.getName());
        }
        mArticlesList = DataSupport.where("userId=?",userId.toString()).find(Article.class);
        if (mArticlesList.isEmpty()){
            //到服务中请求数据
            Log.i(TAG, "onCreate: " + userId);
            String address = "http://192.168.137.1/articlesAndroid?userId=" + userId;
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
                    Log.i(TAG, "onResponse: " + responseText);
                    int result = -1; //默认为网络错误
                    result = Utility.handleArticlesResponse(responseText);
                    if (result == 1){
                        mArticlesList = DataSupport.where("userId=?",userId.toString()).find(Article.class);
                        final ArticlesAdapter adapter = new ArticlesAdapter(mArticlesList);
                        adapter.setOnItemClickLitener(new ArticlesAdapter.OnItemClickLitener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Article article = mArticlesList.get(position);
                                Integer articleId = article.getArticleId();
                                Intent intent = new Intent(UserActivity.this, ArticleActivity.class);
                                intent.putExtra("articleId", articleId.toString());
                                startActivity(intent);
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvUserArticles.setAdapter(adapter);
                            }
                        });

                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserActivity.this, "暂无文章数据", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }else{
            final ArticlesAdapter adapter = new ArticlesAdapter(mArticlesList);
            adapter.setOnItemClickLitener(new ArticlesAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    Article article = mArticlesList.get(position);
                    Integer articleId = article.getArticleId();
                    Intent intent = new Intent(UserActivity.this, ArticleActivity.class);
                    intent.putExtra("articleId", articleId.toString());
                    startActivity(intent);
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rvUserArticles.setAdapter(adapter);
                }
            });
        }
    }
}
