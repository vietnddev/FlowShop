package com.flowiee.pms.modules.promotion.repository;

import com.flowiee.pms.modules.promotion.entity.PromotionApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionApplyRepository extends JpaRepository<PromotionApply, Long> {
    @Query("select " +
            "pa.id as promotion_apply_id_0, " +
            "vi.id as promotion_info_id_1, " +
            "vi.title as promotion_info_title_2, " +
            "p.id as product_id_3, " +
            "p.productName as product_name_4, " +
            "pa.createdAt as applied_at_5, " +
            "pa.createdBy as applied_by_6 " +
            "from PromotionApply pa " +
            "left join VoucherInfo vi on vi.id = pa.promotionId " +
            "left join Product p on p.id = pa.productId " +
            "where 1=1" +
            "and (:productId is null or pa.productId=:productId) " +
            "and (:promotionId is null or pa.promotionId=:promotionId) ")
    List<Object[]> findAll(@Param("productId") Long productId, @Param("promotionId") Long promotionId);

    @Query("from PromotionApply where promotionId=:promotionId")
    List<PromotionApply> findByPromotionId(@Param("promotionId") Long promotionId);
}