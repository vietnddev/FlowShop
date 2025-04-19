package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.common.utils.RequestUtils;
import com.flowiee.pms.modules.system.entity.EventLog;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.security.UserSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final Logger mvLogger = LoggerFactory.getLogger(getClass());
    private final EventLogService mvEventLogService;
    private final UserSession mvUserSession;

    private ThreadLocal<RequestContext> mvRequestContext = ThreadLocal.withInitial(RequestContext::new); // Tạo ThreadLocal để lưu thông tin của request
    private int mvResponseLogLength = 2000;

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

    @Around("execution(* com.flowiee.pms.modules..controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String lvProcessMethod = RequestUtils.getProcessMethod(joinPoint);
        String lvUri = attributes.getRequest().getRequestURI();
        if (!lvUri.contains("/api/v1") || "handleFileRequest".equals(lvProcessMethod)) {
            return joinPoint.proceed();
        }
        LocalDateTime lvRequestTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());

        EventLog eventLog = mvEventLogService.writeLog(attributes, joinPoint, lvRequestTime, CommonUtils.productID);
        long lvRequestId = eventLog.getRequestId();
        String lvUsername = eventLog.getCreatedBy();
        String lvIpAddress = eventLog.getIpAddress();

        RequestContext lvRequestContext = mvRequestContext.get();
        lvRequestContext.setRequestId(lvRequestId);
        lvRequestContext.setStartTime(startTime);
        lvRequestContext.setUsername(lvUsername);
        lvRequestContext.setIp(lvIpAddress);
        mvRequestContext.set(lvRequestContext);

        mvLogger.info("[REQUEST {}] {}",
                lvRequestContext.getRequestId(),
                formatRequestInfo(attributes.getRequest(), joinPoint, attributes));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            mvLogger.error("[EXCEPTION {}] {}",
                    lvRequestContext.getRequestId(),
                    formatRequestException(attributes.getRequest(), joinPoint, ex.getMessage()),
                    ex);
            throw ex;
        }

        long duration = System.currentTimeMillis() - startTime;

        mvEventLogService.updateDuration(lvRequestContext.requestId, duration);
        mvRequestContext.remove();

        mvLogger.info("[RESPONSE {}] ({} ms) {}",
                lvRequestContext.getRequestId(),
                duration,
                formatResponse(result));

        return result;
    }

    public String formatRequestException(HttpServletRequest pRequest, JoinPoint pJoinPoint, String pMessage) {
        String lvHttpMethod = pRequest != null ? pRequest.getMethod() : "N/A";
        String lvUri = pRequest != null ? pRequest.getRequestURI() : "N/A";
        String lvClassName = pJoinPoint.getTarget().getClass().getSimpleName();
        String lvProcessMethod = RequestUtils.getProcessMethod(pJoinPoint);

        return String.format("%s %s - %s.%s with error: %s", lvHttpMethod, lvUri, lvClassName, lvProcessMethod, pMessage);
    }

    public String formatRequestInfo(HttpServletRequest pRequest, JoinPoint pJoinPoint, ServletRequestAttributes pAttributes) {
        String lvHttpMethod = pRequest != null ? pRequest.getMethod() : "N/A";
        String lvUri = pRequest != null ? pRequest.getRequestURI() : "N/A";
        String lvClassName = pJoinPoint.getTarget().getClass().getSimpleName();
        String lvProcessMethod = RequestUtils.getProcessMethod(pJoinPoint);
        String lvRequestParam = RequestUtils.getRequestParam(pAttributes);
        String lvRequestBody = RequestUtils.getRequestBody(pJoinPoint);

        return String.format("%s %s - %s.%s with params: [%s] & body: [%s]", lvHttpMethod, lvUri, lvClassName, lvProcessMethod, lvRequestParam, lvRequestBody);
    }

    public String formatResponse(Object pResult) {
        if (pResult == null) return "null";
        String output = pResult.toString();
        return output.length() > mvResponseLogLength ? output.substring(0, mvResponseLogLength) + "..." : output;
    }
}