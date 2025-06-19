package com.flowiee.pms.modules.system.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
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
//    private List<TicketImport> listPaymentMethod;
//    private List<Order> listKenhBanHang;
//    private List<ProductVariantDTO> listFabricType;
//    private List<ProductVariantDTO> listLoaiMauSac;
//    private List<ProductVariantDTO> listLoaiKichCo;
//    private List<Material> listUnit;
//    private List<Material> listBrand;
//    private List<Order> listOrderPayment;
//    private List<Product> listProductByProductType;
//    private List<Product> listProductByBrand;
//    private List<Product> listProductByUnit;
//    private List<CategoryHistory> listCategoryHistory;
//    private List<LedgerTransaction> listLedgerByGroupObject;
//    private List<LedgerTransaction> listLedgerTransByTranType;
//    private List<Customer> listCustomerByGroupCustomer;
//    private List<LeaveApplication> listLeaveApplication;
    private Integer totalSubRecords;
    private String statusName;
    private String inUse;

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}