package com.recsoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;
import static com.google.common.base.Predicates.or;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.recsoft"))
                .paths(regex("/.*")).build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Reksoft Study Swagger API")
                .description("Studi progect for me and reksoft")
                .termsOfServiceUrl("http://www.apache.org/licesen.html")
                .contact("36furious@gmail.com").license("Reksoft Study Swagger API")
                .licenseUrl("36furious@gmail.com").version("1.0").build();
    }

}