package com.flowiee.pms.system.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.system.dto.BranchDTO;

import java.util.List;

public interface BranchService extends ICurdService<BranchDTO> {
    List<BranchDTO> find();
}