package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @ManyToOne
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

    @Column(name = "retail_price")
    private BigDecimal retailPrice;

    @Column(name = "wholesale_price")
    private BigDecimal wholesalePrice;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "weight")
    private String weight;

    @Column(name = "dimensions")
    private String dimensions;

    @Column(name = "is_limited_edition")
    private String isLimitedEdition;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("productTemp", productTemp)
                .append("sku", sku)
                .append("variantCode", variantCode)
                .append("variantName", variantName)
                .append("colorId", colorId)
                .append("sizeId", sizeId)
                .append("fabricTypeId", fabricTypeId)
                .append("storageQty", storageQty)
                .append("soldQty", soldQty)
                .append("defectiveQty", defectiveQty)
                .append("weight", weight)
                .append("dimensions", dimensions)
                .append("isLimitedEdition", isLimitedEdition)
                .append("status", status)
                .toString();
    }
}