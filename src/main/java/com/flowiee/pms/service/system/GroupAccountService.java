package com.flowiee.pms.service.system;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.dto.GroupAccountDTO;
import org.springframework.data.domain.Page;

public interface GroupAccountService extends BaseCurdService<GroupAccountDTO> {
    Page<GroupAccountDTO> findAll(int pageSize, int pageNum);
}