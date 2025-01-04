package com.flowiee.pms.service.system;

import com.flowiee.pms.base.service.BaseCurdService;
import com.flowiee.pms.entity.system.GroupAccount;
import org.springframework.data.domain.Page;

public interface GroupAccountService extends BaseCurdService<GroupAccount> {
    Page<GroupAccount> findAll(int pageSize, int pageNum);
}