package com.iiot.web.swaggerui.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = {"com.iiot.web.swaggerui.web"}
)
public class SwaggerBootstrapUiConfiguration {

}