package com.ljh.community.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ljh.community.model.Article;
import com.ljh.community.model.Notification;
import com.ljh.community.model.Section;
import com.ljh.community.model.User;


/**
 * Created by Administrator on 2017/11/27.
 * 处理和解析JSON数据
 */

public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    /**
     * 解析和处理服务器返回的section数据
     * 1为有数据， 0为无数据，-1为网络错误
     *
     * @param response
     * @return
     */
    public static int handleSectionsResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = (String) jsonObject.get("status");
                if ("success".equals(status)) {
                    String data = (String) jsonObject.get("data").toString();
                    LogUtil.i(TAG, data);
                    Gson gson = new Gson();
                    List<com.ljh.community.gson.Section> sectionObjects = gson.fromJson(data, new TypeToken<List<com.ljh.community.gson.Section>>() {
                    }.getType());
                    for (int i = 0; i < sectionObjects.size(); i++) {
                        //组装实体类对象
                        Section section = new Section();
                        section.setSectionId(sectionObjects.get(i).getId());
                        section.setName(sectionObjects.get(i).getName());
                        section.save();//将数据存储到数据库中
                    }
                    return 1; //有数据返回1
                } else if ("zero".equals(status)) {
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
     * 1为有数据， 0为无数据，-1为网络错误
     *
     * @param response
     * @return
     */
    public static int handleArticlesResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = (String) jsonObject.get("status");
                LogUtil.i(TAG, status);
                if (status.equals("success")) {
                    String data = (String) jsonObject.get("data").toString();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    ;
                    List<Article> articles = gson.fromJson(data, new TypeToken<List<Article>>() {
                    }.getType());
                    for (int i = 0; i < articles.size(); i++) {
                        //组装实体类对象
                        Article article = articles.get(i);
                        Log.d(TAG, "handleArticlesResponse: " + article.toString());
                        article.save();//将数据存储到数据库中
                    }
                    return 1;
                } else if (status.equals("zero")) {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 解析和处理服务器返回的登录用户数据
     * 1为有数据， 0为无数据，-1为网络错误
     *
     * @param response
     * @return
     */
    public static String handleLoginResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = (String) jsonObject.get("status");
                LogUtil.i(TAG, status);
                if (status.equals("success")) {
                    String data = (String) jsonObject.get("data").toString();
                    Gson gson = new Gson();
                    Log.i(TAG, "handleLoginResponse: " + data);
                    User user = gson.fromJson(data, User.class);
//                    for (int i = 0; i < users.size(); i++){
//                        //组装实体类对象
//                        User user = users.get(i);
                    Log.d(TAG, "handleArticlesResponse: " + user.toString());
                    boolean exist = DataSupport.isExist(User.class, "userId=" + user.getUserId());
                    if (!exist) {
                        user.save();//将数据存储到数据库中
                    }
//                    }
                    return "success";
                } else if (status.equals("error")) {
                    String data = (String) jsonObject.get("data").toString();
                    Log.i(TAG, "handleLoginResponse: " + data);
                    return data;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "error";
    }

    /**
     * 解析和处理服务器返回的所有用户数据
     * 1为有数据， 0为无数据，-1为网络错误
     *
     * @param response
     * @return
     */
    public static int handleAllUsersResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = (String) jsonObject.get("status");
                LogUtil.i(TAG, status);
                if (status.equals("success")) {
                    String data = (String) jsonObject.get("data").toString();
                    Gson gson = new Gson();
                    List<User> users = gson.fromJson(data, new TypeToken<List<User>>() {
                    }.getType());
                    for (int i = 0; i < users.size(); i++) {
                        //组装实体类对象
                        User user = users.get(i);
                        boolean exist = DataSupport.isExist(User.class, "userId=" + user.getUserId());
                        if (!exist) {
                            user.save();//将数据存储到数据库中
                        }
                    }
                    return 1;
                } else if (status.equals("zero")) {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }



    /**
     * 解析和处理服务器返回的所有用户通知数据
     * 1为有数据， 0为无数据，-1为网络错误
     *
     * @param response
     * @return
     */
    public static int handleNotificationsResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = (String) jsonObject.get("status");
                LogUtil.i(TAG, "handleNotifications:" + status);
                if (status.equals("success")) {
                    String data = jsonObject.get("data").toString();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    List<com.ljh.community.gson.Notification> notifications = gson.fromJson(data, new TypeToken<List<com.ljh.community.gson.Notification>>() {
                    }.getType());
                    for (int i = 0; i < notifications.size(); i++) {
                        //组装实体类对象
                        com.ljh.community.gson.Notification notification = notifications.get(i);
                        boolean exist = DataSupport.isExist(Notification.class, "notificationId=" + notification.getNotificationId());
                        if (!exist) {
                            String pattern = ">[\u4e00-\u9fa5_，_：_a-zA-Z0-9]+<";
                            String content = notification.getContent();
                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(content);
                            String string = "";
                            while(m.find()) {
                                String aString = m.group();
                                string += aString.substring(1, aString.length()-1);
                            }

                            String pattern1= "articleId=?(\\d+(\\d+)?)";
                            Pattern r1 = Pattern.compile(pattern1);
                            Matcher m1 = r1.matcher(content);
                            String articleId = "";
                            while(m1.find()) {
                                String aString = m1.group();
                                articleId = aString.substring(aString.lastIndexOf("=")+1);
                            }
                            Integer articleIdint = Integer.parseInt(articleId);
                            Notification notificationDb = new Notification();
                            notificationDb.setNotificationId(notification.getNotificationId());
                            notificationDb.setPublishTime(notification.getPublishTime());
                            notificationDb.setUserId(notification.getSenderId());
                            notificationDb.setReceiverId(notification.getReceiverId());
                            notificationDb.setArticleId(articleIdint);
                            notificationDb.setContent(string);
                            notificationDb.setIsReaded(notification.getIsReaded());
                            notificationDb.save();//将数据存储到数据库中
                        }
                    }
                    return 1;
                } else if (status.equals("zero")) {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
