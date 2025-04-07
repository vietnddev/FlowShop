package com.flowiee.pms.model.dto;

import com.flowiee.pms.base.service.BaseDTO;
import com.flowiee.pms.entity.category.CategoryHistory;
import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.sales.Customer;
import com.flowiee.pms.entity.sales.LedgerTransaction;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.TicketImport;
import com.flowiee.pms.entity.system.LeaveApplication;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class CategoryDTO extends BaseDTO implements Serializable {
    private String type;
    private String code;
    private String name;
    private Integer sort;
    private String icon;
    private String color;
    private Integer parentId;
    private String note;
    private String endpoint;
    private String isDefault;
    private Boolean status;
    private List<TicketImport> listPaymentMethod;
    private List<Order> listKenhBanHang;
    private List<ProductDetail> listFabricType;
    private List<ProductDetail> listLoaiMauSac;
    private List<ProductDetail> listLoaiKichCo;
    private List<Material> listUnit;
    private List<Material> listBrand;
    private List<Order> listOrderPayment;
    private List<Product> listProductByProductType;
    private List<Product> listProductByBrand;
    private List<Product> listProductByUnit;
    private List<CategoryHistory> listCategoryHistory;
    private List<LedgerTransaction> listLedgerByGroupObject;
    private List<LedgerTransaction> listLedgerTransByTranType;
    private List<Customer> listCustomerByGroupCustomer;
    private List<LeaveApplication> listLeaveApplication;
    private Integer totalSubRecords;
    private String statusName;
    private String inUse;
}