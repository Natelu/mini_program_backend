package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.WeChatAuthInfo;
import com.info.share.mini.mapper.WeChatMapper;
import com.info.share.mini.service.UserService;
import com.info.share.mini.service.WeChatAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.info.share.mini.utils.wechatAuthUtil;

import javax.annotation.Resource;

@Service("weChatAuthService")
@Component
public class WeChatAuthServiceImpl implements WeChatAuthService {
    private static final Logger logger = LogManager.getLogger(WeChatAuthServiceImpl.class);
    @Resource(name = "weChatAuthMapper")
    private WeChatMapper weChatMapper;

    @Resource(name = "userService")
    private UserService userService;

    @Override
    public JSONObject checkUser(String code){
        JSONObject sessionRes = wechatAuthUtil.code2Session(code);
        ResultJSON result;
        JSONObject res = new JSONObject();
        if (res.getIntValue("errcode") != 0){
            ResultJSON tmp = ResultJSON.error(res.getString("errmsg"));
            return JSONObject.parseObject(tmp.toSimpleString());
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
            result = ResultJSON.success(res);
        }catch (Exception e){
            result = ResultJSON.error(e.getLocalizedMessage());
            logger.info("WeChat checkUser " + code + "failed, " + e.getLocalizedMessage());
        }
        return JSONObject.parseObject(result.toSimpleDataString());
    }

    @Override
    public JSONObject register(String openId, String encryptedData) {
        return null;
    }
}
