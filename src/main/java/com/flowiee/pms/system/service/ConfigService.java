package com.flowiee.pms.system.service;

import com.flowiee.pms.shared.base.UpdateService;
import com.flowiee.pms.system.dto.SystemConfigDTO;

import java.util.List;

public interface ConfigService extends UpdateService<SystemConfigDTO> {
    List<SystemConfigDTO> getAll();

    String refreshApp();
}