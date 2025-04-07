package com.flowiee.pms.base;

import com.flowiee.pms.common.enumeration.ConfigCode;
import com.flowiee.pms.entity.system.SystemConfig;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FlwSys {
    private static LocalDateTime                 START_APP_TIME;
    private static Map<ConfigCode, SystemConfig> mvSystemConfigList = new HashMap();

    public static LocalDateTime getStartAppTime() {
        return START_APP_TIME;
    }

    public static Map<ConfigCode, SystemConfig> getSystemConfigs() {
        return mvSystemConfigList;
    }
}