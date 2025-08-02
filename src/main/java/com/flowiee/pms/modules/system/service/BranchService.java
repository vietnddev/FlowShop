package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.system.dto.BranchDTO;

import java.util.List;

public interface BranchService extends ICurdService<BranchDTO> {
    List<BranchDTO> find();
}