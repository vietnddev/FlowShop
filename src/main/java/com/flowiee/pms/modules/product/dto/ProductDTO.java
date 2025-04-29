package com.flowiee.pms.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.product.entity.*;
import com.flowiee.pms.modules.promotion.dto.VoucherInfoDTO;
import com.flowiee.pms.modules.sales.entity.GarmentFactory;
import com.flowiee.pms.modules.sales.entity.Supplier;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO extends BaseDTO implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    String PID;
    Category productType;
    Category brand;
    Category unit;
    String productName;
    LocalDate releaseDate;
    String gender;
    Boolean isSaleOff;
    Boolean isHotTrend;
    String returnPolicy;
    Long variantDefault;
    String internalNotes;
    GarmentFactory garmentFactory;
    Supplier supplier;
//    @JsonIgnore
//    List<ProductDetail> productVariantList;
//    @JsonIgnore
//    List<FileStorage> listImages;
//    @JsonIgnore
//    List<ProductHistory> listProductHistories;
//    @JsonIgnore
//    List<ProductReview> listProductPreviews;
//    @JsonIgnore
//    List<ProductPrice> listProductBasePrice;
//    @JsonIgnore
//    List<ProductDamaged> productDamagedList;
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    List<ProductRelated> productRelatedList;

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