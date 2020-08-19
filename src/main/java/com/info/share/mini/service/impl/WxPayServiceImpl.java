package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.BillingConstants;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.Task;
import com.info.share.mini.entity.WxPreOrder;
import com.info.share.mini.mapper.BillingMapper;
import com.info.share.mini.mapper.TaskMapper;
import com.info.share.mini.service.BillingService;
import com.info.share.mini.service.WxPayService;
import com.info.share.mini.utils.Tools;
import com.info.share.mini.utils.WechatPayUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service(value = "wxPayService")
public class WxPayServiceImpl implements WxPayService {

    private static final Logger logger = LogManager.getLogger(WxPayServiceImpl.class);

    @Resource(name = "billingService")
    private BillingService billingService;

    @Resource(name = "taskMapper")
    private TaskMapper taskMapper;

    @Override public JSONObject getWxPrePayId(WxPreOrder wxPreOrder, String taskId, String type, String wxNumber) {
        ResultJSON res ;
        try {
            // step 1: 请求微信支付api， 获取微信订单
            JSONObject response = WechatPayUtil.genPreOrder(wxPreOrder);
            if(response.getIntValue("code") >= 300){
                return response;
            }
            JSONObject wxResp = response.getJSONObject("response");
            String payId = wxResp.getString("prepay_id");
            final String packages = "prepay_id=" + payId;
            logger.info("packages : " + packages);
            // step 2: 获取微信订单成功，插入订单库。
            String id = Tools.getRandomId();
            Task task = taskMapper.getTaskDetail(taskId);
            if (task == null){
                res = ResultJSON.error("无此任务");
                return JSONObject.parseObject(res.toSimpleString());
            }
            JSONObject billingRes = billingService.createBilling(id, wxPreOrder.getOpenid(), wxNumber, payId, taskId,
                    task.getName(), wxPreOrder.getTotal_fee(),
                    type, BillingConstants.BillingStatus.CREATED.toString());
            if(billingRes.getIntValue("code") != 200){
                return billingRes;
            }
            // step 3: 返回前台请求数据。
            JSONObject data = new JSONObject();
            data.put("appId", wxPreOrder.getAppid());
            data.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            data.put("nonceStr", Tools.getRandomId());
            data.put("signType", "MD5");
            data.put("package", packages);
            String sign = WechatPayUtil.Md5String(data);
            data.put("paySign", sign);
            data.remove("appId");
            res = ResultJSON.success(data);
            return JSONObject.parseObject(res.toSimpleDataString());
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
            return JSONObject.parseObject(res.toSimpleString());
        }
    }

    @Override public JSONObject updateBilling(String wxPayId, String status) {
        ResultJSON res;
        try{
            billingService.updateBillingStatus(wxPayId, status);
            res = ResultJSON.success("更新订单成功");
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error(e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }
}
