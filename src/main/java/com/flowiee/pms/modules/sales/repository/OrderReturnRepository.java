package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.sales.entity.OrderReturn;

public interface OrderReturnRepository extends BaseRepository<OrderReturn, Long> {
    OrderReturn findTopByOrderByIdDesc();
}