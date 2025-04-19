package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.flowiee.pms.common.base.entity.BaseEntity;

import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.enumeration.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "product_detail",
       indexes = {@Index(name = "idx_ProductVariant_productId", columnList = "product_id"),
                  @Index(name = "idx_ProductVariant_colorId", columnList = "color_id"),
                  @Index(name = "idx_ProductVariant_sizeId", columnList = "size_id")})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetail extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;
    
    @Column(name = "variant_code", length = 50, nullable = false, unique = true)
    String variantCode;
    
    @Column(name = "variant_name")
    String variantName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "color_id", nullable = false)
    Category color;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_id")
    Category size;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fabric_id")
    Category fabricType;

    @Column(name = "quantity_stg", nullable = false)
    Integer storageQty;

    @Column(name = "quantity_sold", nullable = false)
    Integer soldQty;

    @Column(name = "quantity_defective", nullable = false)
    Integer defectiveQty;

    @Column(name = "weight")
    String weight;

    @Column(name = "dimensions")
    String dimensions;

    @Column(name = "sku", unique = true)
    String sku;

    @Column(name = "supplier_sku", unique = true)
    String supplierSku;

    @Column(name = "warranty_period")
    Integer warrantyPeriod;

    @Column(name = "sole_material")
    String soleMaterial;

    @Column(name = "heel_height")
    String heelHeight;

    @Column(name = "discontinued_date")
    LocalDate discontinuedDate;

    @Column(name = "is_limited_edition")
    Boolean isLimitedEdition;

    @Column(name = "pattern")
    String pattern;

    @Column(name = "low_stock_threshold")
    Integer lowStockThreshold;

    @Column(name = "out_of_stock_date")
    LocalDateTime outOfStockDate;

    @Column(name = "manufacturing_country", length = 20)
    String manufacturingCountry;

    @Column(name = "manufacturing_date")
    LocalDate manufacturingDate;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Column(name = "storage_instructions")
    String storageInstructions;

    @Column(name = "uv_protection")
    String uvProtection;

    @Column(name = "is_machine_washable")
    Boolean isMachineWashable;

    @Column(name = "note")
    String note;//will be remove

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ProductStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<ProductAttribute> listAttributes;

    @JsonIgnore
    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY)
    List<OrderDetail> listOrderDetail;

    @JsonIgnore
    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<FileStorage> listImages;

    @JsonIgnore
    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Items> listItems;

    @JsonIgnore
    @OneToMany(mappedBy = "productDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<ProductHistory> listProductHistories;

    @JsonIgnore
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<ProductVariantExim> listProductVariantTemp;

    @JsonIgnore
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    List<ProductPrice> priceList;

    @JsonIgnore
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    List<ProductDamaged> productDamagedList;

//    @JsonIgnore
//    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
//    List<TransactionGoodsItem> transactionGoodsItemList;

    @JsonIgnore
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    List<ProductPriceHistory> productPriceHistoryList;

    @Transient
    Integer availableSalesQty;

    public ProductDetail(long id) {
        super.id = id;
    }

    public int getAvailableSalesQty() {
        return storageQty - defectiveQty;
    }

    public boolean isExpiredDate() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    private boolean isSalable() {
        Assert.notNull(status, "Status can't null!");
        return status.equals(ProductStatus.ACT);
    }

	@Override
	public String toString() {
		return "ProductVariant [id=" + super.id + ", variantCode=" + variantCode + ", variantName=" + variantName + ", status=" + status;
	}

    public String toStringInsert() {
        return "ProductVariant [id=" + this.id + ", code=" + variantCode + ", name=" + variantName +
                             ", color=" + color.getName() + ", size=" + size.getName() +
                             //", retailPrice=" + retailPrice + ", retailPriceDiscount=" + retailPriceDiscount +
                             //", wholesalePrice=" + wholesalePrice + ", wholesalePriceDiscount=" + wholesalePriceDiscount +
                             //", costPrice=" + costPrice + ", purchasePrice=" + purchasePrice +
                             ", status=" + status;
    }
}