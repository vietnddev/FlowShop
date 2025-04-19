package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.modules.sales.entity.LoyaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {

}