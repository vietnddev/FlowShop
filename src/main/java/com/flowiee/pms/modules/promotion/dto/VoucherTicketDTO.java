package com.flowiee.pms.modules.promotion.dto;

import com.flowiee.pms.modules.promotion.entity.VoucherTicket;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherTicketDTO extends VoucherTicket implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;
	
	String available;

	public VoucherTicketDTO(String available) {
		this.available = available;
	}
}