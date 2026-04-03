package com.flowiee.pms.shared.base;

import com.flowiee.pms.shared.config.TemplateSendEmail;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.system.enums.NotificationType;
import com.flowiee.pms.system.entity.SystemConfig;

import java.util.HashMap;
import java.util.Map;

public class FlwSys {
    private static Map<NotificationType, TemplateSendEmail.Template> mvGeneralEmailTemplateMap = new HashMap<>();
    private static Map<ConfigCode, SystemConfig> mvSystemConfigList = new HashMap();

    public static Map<ConfigCode, SystemConfig> getSystemConfigs() {
        return mvSystemConfigList;
    }

    public static Map<NotificationType, TemplateSendEmail.Template> getEmailTemplateConfigs() {
        return mvGeneralEmailTemplateMap;
    }
}