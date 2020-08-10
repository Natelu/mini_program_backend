package com.info.share.mini.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.info.share.mini.entity.WxPreOrder;
import net.bytebuddy.utility.RandomString;
import org.apache.lucene.util.fst.Util;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.yaml.snakeyaml.serializer.Serializer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

@Component
public class WechatPayUtil {

    private static String buildOrderUrl;
    @Value("${wechat.buildOrder}")
    public void setBuildOrderUrl(String buildOrderUrl1) {
        buildOrderUrl = buildOrderUrl1;
    }

    private static String appKey;
    @Value("${wechat.appKey}")
    public void setAppKey(String appKey1){
        appKey = appKey1;
    }

    private static String appId;
    @Value("${wechat.appId}")
    public void setAppId(String appId1){
        appId= appId1;
    }

    @Value("${wechat.mchId}")
    private String mchId;
    public void setMchId(String mchId1){
        appId= mchId1;
    }

    public String genSign(WxPreOrder preOrder){
        // 参数排序，转字符串，形如 appid=wxd930ea5d5a258f4f&body=test
        preOrder.setAppid(appId);
        preOrder.setMch_id(mchId);
        preOrder.setNonce_str(RandomString.make(30));
        JSONObject params = JSONObject.parseObject(JSONObject.toJSONString(preOrder));
        Set<String> keySet = params.keySet();
        List<String> keysList = new ArrayList<String>(keySet);
//        Collections.sort(keysList);
        String rawStr = "";
        for (int i = 0; i< keysList.size(); i++){
            rawStr += keysList.get(i);
            rawStr += "=";
            rawStr += params.getString(keysList.get(i));
            rawStr += "&";
        }
        String signTemp = rawStr + "keys=" + appKey;
        String sign = DigestUtils.md5DigestAsHex(signTemp.getBytes());
        return sign;
    }

    public JSONObject genPreOrder(WxPreOrder wxPreOrder){
        wxPreOrder.setSign(genSign(wxPreOrder));
        // TODO 请求获取订单
        return null;
    }
}
