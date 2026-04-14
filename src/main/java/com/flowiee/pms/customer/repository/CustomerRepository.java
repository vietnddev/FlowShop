package com.flowiee.pms.customer.repository;

import com.flowiee.pms.customer.model.CustomerSummaryProjection;
import com.flowiee.pms.shared.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.customer.entity.Customer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
    @Query("select c.id as customerId, " +
           "    c.customerName as customerName, " +
           "    ccp.value as phoneDefault, " +
           "    cce.value as emailDefault, " +
           "    cca.value as addressDefault, " +
           "    coalesce(count(distinct o.id), 0) as totalOrders, " +
           "    sum(case when o.orderStatus = 'CANCELLED' then 1 else 0 end) as cancelledOrders, " +
           "    sum(case when o.orderStatus = 'RETURNED' then 1 else 0 end) as returnedOrders, " +
           "    coalesce(sum(od.price * od.quantity), 0) as totalSpent, " +
           "    coalesce(avg(od.price * od.quantity), 0) as averageOrderValue, " +
           "    coalesce(sum(case when o.paymentStatus = false then (od.price * od.quantity) else 0 end), 0) as outstandingDebt, " +
           "    min(o.orderTime) as firstOrderDate, " +
           "    max(o.orderTime) as lastOrderDate, " +
           "    case " +
           "        when coalesce(sum(od.price * od.quantity), 0) >= 10000000 then 'VIP' " +
           "        when coalesce(sum(od.price * od.quantity), 0) >= 5000000 then 'GOLD' " +
           "        when coalesce(sum(od.price * od.quantity), 0) >= 1000000 then 'SILVER' " +
           "        else 'BRONZE' " +
           "    end as customerTier " +
           "from Customer c " +
           "left join c.listOrder o " +
           "left join o.listOrderDetail od " +
           "left join c.listCustomerContact ccp on ccp.code = 'P' and ccp.isDefault = 'Y' and ccp.status = true " +
           "left join c.listCustomerContact cce on cce.code = 'E' and cce.isDefault = 'Y' and cce.status = true " +
           "left join c.listCustomerContact cca on cca.code = 'A' and cca.isDefault = 'Y' and cca.status = true " +
           "where (coalesce(:customerIds) is null or c.id in (:customerIds)) " +
           "    and ((:name is null or :name = '') or c.customerName like concat('%', :name, '%')) " +
           "    and (:sex is null or c.gender = :sex) " +
           "    and (:birthday is null or c.dateOfBirth = :birthday) " +
           "    and ((:phone is null or :phone = '') or ccp.value = :phone) " +
           "    and ((:email is null or :email = '') or cce.value = :email) " +
           "    and ((:address is null or :address = '') or cca.value = :address) " +
           "    and (:isVIP is null or c.isVIP = :isVIP) " +
           "    and (:isBlackList is null or c.isBlackList = :isBlackList) " +
           "    and (:createdAtFrom is null or c.createdAt >= :createdAtFrom) " +
           "    and (:createdAtTo is null or c.createdAt <= :createdAtTo) " +
           "group by c.id, c.customerName, ccp.value, cce.value, cca.value")
    Page<CustomerSummaryProjection> findCustomerSummaryProjection(@Param("customerIds") List<Long> customerIds,
                                                                  @Param("name") String name,
                                                                  @Param("sex") String sex,
                                                                  @Param("birthday") Date birthday,
                                                                  @Param("phone") String phone,
                                                                  @Param("email") String email,
                                                                  @Param("address") String address,
                                                                  @Param("isVIP") Boolean isVIP,
                                                                  @Param("isBlackList") Boolean isBlackList,
                                                                  @Param("createdAtFrom") LocalDateTime createdAtFrom,
                                                                  @Param("createdAtTo") LocalDateTime createdAtTo,
                                                                  Pageable pageable);

    @Modifying
    @Query("update Customer c set c.bonusPoints = :bnsPoints where c.id = :customerId")
    void updateBonusPoint(@Param("customerId") Long customerId, @Param("bnsPoints") int bonusPoints);
}