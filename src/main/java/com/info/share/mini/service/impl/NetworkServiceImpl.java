package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.share.mini.controller.ArticleController;
import com.info.share.mini.entity.Network;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.User;
import com.info.share.mini.mapper.NetworkMapper;
import com.info.share.mini.mapper.UserMapper;
import com.info.share.mini.service.NetworkService;
import com.info.share.mini.service.UserService;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "2. 人脉接口列表", tags = {"人脉接口列表"})
@Service("networkService")
@Component
public class NetworkServiceImpl implements NetworkService {

    private static final Logger logger = LogManager.getLogger(NetworkServiceImpl.class);

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "networkMapper")
    private NetworkMapper networkMapper;

    @Override
    public JSONObject createNull(String id, String openid, int weight){
        ResultJSON res;
        try {
            networkMapper.insertNull(id, openid, weight);
            res = ResultJSON.success(200, "create empty network create successfully.");
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            if (e.getLocalizedMessage().contains("Duplicate entry")){
                res = ResultJSON.success(401, " already has this empty network.");
            }else{
                res = ResultJSON.error(e.getLocalizedMessage());
                logger.error(openid + " create network create failed.");
            }
        }
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override
    public JSONObject updateNetwork(User userInfo, Network networkInfo){
        ResultJSON res ;
        String openId = userInfo.getOpenid();
        // 判断用户是否存在
        if (!userService.checkExists(openId)){
            res = ResultJSON.success(203, "sorry, we have none this user.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        // 判断用户是否会员；
        if(!userService.checkVip(openId)){
            res = ResultJSON.success(203,"sorry, this function can only used by vip person.");
            return JSONObject.parseObject(res.toSimpleString());
        }
        // 更新用户信息
        try {
            userMapper.updateUserInfo(userInfo.getOpenid(), userInfo.getName(), userInfo.getCompany(),
                    userInfo.getCountry(), userInfo.getProvince(),
                    userInfo.getCity(), userInfo.getTel(), userInfo.getWeChat(), userInfo.isShowNumber());
            logger.info(userInfo.getName() + " " + userInfo.getOpenid() + " get userinfo update sucessfully.");
        }catch (Exception e){
            res = ResultJSON.error("failed to update user info, for more detail " + e.getLocalizedMessage());
            logger.error(userInfo.getName() + " " + userInfo.getOpenid() + " userinfo update failed. " + e.getLocalizedMessage());
            return JSONObject.parseObject(res.toSimpleString());
        }
        //更新人脉信息
        try {
            networkMapper.updateNetwork(userInfo.getOpenid(), networkInfo.get_abstract(), networkInfo.getDemand(),
                    networkInfo.getResource());
            logger.info(userInfo.getName() + " " + userInfo.getOpenid() + " network update sucessfully.");
        }catch (Exception e){
            res = ResultJSON.error("failed to update network info, for more detail " + e.getLocalizedMessage());
            logger.error(userInfo.getName() + " " + userInfo.getOpenid() + " network update failed. ");
        }
        res = ResultJSON.success(200, "updating successfully.");
        return JSONObject.parseObject(res.toSimpleString());
    }

    // 人脉列表
    @Override
    public JSONObject listNetworkByCount(int page, int pageSize){
        ResultJSON res;
        List<Network> networks;
        try{
            PageHelper.startPage(page, pageSize);
            networks = networkMapper.listNetwork();
            PageInfo pageInfo = new PageInfo(networks);
            int totalPage = pageInfo.getPages();
            JSONObject tmp = new JSONObject();
            tmp.put("networks", networks);
            res = ResultJSON.success(page, totalPage, pageSize, tmp);
        }catch (Exception e){
            logger.info(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toString());
    }

    @Override
    public JSONObject getNetworkDetail(String openid, String id){
        ResultJSON res ;
        try{
            Network network = networkMapper.fetchNetworkDetail(openid);
            JSONObject tmp = new JSONObject();
            tmp.put("network", network);
            res = ResultJSON.success(tmp);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleDataString());
    }
}
