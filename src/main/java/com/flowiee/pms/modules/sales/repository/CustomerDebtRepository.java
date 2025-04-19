package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.modules.sales.entity.CustomerDebt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDebtRepository extends JpaRepository<CustomerDebt, Long> {
}