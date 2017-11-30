package com.ljh.community.gson;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/28.
 */

public class Article {


    @SerializedName("id")
    private int articleId;
    private String title;
    private Date publishTime;
    private Date lastActiveTime;
    private Integer comments;
    private Integer status;
    private Integer userId;
    private Integer sectionId;
    private String content;
}
