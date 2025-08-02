package com.flowiee.pms.modules.inventory.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "product_crawled")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductCrawled implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "sku")
    private String sku;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "fabric")
    private String fabric;

    @Column(name = "colors")
    private String color;

    @Column(name = "sizes")
    private String size;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "origin_price")
    private BigDecimal originalPrice;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "created_by")
    private String createdBy;

    @Transient
    private List<ImageCrawled> imageCrawledList;
}