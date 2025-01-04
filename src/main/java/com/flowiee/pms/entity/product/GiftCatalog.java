package com.flowiee.pms.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import com.flowiee.pms.entity.sales.GiftRedemption;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "gift_catalog")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftCatalog extends BaseEntity implements Serializable {
    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "required_points", nullable = false)
    Integer requiredPoints;

    @Column(name = "stock")
    Integer stock;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @JsonIgnore
    @OneToMany(mappedBy = "giftCatalog", fetch = FetchType.LAZY)
    List<GiftRedemption> giftRedemptionList;
}