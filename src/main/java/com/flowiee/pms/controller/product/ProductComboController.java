package com.flowiee.pms.controller.product;

import com.flowiee.pms.controller.BaseController;
import com.flowiee.pms.entity.product.ProductCombo;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.service.product.ProductComboService;
import com.flowiee.pms.utils.constants.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${app.api.prefix}/product/combo")
@Tag(name = "Product combo API", description = "Quản lý combo")
public class ProductComboController extends BaseController {
    private final ProductComboService productComboService;

    public ProductComboController(ProductComboService productComboService) {
        this.productComboService = productComboService;
    }

    @Operation(summary = "Find all combos")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleProduct.readCombo(true)")
    public AppResponse<List<ProductCombo>> findProductCombos(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                              @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            Page<ProductCombo> productComboPage = productComboService.findAll(pageSize, pageNum - 1);
            return success(productComboPage.getContent(), pageNum, pageSize, productComboPage.getTotalPages(), productComboPage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product combo"), ex);
        }
    }

    @Operation(summary = "Find detail combo")
    @GetMapping("/{comboId}")
    @PreAuthorize("@vldModuleProduct.readCombo(true)")
    public AppResponse<ProductCombo> findDetailCombo(@PathVariable("comboId") Integer comboId) {
        try {
            Optional<ProductCombo> productCombo = productComboService.findById(comboId);
            if (productCombo.isEmpty()) {
                throw new BadRequestException("productCombo not found");
            }
            return success(productCombo.get());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product combo"), ex);
        }
    }

    @Operation(summary = "Create new combo")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleProduct.insertCombo(true)")
    public AppResponse<ProductCombo> createCombo(@RequestBody ProductCombo productCombo) {
        try {
            return success(productComboService.save(productCombo));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product combo"), ex);
        }
    }

    @Operation(summary = "Update combo")
    @PutMapping("/update/{comboId}")
    @PreAuthorize("@vldModuleProduct.updateCombo(true)")
    public AppResponse<ProductCombo> updateProductCombo(@RequestBody ProductCombo productCombo, @PathVariable("comboId") Integer comboId) {
        try {
            return success(productComboService.update(productCombo, comboId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "product combo"), ex);
        }
    }

    @Operation(summary = "Delete product combo")
    @DeleteMapping("/delete/{comboId}")
    @PreAuthorize("@vldModuleProduct.deleteCombo(true)")
    public AppResponse<String> deleteCombo(@PathVariable("comboId") Integer comboId) {
        return success(productComboService.delete(comboId));
    }
}