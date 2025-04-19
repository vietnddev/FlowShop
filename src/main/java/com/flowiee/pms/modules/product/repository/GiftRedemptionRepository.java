package com.flowiee.pms.modules.product.repository;

import com.flowiee.pms.modules.product.entity.GiftRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRedemptionRepository extends JpaRepository<GiftRedemption, Long> {

}