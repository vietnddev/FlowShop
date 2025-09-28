package com.flowiee.pms.modules.system.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.system.dto.LanguageDTO;
import com.flowiee.pms.modules.system.service.LanguageService;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.flowiee.pms.modules.system.entity.Language;
import com.flowiee.pms.modules.system.repository.LanguagesRepository;

@Service
public class LanguageServiceImpl extends BaseService<Language, LanguageDTO, LanguagesRepository> implements LanguageService {
	private final ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;

	public LanguageServiceImpl(LanguagesRepository pEntityRepository, ReloadableResourceBundleMessageSource pReloadableResourceBundleMessageSource) {
		super(Language.class, LanguageDTO.class, pEntityRepository);
		this.reloadableResourceBundleMessageSource = pReloadableResourceBundleMessageSource;
	}

	@Override
	public Page<LanguageDTO> findAll(int pageNum, int pageSize, String locale) {
		Pageable pageable = getPageable(pageNum, pageSize);
		Page<Language> languagePage = mvEntityRepository.findAll(locale, pageable);
		return new PageImpl<>(convertDTOs(languagePage.getContent()), pageable, languagePage.getTotalElements());
	}

	@Override
	public Map<String, String> findAllLanguageMessages(String pLocale) {
		List<Language> languageList = mvEntityRepository.findByLocale(pLocale);
        Map<String, String> languageMessages = new HashMap<>();
        for (Language language : languageList) {
            languageMessages.put(language.getMessageKey(), language.getMessageValue());
        }
        return languageMessages;
	}

	@Override
	public LanguageDTO update(LanguageDTO pLanguage, Long pLangId) {
		Language lvLanguage = super.findEntById(pLangId, true);
		lvLanguage.setMessageValue(pLanguage.getMessageValue());
		return convertDTO(mvEntityRepository.save(lvLanguage));
	}

	@Override
	public void reloadMessage(String langCode) {
		try {
			Map<String, String> enMessages = this.findAllLanguageMessages(langCode);
			Properties properties = new Properties();

			String outputFolder = System.getProperty("user.dir") + "/languages";
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			OutputStream outputStream = new FileOutputStream(String.format("%s/messages_%s.properties", outputFolder, langCode));

			for (Map.Entry<String, String> entry : enMessages.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
			properties.store(outputStream, String.format("%s Messages", langCode));

			reloadableResourceBundleMessageSource.clearCache();
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
}