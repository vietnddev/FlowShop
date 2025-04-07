package com.flowiee.pms.model.dto;

import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.ProductHistory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantDTO extends ProductDetail implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

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
    List<ProductHistory> listPrices;
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