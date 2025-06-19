package com.flowiee.pms.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:template_alert.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties(TemplateSendEmail.class)
public class YamlConfigLoader {

}