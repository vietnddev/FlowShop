package com.flowiee.pms.repository.storage;

import com.flowiee.pms.base.BaseRepository;
import com.flowiee.pms.entity.storage.Storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends BaseRepository<Storage, Long> {
    @Query(value = "select * from VW_STORAGE_ITEMS " +
                   "where 1=1 " +
                   "and (:searchText is null or NAME like %:searchText%) " +
                   "and (:storageId is null or STORAGE_ID = :storageId) " +
                   "order by IS_PRODUCT desc", nativeQuery = true)
    Page<Object[]> findAllItems(@Param("searchText") String searchText, @Param("storageId") Long storageId, Pageable pageable);

    Storage findByCode(String code);
}