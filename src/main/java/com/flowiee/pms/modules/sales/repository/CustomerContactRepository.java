package com.flowiee.pms.modules.sales.repository;


import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.sales.entity.CustomerContact;

import java.util.List;

@Repository
public interface CustomerContactRepository extends BaseRepository<CustomerContact, Long> {
    @Query("from CustomerContact c where c.customer.id=:customerId order by c.code, c.isDefault, c.status")
    List<CustomerContact> findByCustomerId(@Param("customerId") Long customerId);

    @Query("from CustomerContact c where c.customer.id=:customerId and c.code=:contactType and c.isDefault='Y' and c.status=true")
    CustomerContact findContactDefault(@Param("customerId") Long customerId, @Param("contactType") String contactType);

    @Query("from CustomerContact c where c.code = :contactType and c.value = :value")
    CustomerContact findByContactTypeAndValue(@Param("contactType") String contactType, @Param("value") String value);
}