package com.flowiee.pms.order.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.order.entity.OrderReturn;

public interface OrderReturnRepository extends BaseRepository<OrderReturn, Long> {
    OrderReturn findTopByOrderByIdDesc();
}