package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.WeChatAuthInfo;
import com.info.share.mini.mapper.UserMapper;
import com.info.share.mini.mapper.WeChatMapper;
import com.info.share.mini.service.UserService;
import com.info.share.mini.service.WeChatAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.info.share.mini.utils.wechatAuthUtil;

import javax.annotation.Resource;
import java.util.UUID;

@Service("weChatAuthService")
@Component
public class WeChatAuthServiceImpl implements WeChatAuthService {
    private static final Logger logger = LogManager.getLogger(WeChatAuthServiceImpl.class);

//    @Resource(name = )
    @Resource(name = "weChatAuthMapper")
    private WeChatMapper weChatMapper;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Override
    public JSONObject checkUser(String code){
        logger.info("request code is " + code);
        JSONObject sessionRes = wechatAuthUtil.code2Session(code);
        logger.info("sessionRes " + sessionRes);
        ResultJSON result;
        JSONObject res = new JSONObject();
        if (sessionRes.getIntValue("errcode") != 0){
            sessionRes.put("code", 400);
//            logger.error();
            ResultJSON tmp = ResultJSON.error(res.getString("errmsg"));
            return sessionRes;
        }
        try{
            String openId = sessionRes.getString("openid");
            String sessionKey = sessionRes.getString("session_key");
            String unionId = sessionRes.getString("unionid");
            WeChatAuthInfo authInfo = weChatMapper.getAuthInfo(openId);
            if (authInfo != null){ // 已有openid 记录，更新session_key
                weChatMapper.updateAuthInfo(openId, sessionKey);
            }else{
                weChatMapper.insertAuthInfo(openId, sessionKey, unionId);
            }
            // 判断该用户是否注册本系统
            boolean registed = false;
            if (userService.checkExists(openId)){ //该用户已注册
                registed = true;
            }
            res.put("registed", registed);
            res.put("openid", openId);
            res.put("code", 200);
            result = ResultJSON.success(res);
            logger.info(result);
        }catch (Exception e){
            result = ResultJSON.error(e.getLocalizedMessage());
            logger.info("WeChat checkUser " + code + "failed, " + e.getLocalizedMessage());
        }
        return JSONObject.parseObject(result.toSimpleDataString());
    }

    @Override
    public JSONObject register(String openId, String encryptedData, String iv, String invitedBy){
        ResultJSON res;
        try{
            WeChatAuthInfo weChatAuthInfo = weChatMapper.getAuthInfo(openId);
            String sessionKey = weChatAuthInfo.getSessionKey();
            JSONObject userInfo = wechatAuthUtil.decryptedWechat(encryptedData, iv, sessionKey);
            logger.info(userInfo);
            if (!userInfo.getBoolean("verifyStatus")){
                res = ResultJSON.error("the request data is not safe , maybe it has been already modified.");
                return JSONObject.parseObject(res.toSimpleDataString());
            }
            //更新用户信息
            logger.info(userInfo);
            String unionId = "";
            String nickName = userInfo.getString("nickName");
            int gender = userInfo.getIntValue("gender");
            String city = userInfo.getString("city");
            String province = userInfo.getString("province");
            String country = userInfo.getString("country");
            String avatarUrl = userInfo.getString("avatarUrl");
            if (!userService.checkExists(openId)) {
                String id = UUID.randomUUID().toString();
                id = id.replace("-", "");
                userMapper.createUserByWeChatInfo(id, openId, nickName, gender, city, province, country, avatarUrl,
                        unionId, invitedBy);
                res = ResultJSON.success(200, "registed successfully.");
            }else{
                res = ResultJSON.success(403, "this user has already been registed.");
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }


    @Override
    public JSONObject updatePhone(String openId, String encryptedData, String iv){
        ResultJSON res;
        try{
            WeChatAuthInfo weChatAuthInfo = weChatMapper.getAuthInfo(openId);
            String sessionKey = weChatAuthInfo.getSessionKey();
            JSONObject phoneInfo = wechatAuthUtil.decryptedWechat(encryptedData, iv, sessionKey);
            if (!phoneInfo.getBoolean("verifyStatus")){
                res = ResultJSON.error("the request data is not safe , maybe it has been already modified.");
                return JSONObject.parseObject(res.toSimpleDataString());
            }
            // 更新用户手机号
            userMapper.updatePhone(openId, phoneInfo.getString("phoneNumber"));
            res = ResultJSON.success(200, "update phone number successfully.");
            logger.info(phoneInfo);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleDataString());
    }
}
