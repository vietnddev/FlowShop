package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;
import com.flowiee.pms.model.dto.PromotionApplyDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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