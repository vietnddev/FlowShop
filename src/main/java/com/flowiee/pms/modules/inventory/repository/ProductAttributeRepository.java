package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.inventory.entity.ProductAttribute;

@Repository
public interface ProductAttributeRepository extends BaseRepository<ProductAttribute, Long> {
    @Query(value = "from ProductAttribute t where t.product.id=:productId order by t.sort asc")
    Page<ProductAttribute> findByProductId(@Param("productId") Long productId, Pageable pageable);
}