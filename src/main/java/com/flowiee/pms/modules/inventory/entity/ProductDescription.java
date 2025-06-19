package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "product_description")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDescription extends BaseEntity implements Serializable {
    @Column(name = "product_id", nullable = false)
    Long productId;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    String description;
}