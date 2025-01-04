package com.flowiee.pms.config;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.common.utils.CoreUtils;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

public class CustomHibernateInterceptor implements StatementInspector {
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public String inspect(String pSql) {
        String customKey = MDC.get("customKey");
        if (CoreUtils.isNullStr(customKey)) {
            //customKey = applicationName;
            customKey = getClassCalling();
        }
        MDC.put("customKey", customKey);

        return pSql;
    }

    private String getClassCalling() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                // Kiểm tra xem class có kế thừa BaseService hay không
                if (BaseService.class.isAssignableFrom(clazz)) {
                    // Kiểm tra đối tượng thực tế thay
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    if (BaseService.class.isInstance(instance)) {
                        return instance.getClass().getSimpleName();
                    }
                }
            } catch (Exception ignored) {}
        }
        return "";
    }
}