package com.flowiee.pms.base;

import com.flowiee.pms.common.utils.RequestUtils;
import com.flowiee.pms.entity.system.EventLog;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.system.EventLogService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class RestControllerAspect {
    private final Logger mvLogger = LoggerFactory.getLogger(getClass());
    private final EventLogService mvEventLogService;
    private final UserSession mvUserSession;
    private ThreadLocal<RequestContext> mvRequestContext = ThreadLocal.withInitial(RequestContext::new); // Tạo ThreadLocal để lưu thông tin của request

    @Getter
    @Setter
    public static class RequestContext {
        private long requestId;
        private long startTime;
        private String username;
        private String ip;
    }

    public RequestContext getRequestContext() {
        return mvRequestContext.get();
    }

    @Before("execution(* com.flowiee.pms.controller.*.*.*(..))")
    public void beforeCall(JoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();
        mvLogger.info("AOP Before call system controller {} with arguments: {}", joinPoint, Arrays.toString(args));

        //Save request info into db
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        LocalDateTime lvRequestTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());

        String lvProcessMethod = RequestUtils.getProcessMethod(joinPoint);

        long lvRequestId = -1l;
        String lvUsername = RequestUtils.isLoginPage(attributes) ? null : mvUserSession.getUserPrincipal().getUsername();
        String lvIpAddress = RequestUtils.isLoginPage(attributes) ? null : mvUserSession.getUserPrincipal().getIp();

        if ("handleFileRequest".equals(lvProcessMethod)) {
            String lvLogMsg = String.format("[%s] %s", RequestUtils.getHttpMethod(attributes), RequestUtils.getRequestUrl(attributes));
            mvLogger.info(lvLogMsg);
            System.out.println(lvLogMsg);
        } else {
            EventLog eventLog = mvEventLogService.writeLog(attributes, joinPoint, lvRequestTime, CommonUtils.productID);
            lvRequestId = eventLog.getRequestId();
            lvUsername = eventLog.getCreatedBy();
            lvIpAddress = eventLog.getIpAddress();
        }

        RequestContext lvRequestContext = mvRequestContext.get();
        lvRequestContext.setRequestId(lvRequestId);
        lvRequestContext.setStartTime(startTime);
        lvRequestContext.setUsername(lvUsername);
        lvRequestContext.setIp(lvIpAddress);
        mvRequestContext.set(lvRequestContext);
    }

    @After("execution(* com.flowiee.pms.controller.*.*.*(..))")
    public void afterCall(JoinPoint joinPoint) {
        RequestContext lvRequestContext = mvRequestContext.get();
        long duration = System.currentTimeMillis() - lvRequestContext.getStartTime();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        mvEventLogService.updateDuration(lvRequestContext.requestId, duration);
        mvRequestContext.remove();
    }
}