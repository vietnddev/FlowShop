package com.flowiee.pms.repository.system;

import com.flowiee.pms.entity.system.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("from Branch where branchCode = :branchCode")
    Branch findByCode(@Param("branchCode") String pBranchCode);
}