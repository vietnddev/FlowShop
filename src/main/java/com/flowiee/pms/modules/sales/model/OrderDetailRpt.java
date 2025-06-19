package com.flowiee.pms.modules.sales.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailRpt {
    String productName;
    BigDecimal unitPrice;
    int quantity;
    BigDecimal subTotal;
    String note;
}