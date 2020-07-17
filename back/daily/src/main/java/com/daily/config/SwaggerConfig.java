package com.daily.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Timestamp;
import java.util.Date;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.show}")
    private boolean swaggerShow;

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo()).select()
            .apis(RequestHandlerSelectors.any())
            .paths(Predicates.not(PathSelectors.regex("/error.*"))) // 错误路径不监控
            .paths(PathSelectors.regex("/.*")) // 对根下所有路径进行监控
            .build().directModelSubstitute(Timestamp.class, Date.class);
    }

/*    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .enable(swaggerShow)
            .apiInfo(apiInfo())
            .pathMapping("/")
            .select() // 选择那些路径和api会生成document
            .apis(RequestHandlerSelectors.any())// 对所有api进行监控
            .apis(RequestHandlerSelectors.basePackage("com.daily.controller"))
            .build().directModelSubstitute(Timestamp.class, Date.class);
    }*/

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("日报swagger")    //标题
            .description("Restful-API-Doc")    //描述
            .termsOfServiceUrl("https://www.baidu.com") //这里配置的是服务网站
            .contact(new Contact("jack", "https://www.baidu.com", "xx@qq.com")) // 三个参数依次是姓名，个人网站，邮箱
            .version("1.0") //版本
            .build();
    }

}
