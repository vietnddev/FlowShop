package com.flowiee.pms.modules.system.service;

import java.util.Map;

import com.flowiee.pms.modules.system.dto.LanguageDTO;
import org.springframework.data.domain.Page;

public interface LanguageService {
	Page<LanguageDTO> findAll(int pageNum, int pageSize, String locale);

	Map<String, String> findAllLanguageMessages(String langCode);

	LanguageDTO update(LanguageDTO pLanguage, Long pLangId);

	void reloadMessage(String langCode);
}