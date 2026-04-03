package com.flowiee.pms.promotion.service;

import com.flowiee.pms.promotion.dto.LoyaltyProgramDTO;
import com.flowiee.pms.promotion.entity.LoyaltyProgram;
import com.flowiee.pms.promotion.entity.LoyaltyTransaction;
import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.shared.base.ICurdService;

import java.util.List;

public interface LoyaltyProgramService extends ICurdService<LoyaltyProgramDTO> {
    List<LoyaltyProgram> find();

    List<LoyaltyProgram> getActivePrograms();

    LoyaltyProgram getDefaultProgram();

    LoyaltyProgram enableProgram(Long programId);

    LoyaltyProgram disableProgram(Long programId);

    LoyaltyTransaction accumulatePoints(Order order, Long programId); // Tích điểm

    void redeemPoints(Long customerId, int pointsToRedeem); // Đổi điểm

    LoyaltyTransaction revokePoints(Order order);
}