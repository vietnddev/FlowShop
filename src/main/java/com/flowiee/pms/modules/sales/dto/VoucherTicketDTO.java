package com.flowiee.pms.modules.sales.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.VoucherInfo;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherTicketDTO extends BaseDTO implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;

	VoucherInfo voucherInfo;
	String code;
	Integer length;
	Date activeTime;
	Customer customer;
	boolean isUsed;
	String available;

	public VoucherTicketDTO(String available) {
		this.available = available;
	}
}