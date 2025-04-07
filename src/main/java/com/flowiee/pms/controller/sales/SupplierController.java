package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.dto.SupplierDTO;
import com.flowiee.pms.service.sales.SupplierService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/supplier")
@Tag(name = "Material API", description = "Quản lý nhà cung cấp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SupplierController extends BaseController {
    SupplierService mvSupplierService;

    @Operation(summary = "Find all nhà cung cấp")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSales.readSupplier(true)")
    public AppResponse<List<SupplierDTO>> findAll(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                  @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            if (pageSize != null && pageNum != null) {
                Page<SupplierDTO> suppliers = mvSupplierService.findAll(pageSize, pageNum - 1, null);
                return success(suppliers.getContent(), pageNum, pageSize, suppliers.getTotalPages(), suppliers.getTotalElements());
            } else {
                Page<SupplierDTO> suppliers = mvSupplierService.findAll(null, null, null);
                return success(suppliers.getContent(), 1, 0, suppliers.getTotalPages(), suppliers.getTotalElements());
            }
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "supplier"), ex);
        }
    }

    @Operation(summary = "Thêm mới nhà cung cấp")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleSales.insertSupplier(true)")
    public AppResponse<SupplierDTO> createNewSupplier(@RequestBody SupplierDTO supplier) {
        try {
            return success(mvSupplierService.save(supplier));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "supplier"), ex);
        }
    }

    @Operation(summary = "Cập nhật nhà cung cấp")
    @PutMapping("/update/{id}")
    @PreAuthorize("@vldModuleSales.updateSupplier(true)")
    public AppResponse<SupplierDTO> updateSupplier(@RequestBody SupplierDTO supplier, @PathVariable("id") Long supplierId) {
        return success(mvSupplierService.update(supplier, supplierId));
    }

    @Operation(summary = "Xóa nhà cung cấp")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSales.deleteSupplier(true)")
    public AppResponse<String> deleteSupplier(@PathVariable("id") Long supplierId) {
        return success(mvSupplierService.delete(supplierId));
    }
}