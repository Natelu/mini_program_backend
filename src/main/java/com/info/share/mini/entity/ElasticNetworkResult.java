package com.info.share.mini.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class ElasticNetworkResult {

    // 人脉id
    private String id;

    private String openId;

    private User user;

    private int weight;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date update_time;


}
