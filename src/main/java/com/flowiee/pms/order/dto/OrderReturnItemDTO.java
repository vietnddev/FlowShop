package com.flowiee.pms.order.dto;

import com.flowiee.pms.shared.base.BaseDTO;
import com.flowiee.pms.order.enums.OrderReturnCondition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class OrderReturnItemDTO extends BaseDTO {
    private Long orderReturnId;
    private Long itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String reason;
    private OrderReturnCondition condition;
}