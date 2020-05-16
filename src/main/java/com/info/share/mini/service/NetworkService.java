package com.info.share.mini.service;

import com.alibaba.fastjson.JSONObject;
import com.info.share.mini.entity.Network;
import com.info.share.mini.entity.User;

public interface NetworkService {

    JSONObject createNull(String id, String openId, int weight);
    //人脉信息更新
    JSONObject updateNetwork(User userInfo, Network networkInfo);

    //根据点击量返回 network 列表
    JSONObject listNetworkByCount(int page, int pageSize);

    JSONObject getNetworkDetail(String openid, String id);

    // 根据关键字搜索人脉
    JSONObject searchNetwork(String keyword, int page, int pageSize);
}
