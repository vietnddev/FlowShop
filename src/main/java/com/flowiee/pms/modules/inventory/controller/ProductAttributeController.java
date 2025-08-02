package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.dto.ProductAttributeDTO;
import com.flowiee.pms.modules.inventory.service.ProductAttributeService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.prefix}/product")
@Tag(name = "Product API", description = "Quản lý thuộc tính mở rộng của sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductAttributeController extends BaseController {
    ProductAttributeService mvProductAttributeService;

    @Operation(summary = "Create product attribute")
    @PostMapping("/attribute/create")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public AppResponse<ProductAttributeDTO> createProductAttribute(@RequestBody ProductAttributeDTO productAttribute) {
        try {
            return AppResponse.success(mvProductAttributeService.save(productAttribute));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product attribute"), ex);
        }
    }

    @Operation(summary = "Update product attribute")
    @PutMapping("/attribute/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public AppResponse<ProductAttributeDTO> updateProductAttribute(@RequestBody ProductAttributeDTO productAttribute, @PathVariable("id") Long productAttributeId) {
        try {
            return AppResponse.success(mvProductAttributeService.update(productAttribute, productAttributeId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "product attribute"), ex);
        }
    }

    @Operation(summary = "Delete product attribute")
    @DeleteMapping("/attribute/delete/{id}")
    @PreAuthorize("@vldModuleProduct.deleteProduct(true)")
    public AppResponse<String> deleteProductAttribute(@PathVariable("id") Long productAttributeId) {
        return AppResponse.success(mvProductAttributeService.delete(productAttributeId));
    }
}