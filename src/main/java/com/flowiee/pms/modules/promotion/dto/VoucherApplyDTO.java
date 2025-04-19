package com.flowiee.pms.modules.promotion.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherApplyDTO implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    Long voucherApplyId;
    Long voucherInfoId;
    String voucherInfoTitle;
    Long productId;
    String productName;
    String appliedAt;
    Long appliedBy;
    
	@Override
	public String toString() {
		return "VoucherApplyDTO [voucherApplyId=" + voucherApplyId + ", voucherInfoId=" + voucherInfoId
				+ ", voucherInfoTitle=" + voucherInfoTitle + ", productId=" + productId + ", productName=" + productName
				+ ", appliedAt=" + appliedAt + ", appliedBy=" + appliedBy + "]";
	}    
}