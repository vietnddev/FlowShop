package com.flowiee.pms.product.controller;

import com.flowiee.pms.shared.base.BaseController;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.response.AppResponse;
import com.flowiee.pms.product.dto.ProductAttributeDTO;
import com.flowiee.pms.product.model.CreateProductAttributeReq;
import com.flowiee.pms.product.service.ProductAttributeService;
import com.flowiee.pms.shared.enums.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //Added 2026/03/06
    @PostMapping("/{productId}/attribute/create")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public AppResponse<List<ProductAttributeDTO>> createProductAttribute(@PathVariable("productId") Long pProductId,
                                                                        @RequestBody CreateProductAttributeReq pBody) {
        try {
            return AppResponse.success(mvProductAttributeService.saveAll(pProductId, pBody.getAttributes()));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "productVariant") + ex.getMessage(), ex);
        }
    }

    @Operation(summary = "Update product attribute")
    @PutMapping("/{productId}/attribute/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public AppResponse<ProductAttributeDTO> updateProductAttribute(@PathVariable("productId") Long productId,
                                                                   @PathVariable("id") Long productAttributeId,
                                                                   @RequestBody ProductAttributeDTO productAttribute) {
        try {
            return AppResponse.success(mvProductAttributeService.update(productAttribute, productAttributeId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "product attribute"), ex);
        }
    }

    @Operation(summary = "Delete product attribute")
    @DeleteMapping("/attribute/delete/{attributeId}")
    @PreAuthorize("@vldModuleProduct.deleteProduct(true)")
    public AppResponse<String> deleteProductAttribute(@PathVariable("attributeId") Long productAttributeId) {
        return AppResponse.success("Success: " + mvProductAttributeService.delete(productAttributeId));
    }
}