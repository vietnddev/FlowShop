package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;

import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.entity.product.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "supplier")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Supplier extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

	@Column(name = "name", nullable = false)
    String name;

    @Column(name = "code", unique = true)
    String code;

    @Column(name = "tax_code")
    String taxCode;

    @Column(name = "phone")
    String phone;

    @Column(name = "email")
    String email;

    @Column(name = "address")
    String address;

    @Column(name = "website")
    String website;

    @Column(name = "contact_point")
    String contactPoint;

    @Column(name = "product_provided")
    String productProvided;

    @Column(name = "current_debt_amount")
    BigDecimal currentDebtAmount;

    @Column(name = "note")
    String note;

    @Column(name = "status")
    String status;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    List<TicketImport> listTicketImportGoods;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    List<Material> listMaterial;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    List<Product> productList;

    public Supplier(long id, String name) {
        this.id = id;
        this.name = name;
    }

	@Override
	public String toString() {
		return "Supplier [id=" + super.id + ", name=" + name + ", phone=" + phone + ", email=" + email + ", address=" + address
				+ ", productProvided=" + productProvided + ", note=" + note + ", status=" + status + "]";
	}
}