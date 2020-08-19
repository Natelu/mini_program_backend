package com.info.share.mini.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Date;

public class Task {
    private String id;

    private String name;

    private String serviceWechat;

    // 客服二维码
    private String serviceWechatCode;

    private String introduce;

    // 任务佣金
    private float money;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    private String taskName;

    public String getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
    }

    private String taskOwner;

    @Override public String toString() {
        return JSONObject.toJSONString(this, SerializerFeature.WriteMapNullValue);
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

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

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }
}
