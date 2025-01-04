package com.flowiee.pms.repository.sales;

import com.flowiee.pms.entity.sales.CustomerDebt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDebtRepository extends JpaRepository<CustomerDebt, Long> {
}