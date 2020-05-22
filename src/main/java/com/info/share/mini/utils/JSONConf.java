package com.info.share.mini.utils;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.yaml.snakeyaml.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

//@Configuration
public class JSONConf {

//    @Bean
//    public HttpMessageConverter fastJsonHttpMessageConverters(){
//          FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
//          FastJsonConfig config = new FastJsonConfig();
//          config.setDateFormat("yyyy-MM-dd");
//          converter.setFastJsonConfig(config);
//          return converter;
//    }
}