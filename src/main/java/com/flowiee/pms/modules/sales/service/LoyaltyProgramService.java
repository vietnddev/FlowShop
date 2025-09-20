package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.sales.dto.LoyaltyProgramDTO;
import com.flowiee.pms.modules.sales.entity.LoyaltyProgram;
import com.flowiee.pms.modules.sales.entity.LoyaltyTransaction;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.base.service.ICurdService;

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