package com.flowiee.pms.order.service;

import java.math.BigDecimal;

public interface OrderPayService {
    String doPay(Long orderId, String paymentTime, Long paymentMethod, BigDecimal paymentAmount, String paymentNote);
}