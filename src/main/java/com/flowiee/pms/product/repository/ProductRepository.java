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
            p.id,
            cast(coalesce(sum(inv.onHandQuantity), 0) as integer),
            cast((select coalesce(sum(od.quantity), 0)
                from OrderDetail od
                where od.productDetail.product.id = p.id
                    and od.order.orderStatus = 'COMPLETED')as integer),
            cast(coalesce(sum(inv.defectiveQuantity), 0) as integer),
            cast(coalesce(sum(inv.reservedQuantity), 0) as integer)
        )
        from Product p
        left join p.productVariantList pv
            on pv.deletedAt is null
        left join pv.productInventory inv
        where p.id in (:productIds)
        group by p.id
    """)
    List<ProductSummaryModel> getInventorySummaryByProductIds(@Param("productIds") List<Long> productIds);

    @Query("select count(pd) > 0 from ProductDetail pd where pd.product.id = :productId and pd.status = 'ACT'")
    boolean hasActiveStatus(@Param("productId") Long productId);

    @Query("select count(p) from Product p where p.productType.id = :productTypeId")
    long countByProductType(@Param("productTypeId") Long productTypeId);
}