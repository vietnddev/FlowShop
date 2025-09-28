package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.common.enumeration.ProductStatus;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantDTO extends BaseDTO implements Serializable {
    static final long serialVersionUID = 1L;

    ProductDTO product;
    String variantCode;
    String variantName;
    CategoryDTO color;
    CategoryDTO size;
    CategoryDTO fabricType;
    Integer storageQty;
    Integer soldQty;
    Integer defectiveQty;
    String weight;
    String dimensions;
    String sku;
    String supplierSku;
    Integer warrantyPeriod;
    String soleMaterial;
    String heelHeight;
    LocalDate discontinuedDate;
    Boolean isLimitedEdition;
    String pattern;
    Integer lowStockThreshold;
    LocalDateTime outOfStockDate;
    String manufacturingCountry;
    LocalDate manufacturingDate;
    LocalDate expiryDate;
    String storageInstructions;
    String uvProtection;
    Boolean isMachineWashable;
    String note;//will be remove
    ProductStatus status;
    Integer availableSalesQty;

    Long productId;
    Long productTypeId;
    String productTypeName;
    Long brandId;
    String brandName;
    Long unitId;
    String  unitName;
    Long colorId;
    String colorName;
    Long sizeId;
    String sizeName;
    Long fabricTypeId;
    String fabricTypeName;
    BigDecimal originalPrice;
    BigDecimal discountPrice;
    String unitCurrency;
    //List<ProductHistory> listPrices;
    Long storageIdInitStorageQty;
    Long storageIdInitSoldQty;
    ProductPriceDTO price;
    String imageSrc;
    Boolean currentInCart;

    public ProductVariantDTO(Long id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "ProductVariantDTO [id=" + id + ", code=" + getVariantCode() + ", name=" + getVariantName()
                + ", storageQty=" + getStorageQty() + ", soldQty=" + getSoldQty() + ", status=" + getStatus() + ", colorId=" + colorId + ", sizeId=" + sizeId
                + ", fabricTypeId=" + fabricTypeId + "]";
	}
}