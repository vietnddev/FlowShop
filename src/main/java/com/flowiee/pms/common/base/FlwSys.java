package com.flowiee.pms.common.base;

import com.flowiee.pms.common.config.TemplateSendEmail;
import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.common.enumeration.NotificationType;
import com.flowiee.pms.modules.system.entity.SystemConfig;

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