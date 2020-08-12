package com.info.share.mini.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Product {
    private String id;
    private String name;
    private String introduce;
    private float fee;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }
}
