package com.info.share.mini.utils;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter;
import org.springframework.http.MediaType;
//import org.springframework.http.converter.cbor.MappingFastJsonMessageConverter;

import java.util.ArrayList;
import java.util.List;
//MappingJackson2CborHttpMessageConverter
public class WxMappingJackson2HttpMessageConverter extends FastJsonHttpMessageConverter {
    public WxMappingJackson2HttpMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        this.setSupportedMediaTypes(mediaTypes);}

}
