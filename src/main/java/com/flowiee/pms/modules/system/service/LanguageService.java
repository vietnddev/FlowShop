package com.flowiee.pms.modules.system.service;

import java.util.Map;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.system.dto.LanguageDTO;

public interface LanguageService extends ICurdService<LanguageDTO> {
	Map<String, String> findAllLanguageMessages(String langCode);

	void reloadMessage(String langCode);
}