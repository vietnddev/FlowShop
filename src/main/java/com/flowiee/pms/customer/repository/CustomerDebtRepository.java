package com.flowiee.pms.customer.repository;

import com.flowiee.pms.customer.entity.CustomerDebt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDebtRepository extends JpaRepository<CustomerDebt, Long> {
}