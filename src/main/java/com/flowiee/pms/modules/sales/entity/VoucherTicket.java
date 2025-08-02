package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Builder
@Entity
@Table(name = "voucher_ticket")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherTicket extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "voucher_info_id", nullable = false)
    VoucherInfo voucherInfo;

    @Column(name = "code", nullable = false, length = 15, unique = true)
    String code;

    @Column(name = "length", nullable = false)
    Integer length;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "active_time")
    Date activeTime;

    @ManyToOne
    @JoinColumn(name = "customer")
    Customer customer;

    @Column(name = "is_used", nullable = false)
    boolean isUsed;

	@Override
	public String toString() {
		return "VoucherTicket [id=" + super.id + ", voucherInfo=" + voucherInfo + ", code=" + code + ", activeTime=" + activeTime
				+ ", customer=" + customer + ", status=" + isUsed + "]";
	}
}