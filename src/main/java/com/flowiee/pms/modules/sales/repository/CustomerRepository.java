package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.sales.entity.Customer;

import java.util.Date;
import java.util.List;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
    @Query("select distinct c from Customer c " +
           "left join CustomerContact cc on c.id = cc.customer.id " +
           "where (:name is null or c.customerName like %:name%) " +
           "and (:sex is null or c.gender=:sex) " +
           "and (:birthday is null or c.dateOfBirth=:birthday) " +
           "and (:phone is null or (cc.code = 'P' and cc.isDefault = 'Y' and cc.status = true and cc.value=:phone)) " +
           "and (:email is null or (cc.code = 'E' and cc.isDefault = 'Y' and cc.status = true and cc.value=:email)) " +
           "and (:address is null or (cc.code = 'A' and cc.isDefault = 'Y' and cc.status = true and cc.value=:address)) " +
           "order by c.customerName")
    Page<Customer> findAll(@Param("name") String name,
                           @Param("sex") String sex,
                           @Param("birthday") Date birthday,
                           @Param("phone") String phone,
                           @Param("email") String email,
                           @Param("address") String address,
                           Pageable pageable
    );

    @Query("from Customer c where extract(month from c.createdAt) = extract(month from current_date)")
    List<Customer> findCustomerNewInMonth();

    @Modifying
    @Query("update Customer c set c.bonusPoints = (c.bonusPoints + :bnsPoints) where c.id = :customerId")
    void updateBonusPoint(@Param("customerId") Long customerId, @Param("bnsPoints") int bonusPoints);

    @Query("from Customer c where c.isVIP = true")
    Page<Customer> findVIPCustomers(Pageable pageable);

    @Query("from Customer c where c.isBlackList = true")
    Page<Customer> findBlackListCustomers(Pageable pageable);
}