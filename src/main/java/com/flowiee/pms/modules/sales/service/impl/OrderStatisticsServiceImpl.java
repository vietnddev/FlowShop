package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.sales.service.OrderStatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderStatisticsServiceImpl implements OrderStatisticsService {
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