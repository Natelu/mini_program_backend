package com.info.share.mini.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

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
        RestTemplate template;
        if (protocol.equals("https")){
            template = new RestTemplate((new HttpsClientRequestFactory()));
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
}
