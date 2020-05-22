package com.info.share.mini.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.service.WeChatAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "4. 微信认证相关接口",tags = {"4. 微信认证接口"})
@RequestMapping("/weChat/auth")
public class WechatAuthController {

    private static final Logger logger = LogManager.getLogger(WechatAuthController.class);

    @Resource(name = "weChatAuthService")
    private WeChatAuthService weChatAuthService;

    @ApiOperation(value = "检查用户状态，并返回openid", notes= "前端通过wx.login() 返回得到的code，调用该接口得到用户的openid, session_key" +
            "以及unionID， 由于涉及到安全，只给前端返回用户openId+用户状态，用户状态包括：未注册 && 已注册 两种，若用户未注册，继续调用register" +
            "接口进行注册操作。", httpMethod = "GET")
    @GetMapping(value = "/checkUser", produces = {"application/json;charset=UTF-8"})
    public String checkUser(@RequestParam(value = "code") String code,
                                HttpServletResponse response){
        logger.info("request code is " + code);
        JSONObject res = weChatAuthService.checkUser(code);
//        response.setStatus(res.getIntValue("code"));
        logger.info(res.toJSONString());
        return res.toJSONString();
    }

    @ApiOperation(value = "用户注册", notes= "使用wx.getUserInfo() 得到的数据进行用户注册", httpMethod = "POST")
    @PostMapping(value = "/register/byWeChat", produces = {"application/json;charset=UTF-8"})
    public JSONObject register(@RequestBody JSONObject body,
                               HttpServletResponse response){

        String iv = body.getString("iv");
        String openId = body.getString("openid");
        String encryptedData = body.getString("encryptedData");
        String invitedBy = body.getString("invitedBy");
        logger.info("iv " +  iv + " , data: " + encryptedData);
        JSONObject res = weChatAuthService.register(openId, encryptedData, iv, invitedBy);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    @ApiOperation(value = "获取用户手机号", notes= "使用UI 组件 getPhoneNumber， 获取用户手机号加密数据，更新数据库", httpMethod = "GET")
    @GetMapping(value = "/phoneNumber", produces = {"application/json;charset=UTF-8"})
    public JSONObject getPhoneNumber(@RequestParam(value = "openid") String openId,
                              @RequestParam(value = "encryptedData") String encryptedData,
                                     @RequestParam(value = "iv") String iv,
                              HttpServletResponse response){
        JSONObject res = weChatAuthService.updatePhone(openId, encryptedData, iv);
        response.setStatus(res.getIntValue("code"));
        return res;
    }
}
