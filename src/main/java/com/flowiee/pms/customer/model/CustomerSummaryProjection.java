package com.flowiee.pms.customer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CustomerSummaryProjection {
    Long getCustomerId();
    String getCustomerName();
    String getPhoneDefault();
    String getEmailDefault();
    String getAddressDefault();
    Integer getTotalOrders();
    Integer getCancelledOrders();
    Integer getReturnedOrders();
    BigDecimal getTotalSpent();
    BigDecimal getAverageOrderValue();
    BigDecimal getOutstandingDebt();
    LocalDateTime getFirstOrderDate();
    LocalDateTime getLastOrderDate();
    String getCustomerTier();
}