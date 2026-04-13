package ceos.diggindie.common.config.web;

import ceos.diggindie.common.annotation.ApiVersion;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

        // version 1
        configurer.addPathPrefix("/v1",
                c -> c.isAnnotationPresent(ApiVersion.class) &&
                        c.getAnnotation(ApiVersion.class).value().equals("v1"));

        // version 2
        configurer.addPathPrefix("/v2",
                c -> c.isAnnotationPresent(ApiVersion.class) &&
                        c.getAnnotation(ApiVersion.class).value().equals("v2"));
    }
}
