package com.ljh.community.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/30.
 */

public class Notification extends DataSupport {

    @SerializedName("id")
    private int notificationId;
    private int userId; //评论者
    private int receiverId; //接收者
    private int articleId; //文章id
    private Date publishTime; //发送时间
    private String isReaded; //是否已读
    private String content; //评论内容

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(String isReaded) {
        this.isReaded = isReaded;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
