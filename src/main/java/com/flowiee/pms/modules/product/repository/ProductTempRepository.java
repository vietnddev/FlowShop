package com.flowiee.pms.modules.product.repository;

import com.flowiee.pms.modules.product.entity.ProductTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTempRepository extends JpaRepository<ProductTemp, Long> {
}