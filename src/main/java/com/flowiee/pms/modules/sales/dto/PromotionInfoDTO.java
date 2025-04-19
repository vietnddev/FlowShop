package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import com.flowiee.pms.modules.sales.entity.PromotionInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionInfoDTO extends PromotionInfo implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;
	
	String startTimeStr;
    String endTimeStr;
    String status;
    List<ProductDTO> applicableProducts;
}