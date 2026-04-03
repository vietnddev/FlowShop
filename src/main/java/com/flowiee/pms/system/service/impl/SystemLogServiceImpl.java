package com.flowiee.pms.system.service.impl;

import com.flowiee.pms.shared.security.UserPrincipal;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.shared.util.DateTimeUtil;
import com.flowiee.pms.shared.util.SecurityUtils;
import com.flowiee.pms.system.service.SystemLogService;
import com.flowiee.pms.system.entity.Account;
import com.flowiee.pms.system.entity.SystemLog;
import com.flowiee.pms.shared.util.ChangeLog;
import com.flowiee.pms.system.repository.SystemLogRepository;

import com.flowiee.pms.shared.enums.ACTION;
import com.flowiee.pms.system.enums.LogType;
import com.flowiee.pms.shared.enums.MODULE;
import com.flowiee.pms.shared.enums.MasterObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {
    SystemLogRepository mvSystemLogRepository;

    @Override
    public Page<SystemLog> findAll(int pageSize, int pageNum, String pFromDate, String pToDate, Long pActor) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        String lvInputDatePattern = "yyyy-MM-dd";
        LocalDateTime lvFromDate = CoreUtils.isNullStr(pFromDate) ? DateTimeUtil.MIN_TIME :
                DateTimeUtil.parseToLocalDate(pFromDate, lvInputDatePattern).atStartOfDay();
        LocalDateTime lvToDate = CoreUtils.isNullStr(pToDate) ? DateTimeUtil.MAX_TIME :
                DateTimeUtil.parseToLocalDate(pToDate, lvInputDatePattern).atTime(LocalTime.MAX);

        Page<SystemLog> logs = mvSystemLogRepository.findAll(lvFromDate, lvToDate, pActor, pageable);
        for (SystemLog systemLog : logs.getContent()) {
            systemLog.setAccountName(systemLog.getAccount() != null ? systemLog.getAccount().getFullName() : "");
            systemLog.setContentChange(systemLog.getContentChange() != null ? systemLog.getContentChange() : "");
            String lvLocation = systemLog.getLocation();
            systemLog.setLocation(CoreUtils.isNullStr(lvLocation) ? "" : lvLocation);
        }
        return logs;
    }

    @Override
    public SystemLog writeLogCreate(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.I, title, content, "-");
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, ChangeLog changeLog) {
        return this.writeLog(module, function, object, LogType.U, title, changeLog.getOldValues(), changeLog.getNewValues());
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.U, title, content, "-");
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content, String contentChange) {
        return this.writeLog(module, function, object, LogType.U, title, content, contentChange);
    }

    @Override
    public SystemLog writeLogDelete(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.D, title, content, "-");
    }

    @Override
    public SystemLog writeLog(MODULE module, ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange) {
        String lvContent = CoreUtils.isNullStr(content) ? SystemLog.EMPTY : CoreUtils.trim(content);
        String lvContentChange = CoreUtils.isNullStr(contentChange) ? SystemLog.EMPTY : CoreUtils.trim(contentChange);
        if (lvContent.equals(lvContentChange)) {
            lvContent = "Nothing change";
        }
        UserPrincipal currentUser = SecurityUtils.getCurrentUser();
        return mvSystemLogRepository.save(SystemLog.builder()
                .module(module.name())
                .function(function.name())
                .object(object.name())
                .mode(mode.name())
                .title(title)
                .content(lvContent)
                .contentChange(lvContentChange)
                .ip(currentUser.getIp())
                .location(currentUser.getLocation())
                .account(new Account(currentUser.getId()))
                .build());
    }
}