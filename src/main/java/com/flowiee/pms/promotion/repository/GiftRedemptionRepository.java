package com.flowiee.pms.promotion.repository;

import com.flowiee.pms.promotion.dto.GiftRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRedemptionRepository extends JpaRepository<GiftRedemption, Long> {

}