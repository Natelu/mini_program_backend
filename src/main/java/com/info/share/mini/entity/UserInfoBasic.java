package com.info.share.mini.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;

public class UserInfoBasic {

    private String id;

    @Id
    private String openId;


    private String name;


    private String country;


    private String province;


    private String city;


    private String company;

    //公司职位

    private String position;

    private String avatarUrl;

    @Override
    public String toString() {
        return "{" +
                "id:'" + id + '\'' +
                ", openId:'" + openId + '\'' +
                ", name:'" + name + '\'' +
                ", country:'" + country + '\'' +
                ", province:'" + province + '\'' +
                ", city:'" + city + '\'' +
                ", company:'" + company + '\'' +
                ", position:'" + position + '\'' +
                ", avatarUrl:'" + avatarUrl + '\'' +
                '}';
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
