package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.modules.sales.entity.LoyaltyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    @Query("from LoyaltyProgram where isActive = true")
    List<LoyaltyProgram> findActiveProgram();

    @Query("from LoyaltyProgram where isDefault = true")
    LoyaltyProgram findDefaultProgram();
}