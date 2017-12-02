package com.ljh.community.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by Administrator on 2017/12/2.
 */

public class Chat extends DataSupport {
    @SerializedName("id")
    private Integer chatId;
    private Integer messages;
    private String lastActiveContent;
    private Date lastActiveTime;
    private Integer unread;
    private Integer userId;
    private Integer receiverId;
    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getMessages() {
        return messages;
    }

    public void setMessages(Integer messages) {
        this.messages = messages;
    }

    public String getLastActiveContent() {
        return lastActiveContent;
    }

    public void setLastActiveContent(String lastActiveContent) {
        this.lastActiveContent = lastActiveContent;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public Integer getUnread() {
        return unread;
    }

    public void setUnread(Integer unread) {
        this.unread = unread;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }
}
