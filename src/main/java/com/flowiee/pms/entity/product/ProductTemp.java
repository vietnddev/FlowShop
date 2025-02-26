package com.flowiee.pms.entity.product;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
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

    @OneToMany(mappedBy = "productTemp", fetch = FetchType.LAZY)
    private List<ProductVariantTemp> productVariantTempList;
}