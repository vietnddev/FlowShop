package com.flowiee.pms.modules.staff.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.staff.dto.GroupAccountDTO;
import org.springframework.data.domain.Page;

public interface GroupAccountService extends ICurdService<GroupAccountDTO> {
    Page<GroupAccountDTO> find(int pageSize, int pageNum);
}