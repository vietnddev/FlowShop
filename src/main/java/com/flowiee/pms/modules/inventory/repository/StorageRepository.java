package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.inventory.entity.Storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends BaseRepository<Storage, Long> {
    @Query(value = "select * from vw_storage_items " +
                   "where 1=1 " +
                   "and (:searchText is null or NAME like %:searchText%) " +
                   "and (:storageId is null or STORAGE_ID = :storageId) " +
                   "order by IS_PRODUCT desc", nativeQuery = true)
    Page<Object[]> findAllItems(@Param("searchText") String searchText, @Param("storageId") Long storageId, Pageable pageable);

    Storage findByCode(String code);
}