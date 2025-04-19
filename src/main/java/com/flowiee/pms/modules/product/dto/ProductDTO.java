package com.flowiee.pms.modules.product.dto;

import com.flowiee.pms.modules.product.entity.Product;
import com.flowiee.pms.modules.promotion.dto.VoucherInfoDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO extends Product implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    Long productTypeId;
    String productTypeName;
    Long brandId;
    String brandName;
    Long unitId;
    String unitName;
    Long garmentFactoryId;
    String garmentFactoryName;
    Long supplierId;
    String supplierName;
    String imageActive;
    Integer totalQtySell;
    Integer totalQtyStorage;
    Integer totalDefective;
    Integer totalQtyAvailableSales;
    Integer productVariantQty;
    Integer soldQty;
    String description;
    String status;
    List<VoucherInfoDTO> listVoucherInfoApply;
    LinkedHashMap<String, String> productVariantInfo;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductDTO [");
		builder.append(", productTypeId=").append(productTypeId);
		builder.append(", brandId=").append(brandId);
		builder.append(", unitId=").append(unitId);
		//builder.append(", status=").append(getStatus());
		builder.append(", productVariantQty=").append(productVariantQty);
		builder.append(", soldQty=").append(soldQty);
		builder.append(", createdAt=").append(getCreatedAt());
		builder.append(", createdByName=").append(getCreatedBy());
		builder.append("]");
		return builder.toString();
	}        
}