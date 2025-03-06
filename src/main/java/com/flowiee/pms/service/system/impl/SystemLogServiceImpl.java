package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.entity.system.SystemLog;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.repository.system.SystemLogRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.system.SystemLogService;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.LogType;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemLogServiceImpl extends BaseService implements SystemLogService {
    SystemLogRepository mvSystemLogRepository;
    UserSession userSession;

    @Override
    public Page<SystemLog> findAll(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<SystemLog> logs = mvSystemLogRepository.findAll(pageable);
        for (SystemLog systemLog : logs.getContent()) {
            systemLog.setAccountName(systemLog.getAccount() != null ? systemLog.getAccount().getFullName() : "");
            systemLog.setContentChange(systemLog.getContentChange() != null ? systemLog.getContentChange() : "");
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
        return mvSystemLogRepository.save(SystemLog.builder()
                .module(module.name())
                .function(function.name())
                .object(object.name())
                .mode(mode.name())
                .title(title)
                .content(lvContent)
                .contentChange(lvContentChange)
                .ip(userSession.getUserPrincipal().getIp())
                .account(userSession.getUserPrincipal().getEntity())
                .build());
    }
}