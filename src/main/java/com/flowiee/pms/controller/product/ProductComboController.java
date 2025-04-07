package com.flowiee.pms.controller.product;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.dto.ProductComboDTO;
import com.flowiee.pms.service.product.ProductComboService;
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
@RequestMapping("${app.api.prefix}/product/combo")
@Tag(name = "Product combo API", description = "Quản lý combo")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductComboController extends BaseController {
    ProductComboService mvProductComboService;

    @Operation(summary = "Find all combos")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleProduct.readCombo(true)")
    public AppResponse<List<ProductComboDTO>> findProductCombos(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        Page<ProductComboDTO> productComboPage = mvProductComboService.findAll(pageSize, pageNum - 1);
        return success(productComboPage.getContent(), pageNum, pageSize, productComboPage.getTotalPages(), productComboPage.getTotalElements());
    }

    @Operation(summary = "Find detail combo")
    @GetMapping("/{comboId}")
    @PreAuthorize("@vldModuleProduct.readCombo(true)")
    public AppResponse<ProductComboDTO> findDetailCombo(@PathVariable("comboId") Long comboId) {
        ProductComboDTO productCombo = mvProductComboService.findById(comboId, true);
        return success(productCombo);
    }

    @Operation(summary = "Create new combo")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleProduct.insertCombo(true)")
    public AppResponse<ProductComboDTO> createCombo(@RequestBody ProductComboDTO productCombo) {
        try {
            return success(mvProductComboService.save(productCombo));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product combo"), ex);
        }
    }

    @Operation(summary = "Update combo")
    @PutMapping("/update/{comboId}")
    @PreAuthorize("@vldModuleProduct.updateCombo(true)")
    public AppResponse<ProductComboDTO> updateProductCombo(@RequestBody ProductComboDTO productCombo, @PathVariable("comboId") Long comboId) {
        try {
            return success(mvProductComboService.update(productCombo, comboId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "product combo"), ex);
        }
    }

    @Operation(summary = "Delete product combo")
    @DeleteMapping("/delete/{comboId}")
    @PreAuthorize("@vldModuleProduct.deleteCombo(true)")
    public AppResponse<String> deleteCombo(@PathVariable("comboId") Long comboId) {
        return success(mvProductComboService.delete(comboId));
    }
}