package com.info.share.mini.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private String id;

    private String name;

    // vip 等级
    private int rank;

    private String tel;

    private String openid;

    private String unionid;

    private String nick_name;

    private String weChat;

    private int gender;

    private String country;

    private String province;

    private String city;

    private String company;

    private String position;

    private boolean showNumber;

//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date deleteTime;

    @Override
    public String toString() {
        return "{" +
                "id:'" + id + '\'' +
                ", name:'" + name + '\'' +
                ", rank:" + rank +
                ", tel:'" + tel + '\'' +
                ", openid:'" + openid + '\'' +
                ", unionid:'" + unionid + '\'' +
                ", nick_name:'" + nick_name + '\'' +
                ", weChat:'" + weChat + '\'' +
                ", gender:" + gender +
                ", country:'" + country + '\'' +
                ", province:'" + province + '\'' +
                ", city:'" + city + '\'' +
                ", company:'" + company + '\'' +
                ", position:'" + position + '\'' +
                ", showNumber:" + showNumber +
                ", createTime:" + dateTime2string(createTime) +
                ", deleteTime:" + dateTime2string(deleteTime) +
                '}';
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

    public boolean isShowNumber() {
        return showNumber;
    }

    public void setShowNumber(boolean showNumber) {
        this.showNumber = showNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getWeChat() {
        String tmpWeChat = "";
        if(this.isShowNumber()){ // show_number 为true 才显示微信号；
            tmpWeChat = weChat;
        }
        return tmpWeChat;
    }

    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }

    public String dateTime2string(Date dateTime){
        if (dateTime != null){
            return dateTime.toString();
        }
        return null;
    }
}
