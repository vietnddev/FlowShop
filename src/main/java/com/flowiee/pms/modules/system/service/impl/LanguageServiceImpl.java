package com.flowiee.pms.modules.system.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.system.dto.LanguageDTO;
import com.flowiee.pms.modules.system.service.LanguageService;
import org.springframework.stereotype.Service;

import com.flowiee.pms.modules.system.entity.Language;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.system.repository.LanguagesRepository;

@Service
public class LanguageServiceImpl extends BaseService<Language, LanguageDTO, LanguagesRepository> implements LanguageService {
	public LanguageServiceImpl(LanguagesRepository pEntityRepository) {
		super(Language.class, LanguageDTO.class, pEntityRepository);
	}

	@Override
	public List<LanguageDTO>find(BaseParameter pParam) {
		return super.find(pParam);
	}

	@Override
	public LanguageDTO findById(Long langId, boolean pThrowException) {
		return super.findDtoById(langId, pThrowException);
	}

	@Override
	public LanguageDTO save(LanguageDTO entity) {
		throw new AppException("System does not support this function!");
	}

	@Override
	public Map<String, String> findAllLanguageMessages(String langCode) {
		List<Language> languageList = mvEntityRepository.findByCode(langCode);
        Map<String, String> languageMessages = new HashMap<>();
        for (Language language : languageList) {
            languageMessages.put(language.getKey(), language.getValue());
        }
        return languageMessages;
	}

	@Override
	public LanguageDTO update(LanguageDTO pLanguage, Long pLangId) {
		if (pLangId == null || pLangId <= 0) {
			throw new BadRequestException();
		}
		return super.update(pLanguage, pLangId);
	}

	@Override
	public String delete(Long entityId) {
		throw new AppException("System does not support this function!");
	}

	@Override
	public void reloadMessage(String langCode) {
		try {
			Map<String, String> enMessages = this.findAllLanguageMessages(langCode);
			Properties properties = new Properties();
			//Begin hot fix (temp)
			String outputFolder = System.getProperty("user.dir") + "/language";
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			//OutputStream outputStream = new FileOutputStream(String.format("src/main/resources/language/messages_%s.properties", langCode));
			OutputStream outputStream = new FileOutputStream(String.format("%s/messages_%s.properties", outputFolder, langCode));
			//End hot fix
			for (Map.Entry<String, String> entry : enMessages.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
			properties.store(outputStream, String.format("%s Messages", langCode));
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
}