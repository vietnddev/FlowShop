package com.flowiee.pms.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "product_variant_temp")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductVariantTemp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_temp_id", nullable = false)
    private ProductTemp productTemp;

    @Column(name = "sku")
    private String sku;

    @Column(name = "variant_code")
    private String variantCode;

    @Column(name = "variant_name")
    private String variantName;

    @Column(name = "color_id")
    private Long colorId;

    @JoinColumn(name = "size_id")
    private Long sizeId;

    @JoinColumn(name = "fabric_id")
    private Long fabricTypeId;

    @Column(name = "quantity_stg")
    private Integer storageQty;

    @Column(name = "quantity_sold")
    private Integer soldQty;

    @Column(name = "quantity_defective")
    private Integer defectiveQty;

    @Column(name = "weight")
    private String weight;

    @Column(name = "dimensions")
    private String dimensions;

    @Column(name = "is_limited_edition")
    private String isLimitedEdition;

    @Column(name = "status")
    private String status;
}