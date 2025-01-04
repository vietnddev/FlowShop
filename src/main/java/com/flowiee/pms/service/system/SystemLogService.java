package com.flowiee.pms.service.system;

import com.flowiee.pms.entity.system.SystemLog;
import com.flowiee.pms.common.ChangeLog;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.LogType;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import org.springframework.data.domain.Page;

public interface SystemLogService {
    Page<SystemLog> findAll(int pageSize, int pageNum);

    SystemLog writeLogCreate(MODULE module, ACTION function, MasterObject object, String title, String content);

    SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, ChangeLog changeLog);

    SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content);

    SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content, String contentChange);

    SystemLog writeLogDelete(MODULE module, ACTION function, MasterObject object, String title, String content);

    SystemLog writeLog(MODULE module, ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange);
}