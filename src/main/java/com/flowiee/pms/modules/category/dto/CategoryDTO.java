package com.flowiee.pms.modules.category.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.category.entity.CategoryHistory;
import com.flowiee.pms.modules.product.entity.Material;
import com.flowiee.pms.modules.product.entity.Product;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.entity.LedgerTransaction;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.inventory.entity.TicketImport;
import com.flowiee.pms.modules.leave.entity.LeaveApplication;
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