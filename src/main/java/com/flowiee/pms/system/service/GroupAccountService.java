package com.flowiee.pms.system.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.system.dto.GroupAccountDTO;
import org.springframework.data.domain.Page;

public interface GroupAccountService extends ICurdService<GroupAccountDTO> {
    Page<GroupAccountDTO> find(int pageSize, int pageNum);
}