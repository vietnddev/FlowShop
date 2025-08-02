package com.flowiee.pms.modules.dashboard.service.impl;

import com.flowiee.pms.modules.dashboard.model.DashboardModel;
import com.flowiee.pms.modules.dashboard.service.DashboardService;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.dto.CustomerDTO;
import com.flowiee.pms.modules.inventory.service.ProductStatisticsService_0;
import com.flowiee.pms.modules.sales.service.CustomerService;

import com.flowiee.pms.modules.sales.service.OrderReadService;
import com.flowiee.pms.modules.sales.service.OrderStatisticsService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.CoreUtils;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    OrderReadService mvOrderReadService;
    CustomerService mvCustomerService;
    OrderStatisticsService     mvOrderStatisticsService;
    ProductStatisticsService_0 mvProductStatisticsService;
    EntityManager mvEntityManager;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @SuppressWarnings("unchecked")
    public DashboardModel loadDashboard() {
        logger.info("Start loadDashboard(): " + CommonUtils.now("YYYY/MM/dd HH:mm:ss"));

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonth().getValue();

        //Revenue today
//      String revenueTodaySQL = "SELECT COALESCE(SUM(d.TOTAL_AMOUNT_AFTER_DISCOUNT), 0) FROM PRO_ORDER d WHERE TRUNC(d.THOI_GIAN_DAT_HANG) = TRUNC(SYSDATE)";
//      logger.info("[getRevenueToday() - SQL findData]: " + revenueTodaySQL);
//      Query revenueTodayQuery = entityManager.createNativeQuery(revenueTodaySQL);
//      String revenueToday = CommonUtil.formatToVND(Float.parseFloat(String.valueOf(revenueTodayQuery.getSingleResult())));
//      entityManager.close();

        //Revenue this month
//      String revenueThisMonthSQL = "SELECT COALESCE(SUM(d.TOTAL_AMOUNT_AFTER_DISCOUNT), 0) FROM PRO_ORDER d WHERE EXTRACT(MONTH FROM d.THOI_GIAN_DAT_HANG) = EXTRACT(MONTH FROM SYSDATE)";
//      logger.info("[getRevenueThisMonth() - SQL findData]: " + revenueThisMonthSQL);
//      Query revenueThisMonthSQLQuery = entityManager.createNativeQuery(revenueThisMonthSQL);
//      String revenueThisMonth = CommonUtil.formatToVND(Float.parseFloat(String.valueOf(revenueThisMonthSQLQuery.getSingleResult())));
//      entityManager.close();

        //Customers new
//      String customersNewSQL = "SELECT * FROM PRO_CUSTOMER c WHERE EXTRACT(MONTH FROM c.CREATED_AT) = EXTRACT(MONTH FROM SYSDATE)";
//      logger.info("[getCustomersNew() - SQL findData]: " + customersNewSQL);
//      Query customersNewQuery = entityManager.createNativeQuery(customersNewSQL);
//      List<Customer> customersNew = customersNewQuery.getResultList();
//      entityManager.close();

        //Orders today
//      String ordersTodaySQL = "SELECT * FROM PRO_ORDER D WHERE TRUNC(D.THOI_GIAN_DAT_HANG) = TRUNC(SYSDATE)";
//      logger.info("[getOrdersToday() - SQL findData]: " + ordersTodaySQL);
//      Query ordersTodayQuery = entityManager.createNativeQuery(ordersTodaySQL);
//      List<Order> ordersToday = ordersTodayQuery.getResultList();
//      entityManager.close();

        //Products top sell
        String productsTopSellSQL = "SELECT * FROM " +
                                    "(SELECT s.VARIANT_NAME, COALESCE(SUM(d.QUANTITY), 0) AS Total " +
                                    "FROM PRODUCT_DETAIL s " +
                                    "LEFT JOIN ORDER_DETAIL d ON s.id = d.PRODUCT_VARIANT_ID " +
                                    "GROUP BY s.ID, s.VARIANT_NAME " +
                                    "ORDER BY total DESC) t " +
                                    "LIMIT 10";
        //logger.info("[getProductsTopSell() - SQL findData]: ");
        Query productsTopSellSQLQuery = mvEntityManager.createNativeQuery(productsTopSellSQL.toLowerCase());
        List<Object[]> productsTopSellResultList = productsTopSellSQLQuery.getResultList();
        LinkedHashMap<String, Integer> productsTopSell = new LinkedHashMap<>();
        for (Object[] data : productsTopSellResultList) {
            productsTopSell.put(CoreUtils.trim(data[0]), Integer.parseInt(CoreUtils.trim(data[1])));
        }
        mvEntityManager.close();

        //Revenue month of year
//        String revenueMonthOfYearSQL = "SELECT " +
//                                       "    TO_CHAR(MONTHS.MONTH, 'MM') AS MONTH, " +
//                                       "    COALESCE(SUM(d.PRICE * d.QUANTITY - COALESCE(o.AMOUNT_DISCOUNT, 0)), 0) AS REVENUE " +
//                                       "FROM " +
//                                       "    (SELECT TO_DATE('01-' || LEVEL || '-2024', 'DD-MM-YYYY') AS MONTH " +
//                                       "     FROM DUAL " +
//                                       "     CONNECT BY LEVEL <= 12) MONTHS " +
//                                       "LEFT JOIN " +
//                                       "    ORDERS o ON TO_CHAR(o.ORDER_TIME, 'MM') = TO_CHAR(MONTHS.MONTH, 'MM') " +
//                                       "LEFT JOIN " +
//                                       "    ORDER_DETAIL d ON o.ID = d.ORDER_ID " +
//                                       "WHERE " +
//                                       "    EXTRACT(YEAR FROM MONTHS.MONTH) = ? " +
//                                       "GROUP BY" +
//                                       "    TO_CHAR(MONTHS.MONTH, 'MM') " +
//                                       "ORDER BY " +
//                                       "    TO_CHAR(MONTHS.MONTH, 'MM')";
        String revenueMonthOfYearSQL = "SELECT " +
                "    LPAD(m.month, 2, '0') AS MONTH, " +
                "    COALESCE(SUM(d.PRICE * d.QUANTITY - COALESCE(o.AMOUNT_DISCOUNT, 0)), 0) AS REVENUE " +
                "FROM " +
                "    (SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
                "     UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 " +
                "     UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) m " +
                "LEFT JOIN " +
                "    ORDERS o ON MONTH(o.ORDER_TIME) = m.month AND YEAR(o.ORDER_TIME) = ? " +
                "LEFT JOIN " +
                "    ORDER_DETAIL d ON o.ID = d.ORDER_ID " +
                "GROUP BY " +
                "    m.month " +
                "ORDER BY " +
                "    m.month";
        //logger.info("[getRevenueMonthOfYearSQL() - SQL findData]: ");
        Query revenueMonthOfYearSQLQuery = mvEntityManager.createNativeQuery(revenueMonthOfYearSQL.toLowerCase());
        revenueMonthOfYearSQLQuery.setParameter(1, currentYear);
        List<Object[]> revenueMonthOfYearSQLResultList = revenueMonthOfYearSQLQuery.getResultList();
        LinkedHashMap<Integer, Float> revenueMonthOfYear = new LinkedHashMap<>();
        for (int i = 0; i < revenueMonthOfYearSQLResultList.size(); i++) {
            revenueMonthOfYear.put(Integer.parseInt(CoreUtils.trim(revenueMonthOfYearSQLResultList.get(i)[0])),
                    Float.parseFloat(CoreUtils.trim(revenueMonthOfYearSQLResultList.get(i)[1] != null ? revenueMonthOfYearSQLResultList.get(i)[1] : 0)));
        }
        mvEntityManager.close();

        //Revenue day of month
        LinkedHashMap<String, Float> revenueDayOfMonth = new LinkedHashMap<>();
        revenueDayOfMonth = getRevenueDayOfMonth(currentMonth, currentYear);
//        String revenueDaysOfMonthSQL = "WITH RECURSIVE month_days AS (" +
//                "    SELECT DATE(CONCAT(?, '-', ?, '-01')) AS month_day " +
//                "    UNION ALL " +
//                "    SELECT DATE_ADD(month_day, INTERVAL 1 DAY) " +
//                "    FROM month_days " +
//                "    WHERE DAY(month_day) < DAY(LAST_DAY(month_day)) " +
//                ") " +
//                "SELECT " +
//                "    LPAD(DAY(md.month_day), 2, '0') AS DAY, " +
//                "    COALESCE(SUM(d.PRICE * d.QUANTITY - o.AMOUNT_DISCOUNT), 0) AS REVENUE " +
//                "FROM " +
//                "    month_days md " +
//                "LEFT JOIN " +
//                "    ORDERS o ON DATE(o.ORDER_TIME) = md.month_day " +
//                "LEFT JOIN " +
//                "    ORDER_DETAIL d ON o.ID = d.ORDER_ID " +
//                "GROUP BY " +
//                "    DAY(md.month_day) " +
//                "ORDER BY " +
//                "    DAY(md.month_day)";
//        logger.info("[getRevenueDayOfMonth() - SQL findData]: ");
//        Query revenueDayOfMonthSQLQuery = mvEntityManager.createNativeQuery(revenueDaysOfMonthSQL.toLowerCase());
//        revenueDayOfMonthSQLQuery.setParameter(1, currentMonth);
//        revenueDayOfMonthSQLQuery.setParameter(2, currentYear);
//        List<Object[]> revenueDayOfMonthSQLResultList = revenueDayOfMonthSQLQuery.getResultList();
//        for (int i = 0; i < revenueDayOfMonthSQLResultList.size(); i++) {
//            revenueDayOfMonth.put("Day " + (i + 1), Float.parseFloat(CoreUtils.trim(revenueDayOfMonthSQLResultList.get(i)[1])));
//        }
//        mvEntityManager.close();

        //Revenue by sales channel
        String revenueBySalesChannelSQL = "SELECT " +
                                          "c.NAME, " +
                                          "c.COLOR, " +
                                          "COALESCE(SUM(d.PRICE * d.QUANTITY - o.AMOUNT_DISCOUNT), 0) AS TOTAL " +
                                          "FROM (SELECT * FROM CATEGORY WHERE TYPE = 'SALES_CHANNEL') c " +
                                          "LEFT JOIN ORDERS o ON c.ID = o.CHANNEL " +
                                          "LEFT JOIN ORDER_DETAIL d ON o.ID = d.ORDER_ID " +
                                          "GROUP BY c.NAME, c.COLOR";
        //logger.info("[getRevenueBySalesChannel() - SQL findData]: ");
        Query revenueBySalesChannelQuery = mvEntityManager.createNativeQuery(revenueBySalesChannelSQL.toLowerCase());
        List<Object[]> revenueBySalesChannelResultList = revenueBySalesChannelQuery.getResultList();
        LinkedHashMap<String, Float> revenueSalesChannel = new LinkedHashMap<>();
        for (Object[] data : revenueBySalesChannelResultList) {
            revenueSalesChannel.put(CoreUtils.trim(data[0]), Float.parseFloat(CoreUtils.trim(data[2])));
        }
        mvEntityManager.close();

        //Revenue by products


        String revenueToday = CommonUtils.formatToVND(mvOrderStatisticsService.findRevenueToday());
        String revenueThisMonth = CommonUtils.formatToVND(mvOrderStatisticsService.findRevenueThisMonth());
        List<CustomerDTO> customersNew = mvCustomerService.findCustomerNewInMonth();
        List<Order> ordersToday = mvOrderReadService.findOrdersToday();

        logger.info("Finished loadDashboard(): " + CommonUtils.now("YYYY/MM/dd HH:mm:ss"));

        return DashboardModel.builder()
                .totalProducts(mvProductStatisticsService.countTotalProductsInStorage())
                .revenueToday(revenueToday)
                .revenueThisMonth(revenueThisMonth)
                .ordersNewTodayQty(ordersToday.size())
                .listOrdersToday(ordersToday)
                .customersNewInMonthQty(customersNew.size())
                .listCustomersNewInMonth(customersNew)
                .revenueDayOfMonth(revenueDayOfMonth)
                .revenueMonthOfYear(revenueMonthOfYear)
                .revenueSalesChannel(revenueSalesChannel)
                .productsTopSellQty(productsTopSell)
                .build();
    }

    public LinkedHashMap<String, Float> getRevenueDayOfMonth(int currentMonth, int currentYear) {
        LocalDate firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        int daysInMonth = lastDayOfMonth.getDayOfMonth();

        LinkedHashMap<String, Float> revenueDayOfMonth = new LinkedHashMap<>();
        for (int day = 1; day <= daysInMonth; day++) {
            revenueDayOfMonth.put("Day " + day, 0f);
        }

        String jpql = "SELECT DAY(o.orderTime) as day, " +
                "       COALESCE(SUM(d.price * d.quantity - o.amountDiscount), 0) as revenue " +
                "FROM Order o " +
                "LEFT JOIN o.listOrderDetail d " +
                "WHERE MONTH(o.orderTime) = :month AND YEAR(o.orderTime) = :year " +
                "GROUP BY DAY(o.orderTime) " +
                "ORDER BY DAY(o.orderTime)";

        List<Object[]> results = mvEntityManager.createQuery(jpql, Object[].class)
                .setParameter("month", currentMonth)
                .setParameter("year", currentYear)
                .getResultList();

        for (Object[] result : results) {
            int day = (int) result[0];
            BigDecimal revenue = (BigDecimal) result[1];
            revenueDayOfMonth.put("Day " + day, revenue.floatValue());
        }

        return revenueDayOfMonth;
    }
}