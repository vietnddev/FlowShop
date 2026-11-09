package com.flowiee.pms.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.shared.base.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "product_inventory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInventory extends BaseEntity implements Serializable {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_variant_id", nullable = false, unique = true)
    ProductDetail variant;

    @Column(name = "on_hand_quantity", nullable = false)
    Integer onHandQuantity = 0; //hàng đang có trong kho

    @Column(name = "reserved_quantity", nullable = false)
    Integer reservedQuantity = 0; //hàng đã giữ cho order nhưng chưa giao

    @Column(name = "defective_quantity", nullable = false)
    Integer defectiveQuantity = 0; //hàng bị lỗi/hỏng

    @Column(name = "reorder_level", nullable = false)
    Integer reorderLevel = 10; //mức cảnh báo cần nhập thêm

    @JsonIgnore
    @OneToMany(mappedBy = "productInventory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<ProductStockMovement> productStockMovements;
}