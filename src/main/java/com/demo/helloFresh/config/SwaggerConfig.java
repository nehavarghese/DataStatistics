package com.demo.helloFresh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Value("${application.version}")
    String version;

    @Bean
    public Docket getSwaggerConfiguration(){

        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.demo.helloFresh"))
                .build().pathMapping("/")
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext())
                );
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .build();
    }


    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Microsite Experience APIs")
                .description("API documentation of microsite experience layer")
                .version(version)
                .contact(new Contact("Dev Team", "", ""))
                .build();
    }
}
