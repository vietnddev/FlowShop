package com.flowiee.pms.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.shared.base.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "customer_contact")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerContact extends BaseEntity implements Serializable {
	static final long serialVersionUID = 1L;
	
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @Column(name = "code", length = 20, nullable = false)
    String code;

    @Column(name = "value", length = 500, nullable = false)
    String value;

    @Column(name = "note")
    String note;

    @Column(name = "is_default")
    String isDefault;

    @Column(name = "status", nullable = false)
    boolean status;

    @Column(name = "is_used")
    boolean isUsed;

	@Override
	public String toString() {
		return "CustomerContact [id=" + super.id + ", customer=" + customer + ", code=" + code + ", value=" + value + ", note=" + note + ", isDefault=" + isDefault + ", status=" + status + "]";
	}    
}