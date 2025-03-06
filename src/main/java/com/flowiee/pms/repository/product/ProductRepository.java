package com.flowiee.pms.repository.product;

import com.flowiee.pms.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.entity.product.Product;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {
    @Query("select p.id, p.productName from Product p")
    List<Object[]> findIdAndName();
}