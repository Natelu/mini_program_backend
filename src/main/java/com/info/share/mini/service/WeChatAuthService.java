package com.info.share.mini.service;

import com.alibaba.fastjson.JSONObject;

public interface WeChatAuthService {

    // 判断用户是否注册，并更新session_key
    JSONObject checkUser(String code);

    // 前台返回openId, 加密数据，解密后进行注册操作
    JSONObject register(String openId, String encryptedData);
}
