package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseControllerNew;
import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.dto.SupplierDTO;
import com.flowiee.pms.modules.sales.service.SupplierService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/supplier")
@Tag(name = "Material API", description = "Quản lý nhà cung cấp")
@RequiredArgsConstructor
public class SupplierController extends BaseControllerNew<SupplierDTO> {
    private final SupplierService mvSupplierService;
    private final ControllerHelper mvCHelper;

    @Override
    protected BaseGService<?, SupplierDTO, ?> getService() {
        return (BaseGService<?, SupplierDTO, ?>) mvSupplierService;
    }

    @Operation(summary = "Find all nhà cung cấp")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSales.readSupplier(true)")
    public AppResponse<List<SupplierDTO>> findAll(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                  @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            if (pageSize != null && pageNum != null) {
                Page<SupplierDTO> suppliers = mvSupplierService.findAll(pageSize, pageNum - 1, null);
                return mvCHelper.success(suppliers.getContent(), pageNum, pageSize, suppliers.getTotalPages(), suppliers.getTotalElements());
            } else {
                Page<SupplierDTO> suppliers = mvSupplierService.findAll(null, null, null);
                return mvCHelper.success(suppliers.getContent(), 1, 0, suppliers.getTotalPages(), suppliers.getTotalElements());
            }
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "supplier"), ex);
        }
    }

    @Operation(summary = "Thêm mới nhà cung cấp")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleSales.insertSupplier(true)")
    public AppResponse<SupplierDTO> createNewSupplier(@RequestBody SupplierDTO supplier) {
        return super.handleCreate(supplier);
    }

    @Operation(summary = "Cập nhật nhà cung cấp")
    @PutMapping("/update/{id}")
    @PreAuthorize("@vldModuleSales.updateSupplier(true)")
    public AppResponse<SupplierDTO> updateSupplier(@RequestBody SupplierDTO supplier, @PathVariable("id") Long supplierId) {
        return super.handleUpdate(supplier, supplierId);
    }

    @Operation(summary = "Xóa nhà cung cấp")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSales.deleteSupplier(true)")
    public AppResponse<String> deleteSupplier(@PathVariable("id") Long supplierId) {
        return super.handleDelete(supplierId);
    }
}