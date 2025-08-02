package com.flowiee.pms.common.utils;

import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.dto.OrderDetailDTO;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

public class OrderUtils {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    private static final BigDecimal INITIAL_ORDER_VALUE = BigDecimal.ZERO;

    public static BigDecimal calTotalAmount(List<OrderDetailDTO> orderDetails, BigDecimal amountDiscount) {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return ZERO;
        }
        BigDecimal totalAmount = ZERO;
        for (OrderDetailDTO d : orderDetails) {
            totalAmount = totalAmount.add((d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()))).subtract(d.getExtraDiscount()));
        }
        return totalAmount.subtract(amountDiscount);
    }

    public static BigDecimal calAmount(List<OrderDetail> pItems, BigDecimal pCouponDiscount) {
        if (pItems == null || pItems.isEmpty()) {
            return ZERO;
        }

        BigDecimal totalAmount = pItems.stream()
                .map(d -> d.getPrice()
                        .multiply(BigDecimal.valueOf(d.getQuantity()))
                        .subtract(d.getExtraDiscount()))
                .reduce(INITIAL_ORDER_VALUE, BigDecimal::add);

        return pCouponDiscount == null ? totalAmount : totalAmount.subtract(pCouponDiscount);
    }

    public static BigDecimal calAmount(List<Order> pOrders) {
        if (pOrders == null || CollectionUtils.isEmpty(pOrders)) {
            return ZERO;
        }

        return pOrders.stream()
                .map(OrderUtils::calAmount)
                .reduce(INITIAL_ORDER_VALUE, BigDecimal::add);
    }

    public static BigDecimal calAmount(Order pOrder) {
        if (pOrder == null || CollectionUtils.isEmpty(pOrder.getListOrderDetail())) {
            return ZERO;
        }

        BigDecimal totalAmount = pOrder.getListOrderDetail().stream()
                .map(d -> d.getPrice()
                        .multiply(BigDecimal.valueOf(d.getQuantity()))
                        .subtract(d.getExtraDiscount()))
                .reduce(INITIAL_ORDER_VALUE, BigDecimal::add);

        return pOrder.getAmountDiscount() == null ? totalAmount : totalAmount.subtract(pOrder.getAmountDiscount());
    }

    public static int countItems(List<Order> pOrders) {
        if (pOrders == null || CollectionUtils.isEmpty(pOrders)) {
            return 0;
        }

        return pOrders.stream()
                .map(OrderUtils::countItems)
                .reduce(0, Integer::sum);
    }

    public static int countItems(Order pOrder) {
        if (pOrder == null || CollectionUtils.isEmpty(pOrder.getListOrderDetail())) {
            return 0;
        }

        return pOrder.getListOrderDetail().stream()
                .map(OrderDetail::getQuantity)
                .reduce(0, Integer::sum);
    }

    public static int countItemsEachOrder(List<OrderDetailDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        int totalItems = 0;
        for (OrderDetailDTO d : orderItems) {
            totalItems += d.getQuantity();
        }
        return totalItems;
    }

    public static int countItemsListOrder_(List<Order> pOrders) {
        if (pOrders == null || pOrders.isEmpty()) {
            return 0;
        }
        int totalItems = 0;
        for (Order lvOrder : pOrders) {
            totalItems += countItemsEachOrder_(lvOrder.getListOrderDetail());
        }
        return totalItems;
    }

    public static int countItemsEachOrder_(List<OrderDetail> orderItems) {
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