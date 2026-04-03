package com.flowiee.pms.system.repository;

import java.util.List;

import com.flowiee.pms.shared.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.system.entity.Language;

@Repository
public interface LanguagesRepository extends BaseRepository<Language, Long> {
	@Query("from Language l where (:locale is null or l.locale = :locale)")
	Page<Language> findAll(@Param("locale") String locale, Pageable pageable);

	List<Language> findByLocale(String code);
}