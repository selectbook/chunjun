package com.iiot.web.swaggerui.annotations;

import com.iiot.web.swaggerui.configuration.SwaggerBootstrapUiConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({SwaggerBootstrapUiConfiguration.class})
public @interface EnableSwaggerBootstrapUI {
}
