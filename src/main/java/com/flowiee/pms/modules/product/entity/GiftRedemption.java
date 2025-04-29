package com.flowiee.pms.modules.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.sales.entity.Customer;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_redemption")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftRedemption extends BaseEntity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_id", nullable = false)
    GiftCatalog giftCatalog;

    @Column(name = "redemption_date", nullable = false)
    LocalDateTime redemptionDate;

    @Column(name = "points_used", nullable = false)
    Integer pointsUsed;
}