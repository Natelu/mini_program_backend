package com.info.share.mini.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private static String qrImagesPath;
    @Value("${wechat.QRImagePath}")
    private void setQrImagesPath(String path){ qrImagesPath = path;}

    private static String nginxStaticUrl;
    @Value("${wechat.nginxUrl}")
    private void setNginxStaticUrl(String url){ nginxStaticUrl = url;}

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
    public static JSONObject getAccessToken(){
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

    //通过session_key校验用户数据合法性
    public static JSONObject decryptedWechat (String encryptedData, String iv, String sessionKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, NoSuchProviderException, InvalidParameterSpecException {
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

    // 获取小程序二维码
    public static String getORImage(String openId, String scene, String accessToken, String page, int width) throws IOException {
        JSONObject body = new JSONObject();
        body.put("scene", scene);
        body.put("access_token", accessToken);
        body.put("page", page);
        body.put("width", width);

//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
//        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
//
//        StringEntity entity = new StringEntity(body.toString());
//        entity.setContentType("image/png");
//        httpPost.setEntity(entity);
//
//        HttpResponse response;
//        response = client.execute(httpPost);
//        InputStream inputStream = response.getEntity().getContent();
//        logger.info("返回状态：" + response.getEntity().getContent().toString());

        URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
        printWriter.write(body.toString());
        printWriter.flush();
        BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

        String filePath = qrImagesPath;
        logger.info(filePath);
        String fileName =  openId + ".png";
        int state = saveImageByInputStream(inputStream, filePath,  fileName);
        if (state == 1){
            return nginxStaticUrl + fileName;
        }else{
            return null;
        }
    }

    /**
     * 将二进制转换成文件保存
     * @param inputStream 二进制流
     * @param filePath 图片的保存路径
     * @return
     *      1：保存正常
     *      0：保存失败
     */
    public static int saveImageByInputStream(InputStream inputStream, String filePath, String fileName){
        int stateInt = 1;
        if(inputStream != null){
            try {
                File file=new File(filePath, fileName);//可以是任何图片格式.jpg,.png等
                if(file.exists()){
                   file.delete();
                }
                file.createNewFile();
                FileOutputStream fos=new FileOutputStream(file);
                byte[] b = new byte[1024];
                int nRead;
                while ((nRead = inputStream.read(b)) != -1) {
                    fos.write(b, 0, nRead);
                    fos.flush();
                }
                fos.close();
            } catch (Exception e) {
                stateInt = 0;
                e.printStackTrace();
            } finally {
            }
        }
        return stateInt;
    }
}
