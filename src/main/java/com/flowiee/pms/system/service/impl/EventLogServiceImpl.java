package com.flowiee.pms.system.service.impl;

import com.flowiee.pms.shared.security.UserPrincipal;
import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.shared.util.RequestUtils;
import com.flowiee.pms.shared.util.SecurityUtils;
import com.flowiee.pms.system.service.EventLogService;
import com.flowiee.pms.system.entity.EventLog;
import com.flowiee.pms.system.repository.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventLogServiceImpl implements EventLogService {
    private final EventLogRepository eventLogRepository;

    @Override
    public EventLog writeLog(ServletRequestAttributes pServletRequestAttributes, JoinPoint pJoinPoint, LocalDateTime pCreateTime, String pApplication) {
        UserPrincipal currentUser = SecurityUtils.getCurrentUser();
        String lvRequestParam = RequestUtils.getRequestParam(pServletRequestAttributes);
        String lvRequestBody = RequestUtils.getRequestBody(pJoinPoint);
        if (lvRequestBody.length() > 4000) {
            lvRequestBody = lvRequestBody.substring(0, 3996) + " ...";
        }
        String lvHttpMethod = RequestUtils.getHttpMethod(pServletRequestAttributes);
        String lvRequestUrl = RequestUtils.getRequestUrl(pServletRequestAttributes);
        String lvProcessClass = RequestUtils.getProcessClass(pJoinPoint);
        String lvProcessMethod = RequestUtils.getProcessMethod(pJoinPoint);
        String lvUsername = RequestUtils.isLoginPage(pServletRequestAttributes) ? null : currentUser.getUsername();
        String lvIpAddress = RequestUtils.isLoginPage(pServletRequestAttributes) ? null : currentUser.getIp();

        return eventLogRepository.save(EventLog.builder()
                .httpMethod(lvHttpMethod)
                .processClass(lvProcessClass)
                .processMethod(lvProcessMethod)
                .requestUrl(lvRequestUrl)
                .requestParam(lvRequestParam)
                .requestBody(lvRequestBody)
                .createdBy(lvUsername)
                .createdTime(pCreateTime)
                .ipAddress(lvIpAddress)
                .application(CommonUtils.productID)
                .build());
    }

    @Override
    public void updateDuration(Long pRequestID, long pDuration) {
        EventLog eventLog = eventLogRepository.findByRequestId(pRequestID);
        if (eventLog != null) {
            eventLog.setDuration(pDuration);
            eventLogRepository.save(eventLog);
        }
    }
}