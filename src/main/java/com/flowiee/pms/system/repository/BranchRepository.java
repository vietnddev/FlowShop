package com.flowiee.pms.system.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.system.entity.Branch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends BaseRepository<Branch, Long> {
    @Query("from Branch where branchCode = :branchCode")
    Branch findByCode(@Param("branchCode") String pBranchCode);
}