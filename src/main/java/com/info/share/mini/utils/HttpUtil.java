package com.info.share.mini.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.net.ssl.HostnameVerifier;
import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HttpUtil {
    private static final Logger logger = LogManager.getLogger(HttpUtil.class);
    private static final HostnameVerifier PROMISCUOUS_VERIFIER = (s, sslSession) -> true;

    public static ResponseEntity request(String protocol, String url, HttpMethod method, Cookie[] cookies, Object params, Class clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Request-From", "miniProgram");
        List<String> cookiesList = setCookie(cookies);
        if (cookiesList.size() > 0){
            headers.put(HttpHeaders.COOKIE, cookiesList);
        }
        HttpEntity entity = new HttpEntity<>(params, headers);
        RestTemplate template;
        if (protocol.equals("https")) {
            template = new RestTemplate(new HttpsClientRequestFactory());
        } else{
            template = new RestTemplate();
        }
        String requestUrl = protocol + "://" + url;
        return template.exchange(requestUrl, method, entity, clazz);
    }

    public static ResponseEntity simpleGet(String protocol, String url, HttpMethod method, Class clazz){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity entity = new HttpEntity<>(null, headers);
//        RestTemplate
        RestTemplate template = new RestTemplate();
        if (protocol.equals("https")){
//            template = new RestTemplate((new HttpsClientRequestFactory()));
            template.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        }else{
            template = new RestTemplate();
        }
        String requestUrl = protocol + "://" + url;
        ResponseEntity responseEntity = null;
        try {
           responseEntity = template.exchange(requestUrl, method, entity, clazz);
        }catch (RestClientResponseException e){
            logger.info("request url " + requestUrl);
            logger.info(e.getRawStatusCode());
        }
        return responseEntity;
    }

    public static ResponseEntity billingRequest(String url, HttpMethod method, Class clazz, String instanceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Request-From", "miniProgram");
        HttpEntity entity = new HttpEntity<>(null, headers);
        RestTemplate template = new RestTemplate();
        ResponseEntity response = null;
        try{
            // RestClientException 包括客户端（4_xx）和服务器（5_xx）错误
            response = template.exchange(url, method, entity, clazz);
        }catch(RestClientResponseException e){
            logger.info(e.getRawStatusCode());
        }
        return response;
    }

    public static List<String> setCookie(Cookie[] cookies){
        List<String> cookieList = new ArrayList<>();
        if (cookies != null && cookies.length > 0){
            for(Cookie cookie: cookies){
                cookieList.add(cookie.getName()+ "=" + cookie.getValue());
            }
        }
        return cookieList;
    }

    public static JSONObject requestXmlData(String url, String xml, int connectTimeoutMs, int readTimeoutMs){
        JSONObject res = new JSONObject();
        // 无需证书
        BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                null,
                null,
                null
        );
        try {
            HttpClient httpClient = HttpClientBuilder.create()
                    .setConnectionManager(connManager)
                    .build();
            HttpPost httpPost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
            httpPost.setConfig(requestConfig);

            StringEntity postEntity = new StringEntity(xml, "UTF-8");
            httpPost.addHeader("Content-Type", "text/xml");
//            httpPost.addHeader("User-Agent", USER_AGENT + " " + config.getMchID());
            httpPost.setEntity(postEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            org.apache.http.HttpEntity httpEntity = httpResponse.getEntity();
            res = xmlToMap(EntityUtils.toString(httpEntity, "UTF-8"));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
        return res;
    }

    public static JSONObject xmlToMap(String strXML) throws Exception {
        try {
            JSONObject data = new JSONObject();
            DocumentBuilder documentBuilder = WechatPayXmlUtil.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            logger.error("Invalid XML, can not convert to map. Error message: {}. XML content: {}", ex.getMessage(), strXML);
            throw ex;
        }

    }
}
