package com.flowiee;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
public class FlowieeOfficialApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowieeOfficialApplication.class, args);
        LoggerFactory.getLogger(FlowieeOfficialApplication.class).info("Welcome");
    }

}