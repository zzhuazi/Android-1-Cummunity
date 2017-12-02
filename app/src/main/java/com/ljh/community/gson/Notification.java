package com.ljh.community.gson;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Administrator on 2017/12/1.
 */

public class Notification {
    @SerializedName("id")
    private Integer notificationId;
    private String content;
    private String url;
    private Date publishTime;
    private String isReaded;
    private Integer senderId;
    private Integer receiverId;

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }
}
