package com.flowiee.pms.repository.product;

import com.flowiee.pms.entity.product.ProductVariantTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantTempRepository extends JpaRepository<ProductVariantTemp, Long> {
}