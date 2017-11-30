package com.ljh.community.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;

import com.ljh.community.model.Article;
import com.ljh.community.model.Section;


/**
 * Created by Administrator on 2017/11/27.
 * 处理和解析JSON数据
 */

public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    /**
     * 解析和处理服务器返回的section数据
     * 1为有数据， 0为无数据，-1为网络错误
     * @param response
     * @return
     */
    public static int handleSectionsResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONObject jsonObject = new JSONObject(response);
                String status = (String) jsonObject.get("status");
                if ("success".equals(status)){
                    String data = (String) jsonObject.get("data").toString();
                    LogUtil.i(TAG, data);
                    Gson gson = new Gson();
                    List<com.ljh.community.gson.Section> sectionObjects = gson.fromJson(data, new TypeToken<List<com.ljh.community.gson.Section>>(){}.getType());
                    for (int i = 0; i < sectionObjects.size(); i++){
                        //组装实体类对象
                        Section section = new Section();
                        section.setSectionId(sectionObjects.get(i).getId());
                        section.setName(sectionObjects.get(i).getName());
                        section.save();//将数据存储到数据库中
                    }
                    return 1; //有数据返回1
                }else if ("zero".equals(status)){
                    return 0;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }



    /**
     * 解析和处理服务器返回的articles数据
     * @param response
     * @return
     */
    public static int handleArticlesResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONObject jsonObject = new JSONObject(response);
                String status = (String) jsonObject.get("status");
                LogUtil.i(TAG, status);
                if (status.equals("success")){
                    String data = (String) jsonObject.get("data").toString();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();   ;
                    List<Article> articles = gson.fromJson(data, new TypeToken<List<Article>>(){}.getType());
                    for (int i = 0; i < articles.size(); i++){
                        //组装实体类对象
                        Article article = articles.get(i);
                        Log.d(TAG, "handleArticlesResponse: "+ article.toString());
                        article.save();//将数据存储到数据库中
                    }
                    return 1;
                }else if(status.equals("zero")){
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
