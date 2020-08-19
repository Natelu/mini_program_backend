package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.mapper.BillingMapper;
import com.info.share.mini.service.BillingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("billingService")
@Component
public class BillingServiceImpl implements BillingService {

    private static final Logger logger = LogManager.getLogger(BillingServiceImpl.class);

    @Resource(name = "billingMapper")
    private BillingMapper billingMapper;

    @Override
    public JSONObject createBilling(String id, String userId, String wxNumber, String wxPayId, String taskId, String taskName, int money, String type, String status) {
        ResultJSON res;
        try {
            billingMapper.insertBilling(id, userId, wxNumber, wxPayId, taskId, taskName, money, type, status);
            res = ResultJSON.success("添加订单成功");
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error("添加订单失败，" + e.getLocalizedMessage());
        }
        return JSONObject.parseObject(res.toSimpleString());
    }

    @Override public JSONObject updateBillingStatus(String wxPayId, String status) {
        ResultJSON res;
        try {
            billingMapper.updateBillingStatus(wxPayId, status);
            res = ResultJSON.success("更新订单成功");
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            res = ResultJSON.error("更新订单失败");
        }
        return JSONObject.parseObject(res.toSimpleString());
    }
}
