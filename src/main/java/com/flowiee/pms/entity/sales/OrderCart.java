package com.flowiee.pms.entity.sales;

import com.flowiee.pms.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "order_cart")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCart extends BaseEntity implements Serializable {
	@Serial
	static final long serialVersionUID = 1L;
	
	@OneToMany(mappedBy = "orderCart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Items> listItems;

	public OrderCart(long id) {
		super.id = id;
	}

	@Override
	public String toString() {
		return "OrderCart [id=" + id + ", getCreatedAt()=" + getCreatedAt() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getLastUpdatedAt()=" + getLastUpdatedAt() + ", getLastUpdatedBy()=" + getLastUpdatedBy() + "]";
	}
}