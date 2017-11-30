package com.ljh.community.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/27.
 */

public class Section {
    @SerializedName("id")
    public int id;
    public String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
