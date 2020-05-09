package com.info.share.mini.service.impl;

import com.info.share.mini.service.UserService;
import com.info.share.mini.entity.User;
import com.info.share.mini.mapper.UserMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.UUID;

@Service("userService")
@Component
public class UserServiceImple implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImple.class);

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Override
    public JSONObject fetchUser(Cookie[] cookies, String openId){
        JSONObject res = new JSONObject();
        int status = 200;
        String msg = "";
        logger.info("查询的openID " + openId);
        try {
            List<User> users = userMapper.getUserInfo(openId);
//            logger.info("")
            if (users!= null){
                res.put("data", users);
                msg = "user " + openId +" select successfully.";
            }else{
                status = 201;
//                res.put("message", "has none user which openid is " + openId);
            }
        }catch (Exception e){
            status = 500;
            msg = e.getLocalizedMessage();
        }
        res.put("status", status);
        res.put("message", msg);
        logger.info(msg);
        return res;
    }

    public JSONObject createUser(String openId, String name, String tel){
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");
        JSONObject res = new JSONObject();
        int status = 0;
        String msg = "";
        try {
            userMapper.createUser(id, openId, name, tel);
            msg = "user -- " + name + " , insert successfully.";
            logger.info(msg);
            status = 200;
        }catch (Exception e){
            msg = "user -- " + name + " , insert failed.";
            status = 500;
            logger.info(msg);
        }
        res.put("message", msg);
        res.put("status", status);
        return res;
    }

    public JSONObject wechatAuth(String userId){
        JSONObject res = new JSONObject();
        return res;
    }
}
