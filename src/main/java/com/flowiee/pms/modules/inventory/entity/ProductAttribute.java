package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "product_attribute")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttribute extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "attribute_name", nullable = false)
    String attributeName;

    @Column(name = "attribute_value", length = 500)
    String attributeValue;

    @Column(name = "sort", nullable = false)
    int sort;

    @Column(name = "status", nullable = false)
    boolean status;

    @OneToMany(mappedBy = "productAttribute", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<ProductHistory> listProductHistory;

    public ProductAttribute(Long id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "ProductAttribute [id=" + super.id  + ", attributeName=" + attributeName
				+ ", attributeValue=" + attributeValue + ", sort=" + sort + ", status=" + status + "]";
	}
}