package com.info.share.mini.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api(){
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
        .name("openid")
        .description("用户openid")
        .modelRef(new ModelRef("string"))
        .required(false)
        .parameterType("header")
        .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(""))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters)
                .host("www.qsssss.com")
                ;
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("小程序后端接口文档")
                .description("接口请求说明")
                .version("1.0")
                .build();
    }
}
