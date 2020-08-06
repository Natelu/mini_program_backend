package com.info.share.mini.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Date;

public class ElasticTaskResult {
    private String id;

    private String name;

    private String serviceWechat;

    // 客服二维码
    private String serviceWechatCode;

    private String introduce;

    private String index;

    // 任务佣金
    private float money;

    private String publishTime;

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

    public String getServiceWechat() {
        return serviceWechat;
    }

    public void setServiceWechat(String serviceWechat) {
        this.serviceWechat = serviceWechat;
    }

    public String getServiceWechatCode() {
        return serviceWechatCode;
    }

    public void setServiceWechatCode(String serviceWechatCode) {
        this.serviceWechatCode = serviceWechatCode;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    @Override public String toString() {
        return JSONObject.toJSONString(this, SerializerFeature.WriteMapNullValue);
    }
}
