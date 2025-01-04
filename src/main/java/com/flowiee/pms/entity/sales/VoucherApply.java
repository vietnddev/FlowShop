package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "voucher_apply")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherApply extends BaseEntity implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	@Column(name = "product_id", nullable = false)
	Long productId;

    @Column(name = "voucher_id", nullable = false)
	Long voucherId;

	@Override
	public String toString() {
		return "VoucherApply [id=" + super.id + ", productId=" + productId + ", voucherId=" + voucherId + "]";
	}
}