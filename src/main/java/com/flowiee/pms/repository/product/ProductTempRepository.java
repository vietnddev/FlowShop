package com.flowiee.pms.repository.product;

import com.flowiee.pms.entity.product.ProductTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTempRepository extends JpaRepository<ProductTemp, Long> {
}