package com.flowiee.pms.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@ConfigurationProperties(prefix = "general-email-notification")
public class TemplateSendEmail {
    private List<Template> templates;

    @Getter
    @Setter
    public static class Template {
        private String type;
        private String path;
        private String encoding;
        private String subject;
        private String sender;
        private String bcc;
        private String cc;
        private String templateContent;
        private Map<String, byte[]> attachments = new HashMap<>();
    }

    public List<Template> getGeneralMailTemplates() {
        return templates;
    }
}