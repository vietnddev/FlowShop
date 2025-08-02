package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.modules.system.dto.SystemConfigDTO;

import java.util.List;
import java.util.Optional;

public interface ConfigService {
    List<SystemConfigDTO> find();

    Optional<SystemConfig> findById(Long configId);

    SystemConfigDTO update(SystemConfigDTO systemConfig, Long configId);

    String refreshApp();

    List<SystemConfig> getSystemConfigs(String[] configCodes);

    SystemConfig getSystemConfig(String configCode);

    List<SystemConfig> getSystemConfigs(List<String> configCodes);
}