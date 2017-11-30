package com.ljh.community.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/11/27.
 */

public class Section extends DataSupport {
    private int id;
    private int sectionId;
    private String name;

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

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
