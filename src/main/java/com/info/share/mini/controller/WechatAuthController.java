package com.info.share.mini.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.service.WeChatAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "4. 微信认证相关接口",tags = {"4. 微信认证接口"})
@RequestMapping("/weChat/auth")
public class WechatAuthController {

    @Resource(name = "weChatAuthService")
    private WeChatAuthService weChatAuthService;

    @ApiOperation(value = "检查用户状态，并返回openid", notes= "用户状态包括：未注册 && 已注册 两种。", httpMethod = "GET")
    @GetMapping(value = "/checkUser", produces = {"application/json;charset=UTF-8"})
    public JSONObject checkUser(@RequestParam(value = "code") String code,
                                HttpServletResponse response){
        JSONObject res = weChatAuthService.checkUser(code);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    @ApiOperation(value = "注册用户", notes= "通过openid+encryptedData 进行注册", httpMethod = "GET")
    @GetMapping(value = "/register", produces = {"application/json;charset=UTF-8"})
    public JSONObject registerUser(@RequestParam(value = "openid") String openId,
                              @RequestParam(value = "encryptedData") String encryptedData,
                              HttpServletResponse response){
        JSONObject res = weChatAuthService.register(openId, encryptedData);
        response.setStatus(res.getIntValue("code"));
        return res;
    }
}
