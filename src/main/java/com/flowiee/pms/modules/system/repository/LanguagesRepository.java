package com.flowiee.pms.modules.system.repository;

import java.util.List;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.system.entity.Language;

@Repository
public interface LanguagesRepository extends BaseRepository<Language, Long> {
	List<Language> findByCode(String code);	
}