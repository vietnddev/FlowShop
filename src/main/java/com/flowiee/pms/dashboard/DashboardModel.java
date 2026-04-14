package com.flowiee.pms.dashboard;

import com.flowiee.pms.order.dto.OrderDTO;
import com.flowiee.pms.product.dto.ProductVariantDTO;
import com.flowiee.pms.customer.dto.CustomerDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.LinkedHashMap;
import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardModel {
    Integer totalProducts;
    String revenueToday;
    String revenueThisMonth;
    Integer ordersNewTodayQty;
    Integer ordersCancelTodayQty;
    Integer ordersReturnTodayQty;
    List<OrderDTO> listOrdersToday;
    Integer customersNewInMonthQty;
    Integer customersNewInTodayQty;
    List<CustomerDTO> listCustomersNewInMonth;
    LinkedHashMap<String, Float> revenueDayOfMonth;
    LinkedHashMap<Integer, Float> revenueMonthOfYear;
    LinkedHashMap<String, Float> revenueSalesChannel;
    LinkedHashMap<String, Integer> productsTopSellQty;
    LinkedHashMap<String, Integer> productsTopSellRevenue;
    List<ProductVariantDTO> lowStockProducts;
}