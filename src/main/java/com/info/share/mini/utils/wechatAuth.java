package com.info.share.mini.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


public class wechatAuth {

    @Value("${wechat.appId}")
    private static String appId;
    private void setAppId(String tempAppId){
        appId = tempAppId;
    }

    @Value("${wechat.appSecret}")
    private static String appSercret;
    private void setSrcret(String sct){
        appSercret = sct;
    }

    @Value("${wechat.code2SessionUrl}")
    private static String sessionKeyUrl;
    private void setSessionKeyUrl(String url){
        sessionKeyUrl = url;
    }

    @Value("${wechat.accessTokenUrl}")
    private static String accessTokenUrl;
    private void setAccessTokenUrl(String url){
        accessTokenUrl = url;
    }

    // 通过前台的code获取用户的openid、sessionKey;
    public JSONObject code2Session(String code){
        JSONObject res = null;
        String tempUrl = sessionKeyUrl;
        tempUrl = tempUrl.replace("APPID", appId);
        tempUrl = tempUrl.replace("SECRET", appSercret);
        tempUrl = tempUrl.replace("JSCODE", code);
        ResponseEntity response;
        response = HttpUtil.simpleGet("https", tempUrl, HttpMethod.GET, JSONObject.class);
        if (response.getStatusCodeValue() < 300){
            res = JSON.parseObject(JSON.toJSONString(response.getBody()));
        }
        return res;
    }

    // 获取accesstoken
    public JSONObject getAccessToken(){
        JSONObject res = null;
        String tempUrl = accessTokenUrl;
        tempUrl = tempUrl.replace("APPID", appId);
        tempUrl = tempUrl.replace("APPSECRET", appSercret);
        ResponseEntity responseEntity;
        responseEntity = HttpUtil.simpleGet("https", tempUrl, HttpMethod.GET, JSONObject.class);
        if (responseEntity.getStatusCodeValue() < 300){
            res = JSONObject.parseObject(JSON.toJSONString(responseEntity.getBody()));
        }
        return res;
    }

    //TODO:
    //通过session_key校验用户数据合法性
    public boolean checkData(String data, String key){
        boolean res = true;
        return res;
    }
}
