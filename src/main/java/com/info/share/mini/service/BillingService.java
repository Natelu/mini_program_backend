package com.info.share.mini.service;

import com.alibaba.fastjson.JSONObject;

public interface BillingService {

    JSONObject createBilling(String id, String userId, String wxPayId, String taskId, int money, String type, String status);

    JSONObject updateBillingStatus(String wxPayId, String stauts);
}
