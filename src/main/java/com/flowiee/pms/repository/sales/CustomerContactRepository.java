package com.flowiee.pms.repository.sales;


import com.flowiee.pms.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.entity.sales.CustomerContact;

import java.util.List;

@Repository
public interface CustomerContactRepository extends BaseRepository<CustomerContact, Long> {
    @Query("from CustomerContact c where c.customer.id=:customerId order by c.code, c.isDefault, c.status")
    List<CustomerContact> findByCustomerId(@Param("customerId") Long customerId);

    @Query("from CustomerContact c where c.customer.id=:customerId and c.code='P' and c.isDefault='Y' and c.status=true")
    CustomerContact findPhoneUseDefault(@Param("customerId") Long customerId);

    @Query("from CustomerContact c where c.customer.id=:customerId and c.code='E' and c.isDefault='Y' and c.status=true")
    CustomerContact findEmailUseDefault(@Param("customerId") Long customerId);

    @Query("from CustomerContact c where c.customer.id=:customerId and c.code='A' and c.isDefault='Y'  and c.status=true")
    CustomerContact findAddressUseDefault(@Param("customerId") Long customerId);

    @Query("from CustomerContact c where c.code = :contactType and c.value = :value")
    CustomerContact findByContactTypeAndValue(@Param("contactType") String contactType, @Param("value") String value);
}