package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.sales.entity.Order;

import com.flowiee.pms.common.enumeration.OrderStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long> {
    @Query(value = "select sum((select sum(d.price * d.quantity) from order_detail d where d.order_id = o.id) - o.amount_discount) from orders o where function('date', o.order_time) = function('date', current_date)", nativeQuery = true)
    Double findRevenueToday();

//    @Query("select coalesce(sum(o.totalAmountDiscount), 0) from Order o where extract(month from o.thoiGianDatHang) = extract(month from current_date)")
//    Double findRevenueThisMonth();

    //@Query("from Order o where trunc(o.orderTime) = trunc(current_date)")
    @Query("from Order o where function('date', o.orderTime) = function('date', current_date)")//2025-04-28
    List<Order> findOrdersToday();

    @Modifying
    @Query("update Order set paymentTime=:paymentTime, paymentMethod.id=:paymentMethod, paymentAmount=:paymentAmount, paymentNote=:paymentNote, paymentStatus = true where id=:orderId")
    void updatePaymentStatus(@Param("orderId") Long orderId, @Param("paymentTime") LocalDateTime paymentTime, @Param("paymentMethod") Long paymentMethod, @Param("paymentAmount") BigDecimal paymentAmount, @Param("paymentNote") String paymentNote);

//    @Query("select " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 1 then o.totalAmountDiscount else 0 end) as JAN, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 2 then o.totalAmountDiscount else 0 end) as FEB, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 3 then o.totalAmountDiscount else 0 end) as MAR, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 4 then o.totalAmountDiscount else 0 end) as APRIL, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 5 then o.totalAmountDiscount else 0 end) as MAY, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 6 then o.totalAmountDiscount else 0 end) as JUN, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 7 then o.totalAmountDiscount else 0 end) as JUL, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 8 then o.totalAmountDiscount else 0 end) as AUG, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 9 then o.totalAmountDiscount else 0 end) as SEP, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 10 then o.totalAmountDiscount else 0 end) as OCT, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 11 then o.totalAmountDiscount else 0 end) as NOV, " +
//           "sum(case when extract(month from o.thoiGianDatHang) = 12 then o.totalAmountDiscount else 0 end) as DEC " +
//           "from Order o " +
//           "where extract(year from o.thoiGianDatHang) = extract(year from current_date)")
//    List<Object[]> findRevenueEachMonthOfYear();

    @Modifying
    @Query("update Order set ticketExport.id=:ticketExportId where id=:orderId")
    void updateTicketExportInfo(@Param("orderId") Long orderId, @Param("ticketExportId") Long ticketExportId);

    @Query("from Order where ticketExport.id=:ticketExportId")
    Order findByTicketExport(@Param("ticketExportId") Long ticketExportId);

    @Query("from Order where trackingCode = :trackingCode")
    Order findByTrackingCode(@Param("trackingCode") String trackingCode);

    @Query("select " +
           "    extract(year from o.orderTime) AS year, " +
           "    extract(month from o.orderTime) AS month, " +
           "    count(o.id) AS order_count, " +
           "    coalesce(sum(od.price - o.amountDiscount), 0) AS avgValue " +
           "from Order o " +
           "left join OrderDetail od on od.order.id = o.id " +
           "where 1=1 " +
           "    and o.customer.id=:customerId " +
           "    and extract(year from o.orderTime) = :year " +
           "    and (:month is null or (extract(month from o.orderTime) = :month)) " +
           "group by extract(year from o.orderTime), extract(month from o.orderTime) " +
           "order by extract(year from o.orderTime), extract(month from o.orderTime)")
    List<Object[]> findPurchaseHistory(@Param("customerId") Long customerId, @Param("year") Integer year, @Param("month") Integer month);

    @Query("from Order where code = :orderCode")
    Order findByOrderCode(@Param("orderCode") String orderCode);

    @Query("from Order where cancellationReason = :cancellationReason")
    List<Order> findByCancellationReason(Long cancellationReason);

    @Query("from Order where orderStatus in (:orderStatus)")
    List<Order> findByOrderStatus(List<OrderStatus> orderStatus);

    @Query("from Order where customer.id = :customerId")
    List<Order> findByCustomer(@Param("customerId") Long customerId);

    @Query("from Order where deliverySuccessTime between :fromDate and :toDate")
    List<Order> findBySuccessfulDeliveryTime(@Param("fromDate") LocalDateTime pFromDate, @Param("toDate") LocalDateTime pToDate);

    @Query("from Order o where o.salesChannel.id = :salesChannelId")
    List<Order> countBySalesChannel(@Param("salesChannelId") Long salesChannelId);

    @Query("from Order o where o.salesChannel.code = :salesChannelCode")
    List<Order> countBySalesChannel(@Param("salesChannelCode") String salesChannelCode);

    boolean existsByTrackingCode(String trackingCode);
}