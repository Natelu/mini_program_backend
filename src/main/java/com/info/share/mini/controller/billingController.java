package com.info.share.mini.controller;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.service.BillingService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "8. 账单接口",tags = {"8. 账单接口列表"})
public class billingController {

    @Resource(name = "billingService")
    private BillingService billingService;

    @GetMapping(value = "/billing/listByTime", produces = {"application/json;charset=UTF-8"})
    public JSONObject fetArticleByTime(HttpServletResponse response){
        JSONObject res = billingService.fetchAllBillings();
        response.setStatus(res.getIntValue("code"));
        return res;
    }
}
