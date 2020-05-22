package com.info.share.mini.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;

@Component
public class wechatAuthUtil {

    private static final Logger logger = LogManager.getLogger(wechatAuthUtil.class);


    private static String appId;
    @Value("${wechat.appId}")
    private void setAppId(String tempAppId){
        appId = tempAppId;
    }

    private static String appSercret;
    @Value("${wechat.appSecret}")
    private void setSrcret(String sct){
        appSercret = sct;
    }

    private static String sessionKeyUrl;
    @Value("${wechat.code2SessionUrl}")
    private void setSessionKeyUrl(String url){
        sessionKeyUrl = url;
    }

    private static String accessTokenUrl;
    @Value("${wechat.accessTokenUrl}")
    private void setAccessTokenUrl(String url){
        accessTokenUrl = url;
    }

    // 通过前台的code获取用户的openid、sessionKey;
    public static JSONObject code2Session(String code){
        logger.info("appid is :" + appId);
        JSONObject res = null;
        String tempUrl = sessionKeyUrl;
        tempUrl = tempUrl.replace("APPID", appId);
        tempUrl = tempUrl.replace("SECRET", appSercret);
        tempUrl = tempUrl.replace("JSCODE", code);
        logger.info("code2session url : " + tempUrl);
        ResponseEntity response;
        response = HttpUtil.simpleGet("https", tempUrl, HttpMethod.GET, JSONObject.class);
        if (response.getStatusCodeValue() < 300){
            res = JSON.parseObject(JSON.toJSONString(response.getBody()));
        }
        return res;
    }

    // 获取accesstoken
    public JSONObject getAccessToken(){
        JSONObject res = null;
        String tempUrl = accessTokenUrl;
        tempUrl = tempUrl.replace("APPID", appId);
        tempUrl = tempUrl.replace("APPSECRET", appSercret);
        ResponseEntity responseEntity;
        responseEntity = HttpUtil.simpleGet("https", tempUrl, HttpMethod.GET, JSONObject.class);
        if (responseEntity.getStatusCodeValue() < 300){
            res = JSONObject.parseObject(JSON.toJSONString(responseEntity.getBody()));
        }
        return res;
    }

    //TODO:
    //通过session_key校验用户数据合法性
    public static JSONObject decryptedWechat (String encryptedData, String iv, String sessionKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchProviderException, InvalidParameterSpecException {
        Base64 base64 = new Base64();
        byte[] ivDecoder = base64.decode(iv);
        byte[] encryptedDataDecder = base64.decode(encryptedData);
        byte[] sessionKeydecoder = base64.decode(sessionKey);
        int base = 16;
        logger.info("sessionKey is : " + sessionKey + ", length: " + sessionKeydecoder.length);
        logger.info("sessionKey " + sessionKey);
        logger.info("iv " + iv + "，changdu shi " + iv.length());
        logger.info("encrypteData" + encryptedData);
        if (sessionKeydecoder.length % base != 0) {
            int groups = sessionKeydecoder.length / base + (sessionKeydecoder.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(sessionKeydecoder, 0, temp, 0, sessionKeydecoder.length);
            sessionKeydecoder = temp;
        }
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        Key secretKeySpec = new SecretKeySpec(sessionKeydecoder,
                "AES");
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
        parameters.init(new IvParameterSpec(ivDecoder));
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameters);

//        SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeydecoder,"AES");
//        IvParameterSpec ivParam = new IvParameterSpec(ivDecoder);
//        Security.addProvider(new BouncyCastleProvider());
//        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
//
//        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParam);
        JSONObject res = new JSONObject();
        try{
            byte[] bytes = cipher.doFinal(encryptedDataDecder);
//            byte[] bytes = PKCS7Encoder.decode(rawBytes);
            logger.info(new String(bytes));
            res = JSONObject.parseObject(new String(bytes, "UTF-8"));
            JSONObject waterMark = res.getJSONObject("watermark");
            boolean verifyStatus = true;
            if (waterMark == null || !waterMark.getString("appid").equals(appId)){
                res.put("code", 400);
                verifyStatus = false;
            }
            res.put("verifyStatus", verifyStatus);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
