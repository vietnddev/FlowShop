package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import com.flowiee.pms.entity.product.ProductDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "garment_factory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GarmentFactory extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

	@Column(name = "name", nullable = false)
    String name;

    @Column(name = "phone")
    String phone;

    @Column(name = "email")
    String email;

    @Column(name = "address")
    String address;

    @Column(name = "note")
    String note;

    @Column(name = "status")
    String status;

    @OneToMany(mappedBy = "garmentFactory", fetch = FetchType.LAZY)
    List<ProductDetail> listProductDetail;

    public GarmentFactory(long id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "GarmentFactory [id=" + super.id + ", name=" + name + ", phone=" + phone + ", email=" + email + ", address=" + address + ", note=" + note + ", status=" + status + "]";
	}        
}