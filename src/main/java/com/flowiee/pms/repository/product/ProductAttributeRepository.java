package com.flowiee.pms.repository.product;

import com.flowiee.pms.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.entity.product.ProductAttribute;

@Repository
public interface ProductAttributeRepository extends BaseRepository<ProductAttribute, Long> {
    @Query(value = "from ProductAttribute t where t.productDetail.id=:productVariantId order by t.sort asc")
    Page<ProductAttribute> findByProductVariantId(@Param("productVariantId") Long productDetailId, Pageable pageable);
}