package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.share.mini.entity.*;
import com.info.share.mini.mapper.NetworkMapper;
import com.info.share.mini.mapper.TaskMapper;
import com.info.share.mini.service.NetworkService;
import com.info.share.mini.service.UserService;
import com.info.share.mini.mapper.UserMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("userService")
@Component
public class UserServiceImple implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImple.class);

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "networkMapper")
    private NetworkMapper networkMapper;

    @Resource(name = "networkService")
    private NetworkService networkService;

    @Resource(name = "taskMapper")
    private TaskMapper taskMapper;

    @Override
    public JSONObject fetchUser(Cookie[] cookies, String openId){
        ResultJSON resUser ;
        JSONObject res = new JSONObject();
        try {
            User user = userMapper.getUserInfo(openId);
            if (user!= null){
                res.put("userInfo", user);
                resUser = ResultJSON.success(res);
            }else{
                resUser = ResultJSON.success(202, "has none user which openid is " + openId);
            }
        }catch (Exception e){
            resUser = ResultJSON.error(e.getLocalizedMessage());
        }
        logger.info(resUser.toSimpleDataString());
        return JSONObject.parseObject(resUser.toSimpleDataString());
    }

    @Override public JSONObject deleteUser(String openId) {
        ResultJSON res;
        try{
            userMapper.deleteUser(openId);
            networkMapper.deleteNetwork(openId);
            res = ResultJSON.success("delete successfully.");
        }catch (Exception e){
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
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

    // 这里的逻辑，注册完用户后，顺便生成一个空的人脉信息，方便升级vip后直接更新人脉信息
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
            logger.info("开始生成空人脉信息 " + id + openId);
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

    // 获取用户简要信息
    @Override
    public JSONObject getBasicUserInfo(String openId){
        ResultJSON res;
        JSONObject tmp_user = new JSONObject();
        try {
            UserInfoBasic userInfoBasic = userMapper.getUserInfoBasic(openId);
            if (userInfoBasic!= null){
                tmp_user.put("basicUserInfo", userInfoBasic);
                res = ResultJSON.success(tmp_user);
            }else{
                res = ResultJSON.success(202, "has none user which openid is " + openId);
            }
        }catch (Exception e){
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        logger.info(res.toSimpleDataString());
        return JSONObject.parseObject(res.toSimpleDataString());
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
    public JSONObject checkExistsJson(String openId) {
        JSONObject res = new JSONObject();
        boolean isExist = false;
        if(checkExists(openId)){
            isExist = true;
        }
        res.put("code", 200);
        res.put("isExist", isExist);
        return res;
    }

    @Override
    public JSONObject checkVipJson(String openId) {
        JSONObject existRes = checkExistsJson(openId);
        if(existRes.getBooleanValue("isExist")){
            JSONObject res = new JSONObject();
            res.put("code", 200);
            res.put("isVip", checkVip(openId));
            return res;
        }else{
            return JSONObject.parseObject(ResultJSON.success(400, "无此用户.").toSimpleString());
        }
    }

    @Override
    public JSONObject update2Vip(String openid){
        ResultJSON res;
        if(!checkExists(openid)){
            res = ResultJSON.success("sorry, we have none this user.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        if(checkVip(openid)){
            res = ResultJSON.success("sorry, the target user has already been vip.");
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

    @Override
    public JSONObject invitedVipUsersByOpenId(String openId){

        List<User> users ;
        ResultJSON res;
        try {
            users = userMapper.getInviteUsers(openId);
            JSONObject tmp_res = new JSONObject();
            int count = 0;
            if (!users.isEmpty()){
                count = users.size();
            }
            tmp_res.put("count", count);
            tmp_res.put("invitedVipPerson", users);
            res = ResultJSON.success(tmp_res);
            return JSONObject.parseObject(res.toSimpleDataString());
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        logger.info(res.toSimpleDataString());
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override
    public JSONObject getUserList(int page, int pageSize){
        ResultJSON res = null;
        List<User> list = null;
        try{
            PageHelper.startPage(page, pageSize);
            list = userMapper.fetchAllUser();
            PageInfo pageInfo = new PageInfo(list);
            int totalPage = pageInfo.getPages();
            JSONObject temp = new JSONObject();
            temp.put("users", list);
            res = ResultJSON.success(page, pageSize, totalPage, temp);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toString());
    }

    @Override
    public List<User> getReferralList(String openId) {
        List<User> users = new ArrayList<>();
        try{
            // 获取用户邀请的所有下线用户列表
            users = userMapper.getAllReferrals(openId);
            // 过滤掉没完成任务用户
            for(User user:users){
                // 查询当前用户的任务领取列表
                List<TaskDo> taskDos = taskMapper.getDoTasksByUser(openId);
                for(TaskDo taskDo:taskDos){
                    // 如果当前用户有完成任务 则保留，否则去除
                    if(taskDo.getStatus().equals(TaskStatus.done.toString())){
                        continue;
                    }
                }
                users.remove(user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return users;
    }

    // 发展下线，提升的佣金比例
    @Override
    public float getBonusPercentage(String userId){
        // 获取用户徒弟数
        List<User> users = getReferralList(userId);
        float percentage = 0.0f;
        percentage += users.size()*0.02;
        return Math.min(0.1f, percentage);
    }
}
