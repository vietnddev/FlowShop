package com.flowiee.pms.modules.system.service;

import java.util.Map;

import com.flowiee.pms.common.base.service.BaseCurdService;
import com.flowiee.pms.modules.system.dto.LanguageDTO;

public interface LanguageService extends BaseCurdService<LanguageDTO> {
	Map<String, String> findAllLanguageMessages(String langCode);

	void reloadMessage(String langCode);
}