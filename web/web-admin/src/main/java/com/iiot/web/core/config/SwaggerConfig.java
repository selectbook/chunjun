package com.iiot.web.core.config;

import com.iiot.web.swaggerui.annotations.EnableSwaggerBootstrapUI;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger配置
 * Parameter Types
 * OpenAPI 3.0 distinguishes between the following parameter types based on the parameter location. The location is determined by the parameter’s in key, for example, in: query or in: path.
 * <p>
 * path parameters, such as /users/{id}
 * query parameters, such as /users?role=admin
 * header parameters, such as X-MyHeader: Value
 * cookie parameters, which are passed in the Cookie header, such as Cookie: debug=0; csrftoken=BUSe35dohU3O1MZvDCU
 * <p>
 * Data Type
 * allowMultiple=true,  ————表示是数组格式的参数
 * "int",
 * "double",
 * "float",
 * "long",
 * "biginteger",
 * "bigdecimal",
 * "byte",
 * "boolean",
 * "string",
 * "object",
 * "__file"
 * <p>
 * 注意：
 * int、double、float、long、biginteger、bigdecimal、byte、需要给example的值
 */

/**
 * Swagger2的接口配置
 *
 * @author ruoyi
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

    /**
     * 创建API
     */
    @Bean
    public Docket createRestApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name("token").description("用户token").modelRef(new ModelRef("string"))
                .parameterType("header").required(false).build();
        List<Parameter> pars = new ArrayList<Parameter>();
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(security())
                .globalOperationParameters(pars);
    }

    /**
     * 摘要信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("预约系统文档")
                .description("预约系统接口文档")
                .version("1.0.0")
                .contact(new Contact("desom", "", "desom@ntsitech.com"))
                .build();
    }

    private List<ApiKey> security() {
        List<ApiKey> list = new ArrayList<>();
        list.add(new ApiKey("token", "token", "header"));
        return list;
    }
}
