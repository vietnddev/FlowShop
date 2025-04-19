package com.flowiee.pms.modules.promotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.promotion.entity.VoucherApply;

import java.util.List;

@Repository
public interface VoucherApplyRepository extends JpaRepository<VoucherApply, Long> {
    @Query("select " +
           "va.id as voucher_apply_id_0, " +
           "vi.id as voucher_info_id_1, " +
           "vi.title as voucher_info_title_2, " +
           "p.id as product_id_3, " +
           "p.productName as product_name_4, " +
           "va.createdAt as applied_at_5, " +
           "va.createdBy as applied_by_6 " +
           "from VoucherApply va " +
           "left join VoucherInfo vi on vi.id = va.voucherId " +
           "left join Product p on p.id = va.productId " +
           "where (:productId is null or p.id=:productId) ")
    List<Object[]> findAll(@Param("productId") Long productId);

    @Query("from VoucherApply where voucherId=:voucherId")
    List<VoucherApply> findByVoucherId(@Param("voucherId") Long voucherId);
}