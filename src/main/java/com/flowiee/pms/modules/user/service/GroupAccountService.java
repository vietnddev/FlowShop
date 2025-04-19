package com.flowiee.pms.modules.user.service;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.user.dto.GroupAccountDTO;
import org.springframework.data.domain.Page;

public interface GroupAccountService extends BaseCurdService<GroupAccountDTO> {
    Page<GroupAccountDTO> findAll(int pageSize, int pageNum);
}