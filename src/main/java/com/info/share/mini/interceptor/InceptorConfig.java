package com.info.share.mini.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class InceptorConfig extends WebMvcConfigurationSupport {

    @Autowired
    private PermissionInterceptor permissionInterceptor;

//     @Override public void add
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**").excludePathPatterns("/user/setVip")
                .excludePathPatterns("/weChat/auth/QRImage").excludePathPatterns("/wxpay/callback");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 放行swagger
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // 配置跨域请求，允许所有站点访问
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
    }
}
