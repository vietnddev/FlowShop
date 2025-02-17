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
            customKey = applicationName;
        }

        // 🔍 Duyệt qua stack trace để tìm class kế thừa từ ScheduleExecutor
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                if (BaseService.class.isAssignableFrom(clazz)) {
                    // 🛠 Nếu class gọi là instance của ScheduleExecutor, lấy scheduleName
                    String lvClassName = clazz.getSimpleName();
                    if (!CoreUtils.isNullStr(lvClassName)) {
                        customKey = lvClassName;
                    }
                    break; // Chỉ cần lấy từ class đầu tiên tìm thấy
                }
            } catch (Exception e) {}
        }

        MDC.put("customKey", customKey);

        return pSql;
    }
}