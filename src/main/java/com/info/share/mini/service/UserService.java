package com.info.share.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.Cookie;

public interface UserService {
    // fetchUsers : get the Users by userId which was judged by userId;
    JSONObject fetchUser(Cookie[] cookies, String openId);

    JSONObject deleteUser(String openId);

    JSONObject createUser(String openId, String name, String tel);

    JSONObject getBasicUserInfo(String openId);

    JSONObject wechatAuth(String userId);

    Boolean checkVip(String openId);

    JSONObject register(String openId);

    boolean checkExists(String openId);

    JSONObject checkExistsJson(String openId);

    JSONObject checkVipJson(String openId);
    // 升级用户为vip
    JSONObject update2Vip(String openId);

    JSONObject invitedVipUsersByOpenId(String openId);

}
