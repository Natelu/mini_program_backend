package com.info.share.mini.service;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.WxPreOrder;

public interface WxPayService {

    JSONObject getWxPrePayId(WxPreOrder wxPreOrder, String taskId, String type);

    JSONObject updateBilling(String wxPayId, String status);
}
