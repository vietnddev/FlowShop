package com.flowiee.pms.modules.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.media.entity.FileStorage;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_damaged")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDamaged extends BaseEntity implements Serializable {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    ProductDetail productVariant;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "damage_reason", nullable = false)
    private String damageReason;

    @Column(name = "recorded_date", nullable = false)
    private LocalDateTime recordedDate;

    @JsonIgnore
    @OneToMany(mappedBy = "productDamaged", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<FileStorage> imageList;

    @Column(name = "note")
    private String note;
}