package com.flowiee.pms.system.service;

import com.flowiee.pms.system.entity.SystemLog;
import com.flowiee.pms.shared.util.ChangeLog;
import com.flowiee.pms.shared.enums.ACTION;
import com.flowiee.pms.system.enums.LogType;
import com.flowiee.pms.shared.enums.MasterObject;
import org.springframework.data.domain.Page;

public interface SystemLogService {
    Page<SystemLog> findAll(int pageSize, int pageNum, String pFromDate, String pToDate, Long pActor);

    SystemLog writeLogCreate(ACTION function, MasterObject object, String title, String content);

    SystemLog writeLogUpdate(ACTION function, MasterObject object, String title, ChangeLog changeLog);

    SystemLog writeLogUpdate(ACTION function, MasterObject object, String title, String content);

    SystemLog writeLogUpdate(ACTION function, MasterObject object, String title, String content, String contentChange);

    SystemLog writeLogDelete(ACTION function, MasterObject object, String title, String content);

    SystemLog writeLog(ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange);
}