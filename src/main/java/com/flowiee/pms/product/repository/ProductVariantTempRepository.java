package com.flowiee.pms.product.repository;

import com.flowiee.pms.product.entity.ProductVariantTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantTempRepository extends JpaRepository<ProductVariantTemp, Long> {
}