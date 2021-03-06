package com.info.share.mini.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.Billing;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.mapper.BillingMapper;
import com.info.share.mini.service.BillingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("billingService")
@Component
public class BillingServiceImpl implements BillingService {

    private static final Logger logger = LogManager.getLogger(BillingServiceImpl.class);

    @Resource(name = "billingMapper")
    private BillingMapper billingMapper;

    @Override
    public JSONObject createBilling(String id, String userId, String wxNumber, String phoneNumber, String wxPayId, String taskId, String taskName, int money, String type, String status) {
        ResultJSON res;
        try {
            billingMapper.insertBilling(id, userId, wxNumber, phoneNumber, wxPayId, taskId, taskName, money, type, status);
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

    @Override
    public JSONObject fetchAllBillings() {
        ResultJSON res;
        List<Billing> billingList = new ArrayList<>();
        try{
            billingList = billingMapper.fetchALlSuccessBillings();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        res = ResultJSON.success(billingList);
        return JSON.parseObject(res.toSimpleDataString());
    }
}
