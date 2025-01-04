package com.flowiee.pms.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
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
    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_variant_id")
    ProductDetail productVariant;

    @Lob
    @Column(name = "description", length = 30000, columnDefinition = "CLOB")
    String description;
}