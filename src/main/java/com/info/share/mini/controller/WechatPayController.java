package com.info.share.mini.controller;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.BillingConstants;
import com.info.share.mini.entity.WxPreOrder;
import com.info.share.mini.service.WxPayService;
import com.info.share.mini.utils.WechatPayConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Api(value = "微信支付接口",tags = {"7、小程序支付"})
@RequestMapping("/wxpay")
public class WechatPayController {

    @Resource(name = "wxPayService")
    private WxPayService wxPayService;

    @ApiOperation(value = "获取微信订单号", notes= "isBuyUsers为false，表示当前支付为开通VIP。true 则表示购买用户", httpMethod = "GET")
    @GetMapping(value = "/prePayId", produces = {"application/json;charset=UTF-8"})
    public JSONObject getPayId(@RequestParam(value = "openid") String userId,
                               @RequestParam(value = "wxNumber") String wxNumber,
                               @RequestParam(value = "phoneNumber") String phoneNumber,
                               @RequestParam(value = "taskId") String taskId,
                               @RequestParam(value = "totalMoney") int totalMoney,
                               @RequestParam(value = "isBuyUsers", defaultValue = "true") boolean isBuyUsers,
                               HttpServletRequest request, HttpServletResponse response){
        WxPreOrder wxPreOrder = new WxPreOrder();
        wxPreOrder.setSpbill_create_ip(request.getRemoteHost());
        wxPreOrder.setOut_trade_no(UUID.randomUUID().toString().replace("-", ""));
        wxPreOrder.setTotal_fee(totalMoney);
        wxPreOrder.setOpenid(userId);
        wxPreOrder.setTrade_type("JSAPI");
        String payType;
        if (isBuyUsers){
            payType = BillingConstants.BillingType.BUY_USERS.toString();
            wxPreOrder.setBody("批量粉丝购买。");
        }else{
            payType = BillingConstants.BillingType.BUY_VIP.toString();
            wxPreOrder.setBody("用户VIP充值。");
        }
        JSONObject res = wxPayService.getWxPrePayId(wxPreOrder, taskId, payType, wxNumber, phoneNumber);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    @ApiOperation(value = "支付结果回调接口", notes = "isSuccess 为true则支付成功，反之失败。", httpMethod = "PUT")
    @PutMapping(value = "/callback", produces = {"application/json;charset=UTF-8"})
    public JSONObject updateBillingStatus(@RequestParam(value = "wxPayId") String wxPayId,
                                          @RequestParam(value = "isSuccess") boolean isSuccess,
                                          HttpServletResponse response){
        String status = BillingConstants.BillingStatus.SUCCESS.toString();
        if (!isSuccess){
            status = BillingConstants.BillingStatus.FAILED.toString();
        }
        JSONObject res = wxPayService.updateBilling(wxPayId, status);
        response.setStatus(res.getIntValue("code"));
        return res;
    }

    @ApiOperation(value = "支付通知回调接口", httpMethod = "GET")
    @GetMapping(value = "/notify", produces = {"application/json;charset=UTF-8"})
    public JSONObject getNotify(HttpServletResponse response, HttpServletRequest request){
        return null;
    }

}
