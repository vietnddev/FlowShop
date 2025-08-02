package com.flowiee.pms.modules.inventory.entity;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "product_temp")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductTemp implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_type_id")
    private Long productTypeId;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "is_sale_off")
    private String isSaleOff;

    @Column(name = "is_hot_trend")
    private String isHotTrend;

    @Column(name = "note")
    private String note;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "productTemp", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<ProductVariantTemp> productVariantTempList;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("productTypeId", productTypeId)
                .append("brandId", brandId)
                .append("unitId", unitId)
                .append("productName", productName)
                .append("gender", gender)
                .append("isSaleOff", isSaleOff)
                .append("isHotTrend", isHotTrend)
                .append("note", note)
                .toString();
    }
}