package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.entity.LoyaltyProgram;
import com.flowiee.pms.modules.sales.entity.LoyaltyRule;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.base.service.ICurdService;

import java.util.List;

public interface LoyaltyProgramService extends ICurdService<LoyaltyProgram> {
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