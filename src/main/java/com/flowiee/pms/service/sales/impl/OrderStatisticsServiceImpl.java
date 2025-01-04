package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.OrderStatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderStatisticsServiceImpl extends BaseService implements OrderStatisticsService {
    OrderRepository mvOrderRepository;

    @Override
    public Double findRevenueToday() {
        return mvOrderRepository.findRevenueToday();
    }

    @Override
    public Double findRevenueThisMonth() {
        return 0D;//orderRepository.findRevenueThisMonth();
    }
}