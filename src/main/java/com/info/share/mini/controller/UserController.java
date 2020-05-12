package com.info.share.mini.controller;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.service.UserService;
import io.swagger.annotations.Api;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "用户接口列表", tags = {"3. 用户接口列表"})
@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping(value = "/user/{open_id}/info", produces = {"application/json;charset=UTF-8"})
    public String fetchUser(@PathVariable("open_id") String openId, HttpServletRequest request,
                            HttpServletResponse response){
        logger.info("request url is : " + request.getRequestURL());
        Cookie[] cookie = request.getCookies();
        JSONObject userRes = userService.fetchUser(cookie, openId);
        response.setStatus(userRes.getInteger("status"));
        return JSONObject.toJSONString(userRes.get("data"));
    }

    @GetMapping(value = "/user/register", produces = {"application/json;charset=UTF-8"})
    public String register(@RequestParam("openid") String openid,
                           HttpServletResponse response){
        JSONObject res = userService.register(openid);
        response.setStatus(res.getIntValue("code"));
        logger.info(res.toJSONString());
        return JSONObject.toJSONString(res);
    }

    @PutMapping(value = "/user/setVip", produces = {"application/json;charset=UTF-8"})
    public String setVip(@RequestParam("openid") String openId, HttpServletResponse response){
        JSONObject res = userService.update2Vip(openId);
        response.setStatus(res.getIntValue("code"));
        return JSONObject.toJSONString(res);
    }
}
