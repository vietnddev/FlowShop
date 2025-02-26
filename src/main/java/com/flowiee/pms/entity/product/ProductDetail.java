package com.flowiee.pms.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.flowiee.pms.base.entity.BaseEntity;

import com.flowiee.pms.entity.sales.Items;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.entity.sales.Supplier;
import com.flowiee.pms.entity.sales.GarmentFactory;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.common.enumeration.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "product_detail")
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    Category color;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id")
    Category size;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fabric_id")
    Category fabricType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garment_factory_id")
    GarmentFactory garmentFactory;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

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

    @Column(name = "sku")
    String sku;

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

    @Column(name = "manufacturing_date")
    LocalDate manufacturingDate;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Column(name = "note")
    String note;//will be remove

    @OneToOne(mappedBy = "productVariant", cascade = CascadeType.ALL)
    ProductDescription productDescription;

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
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.EAGER)
    List<ProductPrice> priceList;

    @JsonIgnore
    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
    List<ProductDamaged> productDamagedList;

//    @JsonIgnore
//    @OneToMany(mappedBy = "productVariant", fetch = FetchType.LAZY)
//    List<TransactionGoodsItem> transactionGoodsItemList;

    @Transient
    Integer availableSalesQty;

    public ProductDetail(long id) {
        super.id = id;
    }

    public int getAvailableSalesQty() {
        return storageQty - defectiveQty;
    }

    public ProductPrice getVariantPrice(Long pPriceId) {
        if (getPriceList() != null) {
            for (ProductPrice price : getPriceList()) {
                Long lvPriceId = price.getId();
                if (lvPriceId.equals(pPriceId))
                    return price;
            }
            return null;
        }
        return null;
    }

    public ProductPrice getVariantPrice() {
        if (getPriceList() != null) {
            for (ProductPrice price : getPriceList()) {
                if (ProductPrice.STATE_ACTIVE.equals(price.getState()))
                    return price;
            }
            return null;
        }
        return null;
    }

    public FileStorage getImage(Long pImageId) {
        if (getListImages() != null) {
            return getListImages().stream()
                    .filter(image -> image.getId().equals(pImageId))
                    .findAny()
                    .orElse(null);
        }
        return null;
    }

    public FileStorage getImage() {
        if (getListImages() != null) {
            for (FileStorage image : getListImages()) {
                if (image.isActive())
                    return image;
            }
            return null;
        }
        return null;
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