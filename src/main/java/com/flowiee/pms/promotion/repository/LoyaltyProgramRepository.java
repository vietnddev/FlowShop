package com.flowiee.pms.promotion.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.promotion.entity.LoyaltyProgram;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyProgramRepository extends BaseRepository<LoyaltyProgram, Long> {
    @Query("from LoyaltyProgram where isActive = true")
    List<LoyaltyProgram> findActiveProgram();

    @Query("from LoyaltyProgram where isDefault = true")
    LoyaltyProgram findDefaultProgram();
}