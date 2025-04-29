package com.flowiee.pms.modules.promotion.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.promotion.dto.VoucherInfoDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "voucher_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherInfo extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

	@Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", length = 1000)
    String description;

    @Column(name = "applicable_objects", nullable = false)
    String applicableObjects;

    @Column(name = "type", length = 1000)
    String voucherType;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "discount", nullable = false)
    Integer discount;

    @Column(name = "discount_price")
    BigDecimal discountPrice;

    @Column(name = "discount_price_max")
    BigDecimal discountPriceMax;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    @Column(name = "end_time", nullable = false)
    LocalDateTime endTime;

    @JsonIgnore
    @OneToMany(mappedBy = "voucherInfo", fetch = FetchType.LAZY)
    List<VoucherTicket> listVoucherTicket;

    public static VoucherInfo fromVoucherDTO(VoucherInfoDTO dto) {
        VoucherInfo voucherInfo = VoucherInfo.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .applicableObjects(dto.getApplicableObjects())
            .voucherType(dto.getVoucherType())
            .quantity(dto.getQuantity())
            .discount(dto.getDiscount())
            .discountPrice(dto.getDiscountPrice())
            .discountPriceMax(dto.getDiscountPriceMax())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .build();
        voucherInfo.setId(dto.getId());
        return voucherInfo;
    }

	@Override
	public String toString() {
		return "VoucherInfo [id=" + super.id + ", title=" + title + ", description=" + description + ", doiTuongApDung=" + applicableObjects
				+ ", voucherType=" + voucherType + ", quantity=" + quantity + ", discount=" + discount + ", maxPriceDiscount=" + discountPriceMax + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}
}