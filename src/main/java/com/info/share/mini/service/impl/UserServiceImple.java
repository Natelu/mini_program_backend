package com.info.share.mini.service.impl;

import com.info.share.mini.entity.Network;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.service.NetworkService;
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

    @Resource(name = "networkService")
    private NetworkService networkService;

    @Override
    public JSONObject fetchUser(Cookie[] cookies, String openId){
        JSONObject res = new JSONObject();
        int status = 200;
        String msg = "";
        logger.info("查询的openID " + openId);
        try {
            User users = userMapper.getUserInfo(openId);
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

    @Override
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

    @Override
    public JSONObject wechatAuth(String userId){
        JSONObject res = new JSONObject();
        return res;
    }

    @Override
    public Boolean checkVip(String openId) {
        int flag = 0;
        try{
            flag = userMapper.getUserRank(openId);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
        }
        if (flag == 1) { // rank=1 -> vip.
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public JSONObject register(String openId){
        ResultJSON res;
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");
        //插入用户信息
        if (!checkExists(openId)){
            try {
                userMapper.createUser(id, openId, "", "");
            }catch (Exception e){
                logger.error(e.getLocalizedMessage());
                res = ResultJSON.error(e.getLocalizedMessage());
                return JSONObject.parseObject(res.toSimpleString());
            }
        }else{
            res = ResultJSON.success(401, openId + " has already registered.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        //生成空人脉信息
        try {
            JSONObject tmpRes = networkService.createNull(id, openId, 0); //普通用户weight都为0；
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
            return JSONObject.parseObject(res.toSimpleString());
        }
        logger.info(openId + " has registered successfully.");
        res = ResultJSON.success(200, openId + " has registered successfully.");
        logger.info(res.toSimpleString());
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override
    public boolean checkExists(String openId){
        User user = null;
        try {
            user = userMapper.getUserInfo(openId);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
        }
        if (user != null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public JSONObject update2Vip(String openid){
        ResultJSON res;
        if(!checkExists(openid)){
            res = ResultJSON.success(401, "sorry, we have none this user.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        if(checkVip(openid)){
            res = ResultJSON.success(402, "sorry, the target user has already been vip.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        try {
            userMapper.update2Vip(openid);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
            return JSONObject.parseObject(res.toSimpleString());
        }
        res = ResultJSON.success(200, "update successfully.");
        return JSONObject.parseObject(res.toSimpleString());
    }
}
