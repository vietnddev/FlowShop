package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.sales.LoyaltyProgram;
import com.flowiee.pms.entity.sales.LoyaltyRule;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.base.BaseCurdService;

import java.util.List;

public interface LoyaltyProgramService extends BaseCurdService<LoyaltyProgram> {
    LoyaltyRule findRuleById(Long ruleId, boolean throwException);

    List<LoyaltyProgram> getActivePrograms();

    LoyaltyProgram getDefaultProgram();

    LoyaltyProgram enableProgram(Long programId);

    LoyaltyProgram disableProgram(Long programId);

    void accumulatePoints(Order order, Long programId); // Tích điểm

    void redeemPoints(Long customerId, int pointsToRedeem); // Đổi điểm

    void addRule(LoyaltyRule loyaltyRule, Long programId);

    void updateRule(LoyaltyRule loyaltyRule, Long ruleId, Long programId);

    void removeRule(Long ruleId);
}