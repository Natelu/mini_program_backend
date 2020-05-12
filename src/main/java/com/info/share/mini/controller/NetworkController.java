package com.info.share.mini.controller;
import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.Network;
import com.info.share.mini.entity.User;
import com.info.share.mini.service.NetworkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "人脉接口",tags = {"2. 人脉接口列表"})
@RequestMapping("/network")
public class NetworkController {
    private static final Logger logger = LogManager.getLogger(NetworkController.class);

    @Resource(name = "networkService")
    private NetworkService networkService;

    @ApiOperation(value = "人脉更新", notes= "人脉更新", httpMethod = "PUT")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", value = "用户名字", paramType = "body", required = true),
//            @ApiImplicitParam(name = "openid", value = "用户openid", paramType = "body", required = true),
//            @ApiImplicitParam(name = "company", value = "公司", paramType = "body", required = true),
//            @ApiImplicitParam(name = "position", value = "职位", paramType = "body", required = true),
//            @ApiImplicitParam(name = "weChat", value = "微信号", paramType = "body", required = true),
//            @ApiImplicitParam(name = "tel", value = "手机号", paramType = "body", required = true),
//            @ApiImplicitParam(name = "country", value = "国家", paramType = "body", required = true),
//            @ApiImplicitParam(name = "province", value = "省份", paramType = "body", required = true),
//            @ApiImplicitParam(name = "city", value = "城市", paramType = "body", required = true),
//            @ApiImplicitParam(name = "isShowNumber", value = "是否显示微信号", paramType = "body", required = true),
//            @ApiImplicitParam(name = "abstract", value = "简介", paramType = "body", required = true),
//            @ApiImplicitParam(name = "demand", value = "需求", paramType = "body", required = true),
//            @ApiImplicitParam(name = "resource", value = "资源", paramType = "body", required = true)
//    })
    @PutMapping(value = "/update", produces = {"application/json;charset=UTF-8"})
    public String updateNetwork(@RequestBody JSONObject body, HttpServletResponse response){
//        String openid = body.getString("openid");
//        String
        logger.info(body.toJSONString());
        User user = buildUser(body);
        Network network = buildNetwork(body);
        JSONObject res = networkService.updateNetwork(user, network);
        response.setStatus(res.getIntValue("code"));
        return JSONObject.toJSONString(res);
    }

    // 获取人脉列表
    @RequestMapping(value = "/list/byCount", produces = {"application/json;charset=UTF-8"})
    public String listNetworkByCount(@RequestParam(value = "pageNumber" , defaultValue = "1") int page,
                                     @RequestParam(value = "pageSize" , defaultValue = "10") int pageSize,
                                     HttpServletResponse response){
        JSONObject res = networkService.listNetworkByCount(page, pageSize);
        response.setStatus(res.getIntValue("code"));
        return JSONObject.toJSONString(res);
    }

    @RequestMapping(value = "/detail/{open_id}/{network_id}", produces = {"application/json;charset=UTF-8"})
    public String detailNetwork(@PathVariable("open_id") String openId,
                                @PathVariable("network_id") String networkId,
                                HttpServletResponse response){
        JSONObject res = networkService.getNetworkDetail(openId, networkId);
        response.setStatus(res.getIntValue("code"));
        return JSONObject.toJSONString(res);
    }
    private User buildUser(JSONObject body){
        User user = new User();
        user.setOpenid(body.getString("openId"));
        user.setName(body.getString("name"));
        user.setCompany(body.getString("company"));
        user.setPosition(body.getString("position"));
        user.setShowNumber(body.getBoolean("isShowNumber"));
        user.setWeChat(body.getString("weChat"));
        user.setTel(body.getString("tel"));
        user.setCountry(body.getString("country"));
        user.setProvince(body.getString("province"));
        user.setCity(body.getString("city"));
        return user;
    }

    private Network buildNetwork(JSONObject body){
        Network network = new Network();
        network.set_abstract(body.getString("abstract"));
        network.setDemand(body.getString("demand"));
        network.setResource(body.getString("resource"));
        network.setOpenId(body.getString("openid"));
        return network;
    }
}
