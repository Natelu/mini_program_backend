package com.info.share.mini.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Date;

public class ElasticNetworkResult {

    // 人脉id
    private String id;

    private String openid;

    private String company;

    private String position;

    private String city;

    private String province;

    private String Abstract;

    private String updateTime;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this, SerializerFeature.WRITE_MAP_NULL_FEATURES);
//        return "{" +
//                "id:'" + id + '\'' +
//                ", openid:'" + openid + '\'' +
//                ", company:'" + company + '\'' +
//                ", position:'" + position + '\'' +
//                ", city:'" + city + '\'' +
//                ", province:'" + province + '\'' +
//                ", Abstract:'" + Abstract + '\'' +
//                ", updateTime:'" + updateTime + '\'' +
//                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAbstract() {
        return Abstract;
    }

    public void setAbstract(String anAbstract) {
        Abstract = anAbstract;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
