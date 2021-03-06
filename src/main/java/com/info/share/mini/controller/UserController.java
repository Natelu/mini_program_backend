package com.info.share.mini.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.User;
import com.info.share.mini.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "用户接口列表", tags = {"3. 用户接口列表"})
@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping(value = "/user/list", produces = {"application/json;charset=UTF-8"})
    public JSONObject fetchUserList(@RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                    HttpServletResponse response){
        JSONObject res = userService.getUserList(page, pageSize);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    @ApiOperation(value = "获取用户信息", notes= "获取用户信息", httpMethod = "GET")
    @GetMapping(value = "/user/{open_id}/info", produces = {"application/json;charset=UTF-8"})
    public JSONObject fetchUser(@PathVariable("open_id") String openId, HttpServletRequest request,
                            HttpServletResponse response){
        logger.info("request url is : " + request.getRequestURL());
        Cookie[] cookie = request.getCookies();
        JSONObject userRes = userService.fetchUser(cookie, openId);
        response.setStatus(userRes.getInteger("code"));
        return userRes;
    }

    @ApiOperation(value = "删除用户", notes= "删除用户", httpMethod = "DELETE")
    @DeleteMapping(value = "/user/{open_id}/delete", produces = {"application/json;charset=UTF-8"})
    public JSONObject deleteUser(@PathVariable("open_id") String openId, HttpServletRequest request,
                                HttpServletResponse response){
        logger.info("request url is : " + request.getRequestURL());
        JSONObject userRes = userService.deleteUser(openId);
        response.setStatus(userRes.getInteger("code"));
        return userRes;
    }

    @ApiOperation(value = "用户注册", notes= "用户注册", httpMethod = "GET")
    @GetMapping(value = "/user/register", produces = {"application/json;charset=UTF-8"})
    public String register(@RequestParam("openid") String openid,
                           HttpServletResponse response){
        JSONObject res = userService.register(openid);
        response.setStatus(res.getIntValue("code"));
        logger.info(res.toJSONString());
        return JSONObject.toJSONString(res);
    }

    @ApiOperation(value = "查询VIP状态", notes= "查询用户是否为VIP", httpMethod = "GET")
    @GetMapping(value = "/user/checkVip", produces = {"application/json;charset=UTF-8"})
    public JSONObject checkVip(@RequestParam("openid") String openId, HttpServletResponse response){

        JSONObject res = userService.checkVipJson(openId);

//        boolean isVip = false;
//        int code = 200;
//        if(userService.checkExists(openId)) {
//            if (userService.checkVip(openId)) {
//                isVip = true;
//            }
//            res.put("isVip", isVip);
//        }else{
//            String msg = "Sorry, we have none this user.";
//            res.put("msg", msg);
//            res.put("isVip", isVip);
//        }
//        res.put("code", code);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    @ApiOperation(value = "设置VIP", notes= "设置用户为VIP", httpMethod = "PUT")
    @PutMapping(value = "/user/setVip", produces = {"application/json;charset=UTF-8"})
    public String setVip(@RequestParam("openid") String openId, HttpServletResponse response){
        JSONObject res = userService.update2Vip(openId);
        response.setStatus(res.getIntValue("code"));
        return JSONObject.toJSONString(res);
    }

    @ApiOperation(value = "获取该用户邀请的VIP用户", notes = "获取邀请的VIP用户", httpMethod = "GET")
    @GetMapping(value = "/user/invitedVips", produces = {"application/json;charset=UTF-8"})
    public String getInvitedVipByUser(@RequestParam("openid") String openId,
                                      HttpServletResponse response){
        JSONObject res = new JSONObject();
        if (userService.checkExists(openId)){
            res = userService.invitedVipUsersByOpenId(openId);
        }else { //用户不存在
            res.put("code", 200);
            res.put("msg", "we have none this user.");
        }
        response.setStatus(res.getIntValue("code"));
        logger.info(res.toJSONString());
        return JSONObject.toJSONString(res);
    }

    @ApiOperation(value = "获取该用户的徒弟数", notes = "徒弟：经用户邀请，并完成任务", httpMethod = "GET")
    @GetMapping(value = "/user/getRefferals", produces = {"application/json;charset=UTF-8"})
    public JSONObject getRefferals(@RequestParam("openid") String openId,
                                      HttpServletResponse response){
        ResultJSON res;
        if (userService.checkExists(openId)){
            List<User> users = userService.getReferralList(openId);
            res = ResultJSON.success(users);
        }else { //用户不存在
            res = ResultJSON.success(200, "无此用户。");
        }
        response.setStatus(res.getCode());
        return JSONObject.parseObject(res.toSimpleDataString());
    }
}
