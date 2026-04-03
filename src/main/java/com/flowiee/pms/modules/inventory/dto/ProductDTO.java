package com.flowiee.pms.modules.inventory.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.common.enumeration.ProductStatus;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import com.flowiee.pms.modules.sales.dto.VoucherInfoDTO;
import com.flowiee.pms.modules.sales.dto.GarmentFactoryDTO;
import com.flowiee.pms.modules.sales.dto.SupplierDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO extends BaseDTO implements Serializable {
    String productCategory;
    CategoryDTO productType;
    CategoryDTO brand;
    CategoryDTO unit;
    String productName;
    LocalDate releaseDate;
    String gender;
    Boolean isSaleOff;
    Boolean isHotTrend;
    String returnPolicy;
    Long variantDefault;
    String internalNotes;
    GarmentFactoryDTO garmentFactory;
    SupplierDTO supplier;
    String imageActive;
    Integer stockQty = 0;
    Integer soldQty = 0;
    Integer defectiveQty = 0;
    Integer reservedQty = 0;
    Integer availableQty = 0;
    String description;
    String statusCode = ProductStatus.INA.name();
    String statusName = ProductStatus.INA.getLabel();
    List<VoucherInfoDTO> listVoucherInfoApply;

    List<ProductVariantDTO> variants;
    List<ProductAttributeDTO> attributes;

    public ProductDTO(Long id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "ProductDTO [" + super.getId() + "]";
	}
}