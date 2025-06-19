package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.sales.dto.PromotionApplyDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "promotion_apply")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionApply extends BaseEntity implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	@Column(name = "product_id", nullable = false)
	Long productId;

    @Column(name = "promotion_id", nullable = false)
	Long promotionId;

	@Override
	public String toString() {
		return "PromotionApply [id=" + super.id + ", productId=" + productId + ", voucherId=" + promotionId + "]";
	}

	public static PromotionApply fromDTO(PromotionApplyDTO inputDTO) {
		return PromotionApply.builder()
				.productId(inputDTO.getProductId())
				.promotionId(inputDTO.getPromotionId())
				.build();
	}
}