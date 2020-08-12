package com.info.share.mini.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.info.share.mini.entity.ResultJSON;
import com.info.share.mini.entity.WxPreOrder;
import net.bytebuddy.utility.RandomString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(WechatPayUtil.class);

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
    private static String mchId;
    public void setMchId(String mchId1){
        appId= mchId1;
    }

    @Value("${wechat.notifyUrl}")
    private String notifyUrl;

    public static String genSign(WxPreOrder preOrder){
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

    public static JSONObject genPreOrder(WxPreOrder wxPreOrder) throws Exception{
        wxPreOrder.setSign(genSign(wxPreOrder));
        String requestData = WechatPayXmlUtil.buildXmlString(wxPreOrder);
        String url = "https://" + buildOrderUrl;
        JSONObject response = HttpUtil.requestXmlData(url, requestData, 10*1000, 10*1000);
        JSONObject result = new JSONObject();
        // TODO 请求获取订单
        String returnCode = response.getString("result_code");
        if (!WechatPayConstants.SUCCESS.equals(returnCode)){
            logger.error("获取支付订单失败 resultCode="+returnCode);
            result = JSONObject.parseObject(ResultJSON.error("获取支付订单失败").toSimpleString());
        }
        String prepay_id = response.getString("prepay_id");
        if (prepay_id == null) {
            logger.error("获取支付订单prepay_id失败{} prepay_id="+prepay_id);
            result = JSONObject.parseObject(ResultJSON.error("获取支付订单prepay_id失败").toSimpleString());
        }
        // 处理订单库逻辑，将订单状态置为doing 状态
        result.put("response", response);
        return result;
    }
}
