package com.ljh.community.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ljh.community.ArticleActivity;
import com.ljh.community.BaseFragment.BaseFragment;
import com.ljh.community.R;
import com.ljh.community.adapter.ArticlesAdapter;
import com.ljh.community.adapter.SectionsAdapter;
import com.ljh.community.model.Article;
import com.ljh.community.model.Section;
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

public class ArticleFragment extends BaseFragment {
    private static final String TAG = ArticleFragment.class.getSimpleName();

    //板块section数组
    private List<Section> sectionList;
    //article数据
    private List<Article> articleList;
    //sections板块recyclerView
    private RecyclerView rvSections;
    //artilces recyclerView
    private RecyclerView rvArticles;
    //设置sectionId
    private static Integer sectionId;

    @Override
    protected View initView() {
        LogUtil.i(TAG, "InitView");
        //初始化view
        View view = View.inflate(mContext, R.layout.fragment_article, null);
        rvSections = view.findViewById(R.id.rv_sections_article);
        rvArticles = view.findViewById(R.id.rv_articles_article);
        sectionList = new ArrayList<>();
        articleList = new ArrayList<>();

        //给section recyclerView 设置LayoutManager
        LinearLayoutManager section_LayoutManager = new LinearLayoutManager(getContext());
        section_LayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvSections.setLayoutManager(section_LayoutManager);
        //给artilce recyclerView 设置LayoutManager
        LinearLayoutManager article_LayoutManager = new LinearLayoutManager(getContext());
        rvArticles.setLayoutManager(article_LayoutManager);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        Log.d(TAG, "initData: ");
        //查找sections数据
        querySections();

    }


    /**
     * 查询所有板块信息，优先从数据库中查询，如果没有查询则再去服务器上查询
     */
    private void querySections() {
        //在数据库中查询出数据
        Log.d(TAG, "querySections: ");
        sectionList = DataSupport.findAll(Section.class);

        Log.d(TAG, "querySections: sectionList" + (sectionList == null));
        if (sectionList.size() == 0) {
            //数据库中无数据，到网络中查找
            String address = "http://192.168.137.1/sectionsJSON";
            queryFromServer(address, "sections");
        } else {
            //有数据，设置数据适配器
            setSectionAdapter();
            //设置列表中的sectionid为第一个
            sectionId = sectionList.get(0).getSectionId();
            Log.d(TAG, "querySections: sectionId:" + sectionId);
            //根据sectionId获取对应的articles数据
            queryArticles();
        }
    }

    /**
     * 查询某一板块下的文章信息，优先从数据库中查询，如果没有查询则再去服务器上查询
     */
    private void queryArticles() {
        Log.i(TAG, "queryArticles: sectionid:" + sectionId);
        //在数据库中查询出数据
        if (sectionList != null) {
            articleList = DataSupport.where("sectionId=?", sectionId.toString()).find(Article.class);
        }
        if (articleList.size() == 0) {
            //数据库中无数据
            //获取section_id,并网络请求该section中对应的文章
            String address = "http://192.168.137.1/sectionJSON?sectionId=" + sectionId;
            queryFromServer(address, "articles");
        } else {
            //有数据，设置数据适配器
            final ArticlesAdapter adapter = new ArticlesAdapter(articleList);
            adapter.setOnItemClickLitener(new ArticlesAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    Article article = articleList.get(position);
                    Integer articleId = article.getArticleId();
                    Intent intent = new Intent();
                    intent.putExtra("articleId", articleId.toString());
                    intent.setClass(getContext(), ArticleActivity.class);
                    Log.i(TAG, "onItemClick: "+ intent.getStringExtra("articleId"));
                    startActivity(intent);
                }
            });
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rvArticles.setAdapter(adapter);
                }
            });
        }

    }

    /**
     * 从网络中请求数据，并调用对应方法进行处理
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        Log.d(TAG, "queryFromServer: ");
        //发送访问请求，并获得回传信息
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG, "Fail");
                //通过runOnUIThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.i(TAG, "response");
                String responseText = response.body().string();
                int result = -1; //默认为网络错误
                //处理回传信息
                if ("sections".equals(type)) {
                    //将数据存储到数据库中
                    result = Utility.handleSectionsResponse(responseText);
                }
                if ("articles".equals(type)) {
                    result = Utility.handleArticlesResponse(responseText);
                }
                //如果回传成功，根据type，调用对应的处理方法
                if (result == 1) {
                    if ("sections".equals(type)) {
                        //设置数据适配器，可调用querySections，重复处理
                        querySections();
                    }
                    if ("articles".equals(type)) {
                        queryArticles();
                    }
                }else if(result == 0){
                    //没有数据，则提示没有数据
                    if ("articles".equals(type)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "暂无文章，请查看其他板块！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 设置section recyclerView adapter
     */
    public void setSectionAdapter(){
        final SectionsAdapter adapter = new SectionsAdapter(sectionList);
        adapter.setOnItemClickLitener(new SectionsAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Section section = sectionList.get(position);
                List<Boolean> isClicks = adapter.getIsClicks();
                for (int i = 0; i < isClicks.size(); i++) {
                    isClicks.set(i, false);
                }
                isClicks.set(position, true);
                adapter.notifyDataSetChanged();
                sectionId = section.getSectionId();
                //设置文章列表为空
                rvArticles.setAdapter(null);
                queryArticles();
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvSections.setAdapter(adapter);
            }
        });
    }
}
