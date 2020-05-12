package com.info.share.mini.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Network {

    private String id;

    private String openId;

    private String Abstract;

    private String demand;

    private String resource;

    private int weight;

    private int readCount;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;

    @Override
    public String toString() {
        return "{" +
                "id:'" + id + '\'' +
                ", openId:'" + openId + '\'' +
                ", _abstract:'" + Abstract + '\'' +
                ", demand:'" + demand + '\'' +
                ", resource:'" + resource + '\'' +
                ", weight:" + weight +
                ", readCount:" + readCount +
                ", update_time:" + update_time +
                '}';
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String get_abstract() {
        return Abstract;
    }

    public void set_abstract(String _abstract) {
        this.Abstract = _abstract;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
