package com.flowiee.pms.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//            .addResourceHandler("/uploads/**")
//            .addResourceLocations("file:/" + System.getProperty("user.dir") + "/" + FileUtils.fileUploadPath)
//            .setCachePeriod(3600)
//            .resourceChain(true)
//            .addResolver(new PathResourceResolver());
    }
}