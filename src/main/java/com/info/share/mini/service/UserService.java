package com.info.share.mini.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.Cookie;

public interface UserService {
    // fetchUsers : get the Users by userId which was judged by userId;
    JSONObject fetchUser(Cookie[] cookies, String openId);

    JSONObject createUser(String openId, String name, String tel);

    JSONObject wechatAuth(String userId);
}
