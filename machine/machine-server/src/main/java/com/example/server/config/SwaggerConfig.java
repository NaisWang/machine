package com.example.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : whz
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.example.server.controller"))
				.paths(PathSelectors.any())
				.build()

				//全站统一header设置
				.securityContexts(securityContexts())
				.securitySchemes(securitySchemes());

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("机器管理系统接口文档")
				.description("机器管理系统接口文档")
				.contact(new Contact("xxxx", "http://localhost:8080/doc.html", "xxx@xxx.com"))
				.version("1.0")
				.build();
	}

	/**
	 * 下面四个方法都是用于全站统一header设置
	 */
	private List<ApiKey> securitySchemes() {
		//设置请求头信息
		List<ApiKey> result = new ArrayList<>();
		ApiKey apiKey = new ApiKey("Authorization", "Authorization", "Header");
		result.add(apiKey);
		return result;
	}

	private List<SecurityContext> securityContexts() {
		//设置需要登陆认证的路径
		List<SecurityContext> result = new ArrayList<>();
		result.add(getContextByPath("/hello/.*"));
		return result;
	}

	//注：此处的SecurityContext是springfox.documentation.spi.service.contexts包下的
	private SecurityContext getContextByPath(String pathRegex) {
		return SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(pathRegex))
				.build();
	}

	private List<SecurityReference> defaultAuth() {
		List<SecurityReference> result = new ArrayList<>();
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		result.add(new SecurityReference("Authorization", authorizationScopes));
		return result;
	}
}
