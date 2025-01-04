package com.flowiee.pms.common.utils;

import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.OrderDetail;

import java.math.BigDecimal;
import java.util.List;

public class OrderUtils {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    public static BigDecimal calTotalAmount(List<Order> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return ZERO;
        }
        return orderList.stream()
                .map(order -> calTotalAmount(order.getListOrderDetail(), order.getAmountDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calTotalAmount(List<OrderDetail> orderDetails, BigDecimal amountDiscount) {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return ZERO;
        }
        BigDecimal totalAmount = ZERO;
        for (OrderDetail d : orderDetails) {
            totalAmount = totalAmount.add((d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()))).subtract(d.getExtraDiscount()));
        }
        return totalAmount.subtract(amountDiscount);
    }

    public static int countItemsListOrder(List<Order> pOrders) {
        if (pOrders == null || pOrders.isEmpty()) {
            return 0;
        }
        int totalItems = 0;
        for (Order lvOrder : pOrders) {
            totalItems += countItemsEachOrder(lvOrder.getListOrderDetail());
        }
        return totalItems;
    }

    public static int countItemsEachOrder(List<OrderDetail> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        int totalItems = 0;
        for (OrderDetail d : orderItems) {
            totalItems += d.getQuantity();
        }
        return totalItems;
    }

    public static int calBonusPoints(BigDecimal totalAmount) {
        BigDecimal bonusPoints = totalAmount.divide(ONE_HUNDRED).setScale(0, BigDecimal.ROUND_DOWN);
        return bonusPoints.intValue();
    }
}