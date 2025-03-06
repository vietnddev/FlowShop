package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.RequestUtils;
import com.flowiee.pms.entity.system.EventLog;
import com.flowiee.pms.repository.system.EventLogRepository;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.system.EventLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventLogServiceImpl extends BaseService implements EventLogService {
    private final EventLogRepository eventLogRepository;
    private final UserSession userSession;

    @Override
    public EventLog writeLog(ServletRequestAttributes pServletRequestAttributes, JoinPoint pJoinPoint, LocalDateTime pCreateTime, String pApplication) {
        String lvRequestParam = RequestUtils.getRequestParam(pServletRequestAttributes);
        String lvRequestBody = RequestUtils.getRequestBody(pJoinPoint);
        if (lvRequestBody.length() > 4000) {
            lvRequestBody = lvRequestBody.substring(0, 3996) + " ...";
        }
        String lvHttpMethod = RequestUtils.getHttpMethod(pServletRequestAttributes);
        String lvRequestUrl = RequestUtils.getRequestUrl(pServletRequestAttributes);
        String lvProcessClass = RequestUtils.getProcessClass(pJoinPoint);
        String lvProcessMethod = RequestUtils.getProcessMethod(pJoinPoint);
        String lvUsername = RequestUtils.isLoginPage(pServletRequestAttributes) ? null : userSession.getUserPrincipal().getUsername();
        String lvIpAddress = RequestUtils.isLoginPage(pServletRequestAttributes) ? null : userSession.getUserPrincipal().getIp();

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