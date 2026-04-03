package com.flowiee.pms.product.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.product.model.ProductSummaryModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.product.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {
    @Query("select p.id, p.productName from Product p")
    List<Object[]> findIdAndName();

    @Query("""
        select new com.flowiee.pms.product.model.ProductSummaryModel(
            pd.product.id,
            cast(coalesce(sum(pd.storageQty),0) as integer),
            cast(coalesce(sum(pd.soldQty),0) as integer),
            cast(coalesce(sum(pd.defectiveQty),0) as integer),
            cast((select coalesce(sum(od.quantity),0) from OrderDetail od
                where od.productDetail.product.id in (:productId)
                     and od.order.orderStatus = 'PROCESSING') as integer),
            (select count(pd_) > 0 from ProductDetail pd_ where pd_.product.id in (:productId) and pd_.status = 'ACT'))
        from com.flowiee.pms.product.entity.ProductDetail pd
        where pd.product.id in (:productId) and pd.deletedAt is null
        group by pd.product.id
    """)
    List<ProductSummaryModel> getSummariesQty(@Param("productId") List<Long> productId);

    @Query("select count(pd) > 0 from ProductDetail pd where pd.product.id = :productId and pd.status = 'ACT'")
    boolean hasActiveStatus(@Param("productId") Long productId);
}