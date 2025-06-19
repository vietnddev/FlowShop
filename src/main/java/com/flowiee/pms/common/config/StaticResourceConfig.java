package com.flowiee.pms.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.concurrent.TimeUnit;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                //.addResourceHandler("/uploads/**")
                //.addResourceLocations("file:/" + System.getProperty("user.dir") + "/" + FileUtils.fileUploadPath)
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(2592000)//30 days
                .resourceChain(true)
                .addResolver(new PathResourceResolver())
                .addResolver(new VersionResourceResolver()
                        .addContentVersionStrategy("/**")
                );
    }
}