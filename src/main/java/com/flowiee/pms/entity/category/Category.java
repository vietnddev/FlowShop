package com.flowiee.pms.entity.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.BaseEntity;
import com.flowiee.pms.entity.sales.*;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.entity.system.LeaveApplication;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public static final String ROOT_LEVEL = "ROOT";
	public static final String SUB_LEVEL = "SUB";

	@Column(name = "type", length = 20, nullable = false)
	String type;

	@Column(name = "code", length = 20, columnDefinition = "VARCHAR2(20) DEFAULT ''")
	String code;

	@Column(name = "name", length = 50, nullable = false)
	String name;

	@Column(name = "sort")
	Integer sort;

	@Column(name = "icon")
	String icon;

	@Column(name = "color")
	String color;

	@Column(name = "parent_id")
	Integer parentId;

	@Column(name = "note", length = 255)
	String note;

	@Column(name = "endpoint", length = 50)
	String endpoint;

	@Column(name = "is_default", length = 1, nullable = false)
	String isDefault;

	@Column(name = "status", length = 20, nullable = false)
	Boolean status;

	@JsonIgnore
	@OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
	List<TicketImport> listPaymentMethod;

	@JsonIgnore
	@OneToMany(mappedBy = "salesChannel", fetch = FetchType.LAZY)
	List<Order> listKenhBanHang;

	@JsonIgnore
	@OneToMany(mappedBy = "fabricType", fetch = FetchType.LAZY)
	List<ProductDetail> listFabricType;

	@JsonIgnore
	@OneToMany(mappedBy = "color", fetch = FetchType.LAZY)
	List<ProductDetail> listLoaiMauSac;

	@JsonIgnore
	@OneToMany(mappedBy = "size", fetch = FetchType.LAZY)
	List<ProductDetail> listLoaiKichCo;

	@JsonIgnore
	@OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
	List<Material> listUnit;

	@JsonIgnore
	@OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
	List<Material> listBrand;

	@JsonIgnore
	@OneToMany(mappedBy = "paymentMethod", fetch = FetchType.LAZY)
	List<Order> listOrderPayment;

//	@JsonIgnore
//	@OneToMany(mappedBy = "trangThaiDonHang", fetch = FetchType.LAZY)
//	List<Order> listTrangThaiDonHang;

	@JsonIgnore
	@OneToMany(mappedBy = "productType", fetch = FetchType.LAZY)
	List<Product> listProductByProductType;

	@JsonIgnore
	@OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
	List<Product> listProductByBrand;

	@JsonIgnore
	@OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
	List<Product> listProductByUnit;

	@JsonIgnore
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	List<CategoryHistory> listCategoryHistory;

	@JsonIgnore
	@OneToMany(mappedBy = "groupObject", fetch = FetchType.LAZY)
	List<LedgerTransaction> listLedgerByGroupObject;

	@JsonIgnore
	@OneToMany(mappedBy = "tranContent", fetch = FetchType.LAZY)
	List<LedgerTransaction> listLedgerTransByTranType;

	@JsonIgnore
	@OneToMany(mappedBy = "groupCustomer", fetch = FetchType.LAZY)
	List<Customer> listCustomerByGroupCustomer;

	@JsonIgnore
	@OneToMany(mappedBy = "leaveType", fetch = FetchType.LAZY)
	List<LeaveApplication> listLeaveApplication;

	@Transient
	Integer totalSubRecords;

	@Transient
	String statusName;

	@Transient
	String inUse;

	public Category(Long id) {
		super.id = id;
	}

	public Category(Long id, String name) {
		super.id = id;
		this.name = name;
	}

	public boolean isDefault() {
		return (isDefault == null || isDefault.isBlank()) ? false : "Y".equals(isDefault.trim());
	}

	@Override
	public String toString() {
		return "Category [id= " + super.id + ", name=" + name + "]";
	}
}