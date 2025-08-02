package com.flowiee.pms.common.config;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MDCInitializer {
    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void init() {
        if (MDC.get("customKey") == null) {
            MDC.put("customKey", applicationName);
        }
    }
}