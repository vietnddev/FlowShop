package com.flowiee.pms.cart.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.cart.entity.Items;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CartItemsRepository extends BaseRepository<Items, Long> {
    @Query("from Items i where i.orderCart.id=:idCart")
    List<Items> findByCartId(@Param("idCart") Long idCart);

    @Query("select i.quantity from Items i where i.orderCart.id=:cartId and i.productDetail.id=:productVariantId")
    Integer findQuantityByProductVariantId(@Param("cartId") Long cartId, @Param("productVariantId") Long productVariantId);

    @Query("select count(i) > 0 from Items i where i.orderCart.id=:cartId and i.productDetail.id=:productVariantId")
    boolean existsByCartAndProductVariant(@Param("cartId") Long cartId, @Param("productVariantId") Long productVariantId);

    @Query("select count(i) > 0 from Items i where i.orderCart.id=:cartId and i.productDetail.id=:productVariantId")
    Items findByCartAndProductVariant(@Param("cartId") Long cartId, @Param("productVariantId") Long productVariantId);

    @Query("from Items i where i.orderCart.id=:cartId and (coalesce(:productVariantIds, -1) = -1 or i.productDetail.id in :productVariantIds)")
    List<Items> findItems(@Param("cartId") Long cartId, @Param("productVariantIds") List<Long> productVariantIds);

    @Query("select coalesce(sum(coalesce((case when i.price is not null then i.price else i.priceOriginal end), 0) * i.quantity), 0) " +
           "from Items i " +
           "where i.orderCart.id=:cartId")
    BigDecimal calTotalAmountWithoutDiscount(@Param("cartId") long cartId);

    @Modifying
    @Query("delete Items where orderCart.id=:cartId")
    void deleteAllItems(@Param("cartId") Long cartId);
}