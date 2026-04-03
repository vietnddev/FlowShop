package com.flowiee.pms.system.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.system.entity.CategoryHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryHistoryRepository extends BaseRepository<CategoryHistory, Long> {
    @Modifying
    @Query("delete from CategoryHistory where category.id=:categoryId")
    void deleteAllByCategory(@Param("categoryId") Long categoryId);
}