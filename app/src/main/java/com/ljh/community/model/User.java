package com.ljh.community.model;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/11/30.
 */

public class User extends DataSupport {
    @SerializedName("id")
    private int userId;
    private String name;
    private String avatar;
    private String email;
    private String password;
    private String sex;
    private String introduce;
    private String phone;
    private Integer followers;
    private Integer articles;
    private Integer up;
    private Integer roleId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getArticles() {
        return articles;
    }

    public void setArticles(Integer articles) {
        this.articles = articles;
    }

    public Integer getUp() {
        return up;
    }

    public void setUp(Integer up) {
        this.up = up;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }


}
