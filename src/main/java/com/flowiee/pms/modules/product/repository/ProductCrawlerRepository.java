package com.flowiee.pms.modules.product.repository;

import com.flowiee.pms.modules.product.entity.ProductCrawled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCrawlerRepository extends JpaRepository<ProductCrawled, Long> {

}