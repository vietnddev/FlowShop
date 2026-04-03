package com.flowiee.pms.product.controller;

import com.flowiee.pms.shared.base.BaseController;
import com.flowiee.pms.shared.constant.Constants;
import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.ResourceNotFoundException;
import com.flowiee.pms.shared.response.AppResponse;
import com.flowiee.pms.product.dto.ProductVariantDTO;
import com.flowiee.pms.product.dto.ProductVariantTempDTO;
import com.flowiee.pms.product.model.CreateProductVariantReq;
import com.flowiee.pms.product.model.ProductVariantSearchRequest;
import com.flowiee.pms.product.service.ProductPriceService;
import com.flowiee.pms.product.service.ProductVariantService;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.shared.enums.ErrorCode;
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
@RequestMapping("${app.api.prefix}/product")
@Tag(name = "Product API", description = "Quản lý biến thể sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductVariantController extends BaseController {
    ProductVariantService mvProductVariantService;
    ProductPriceService   mvProductPriceService;

    @Operation(summary = "Find all variants")
    @GetMapping("/variant/all")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductVariantDTO>> findProductVariants(@RequestParam(name = Constants.PAGE_SIZE, required = false, defaultValue = Constants.DEFAULT_PSIZE) Integer pageSize,
                                                                    @RequestParam(name = Constants.PAGE_NUM, required = false, defaultValue = Constants.DEFAULT_PNUM) Integer pageNum,
                                                                    @RequestParam(value = "txtSearch", required = false) String pTxtSearch,
                                                                    @RequestParam(value = "readyForSales", required = false) Boolean readyForSales,
                                                                    @RequestParam(value = "productId", required = false) Long productId,
                                                                    @RequestParam(value = "brandId", required = false) Long pBrandId,
                                                                    @RequestParam(value = "colorId", required = false) Long pColorId,
                                                                    @RequestParam(value = "sizeId", required = false) Long pSizeId,
                                                                    @RequestParam(value = "fabricTypeId", required = false) Long fabricTypeId) {
        Page<ProductVariantDTO> data = mvProductVariantService.findAll(ProductVariantSearchRequest.builder()
                .pageNum(CoreUtils.coalesce(pageNum) - 1).pageSize(CoreUtils.coalesce(pageSize)).txtSearch(pTxtSearch)
                .productId(productId).brandId(pBrandId).colorId(pColorId)
                .sizeId(pSizeId).fabricTypeId(fabricTypeId).availableForSales(readyForSales)
                .checkInAnyCart(true).build());
        return AppResponse.paged(data);
    }

    @Operation(summary = "Find all variants of product")
    @GetMapping("/{productId}/variants")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductVariantDTO>> findVariantsOfProduct(@PathVariable("productId") Long productId) {
        return AppResponse.success(mvProductVariantService.findAll(ProductVariantSearchRequest.builder()
                .productId(productId).checkInAnyCart(false).build()).getContent());
    }

    @Operation(summary = "Find detail product variant")
    @GetMapping("/variant/{id}")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<ProductVariantDTO> findDetailProductVariant(@PathVariable("id") Long productVariantId) {
        ProductVariantDTO productVariant = mvProductVariantService.findById(productVariantId, true);
        return AppResponse.success(productVariant);
    }

    @Operation(summary = "Create product variant")
    @PostMapping("/variant/create")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public AppResponse<ProductVariantDTO> createProductVariant(@RequestBody ProductVariantDTO productVariantDTO) {
        try {
            return AppResponse.success(mvProductVariantService.save(productVariantDTO));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "productVariant") + ex.getMessage(), ex);
        }
    }

    //Added 2026/03/06
    @PostMapping("/{productId}/variant/create")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public AppResponse<List<ProductVariantDTO>> createProductVariant(@PathVariable("productId") Long pProductId,
                                                                     @RequestBody CreateProductVariantReq pBody) {
        try {
            return AppResponse.success(mvProductVariantService.save(pProductId, pBody.getVariants()));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "productVariant") + ex.getMessage(), ex);
        }
    }

    @Operation(summary = "Update product variant")
    @PutMapping("/variant/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public AppResponse<ProductVariantDTO> updateProductVariant(@RequestBody ProductVariantDTO productVariant, @PathVariable("id") Long productVariantId) {
        if (mvProductVariantService.findById(productVariantId, true) == null) {
            throw new ResourceNotFoundException("Product variant not found!");
        }
        return AppResponse.success(mvProductVariantService.update(productVariant, productVariantId));
    }

    @Operation(summary = "Delete product variant")
    @DeleteMapping("/variant/delete/{variantId}")
    @PreAuthorize("@vldModuleProduct.deleteProduct(true)")
    public AppResponse<String> deleteProductVariant(@PathVariable("variantId") Long productVariantId) {
        return AppResponse.success(mvProductVariantService.delete(productVariantId));
    }

    @Operation(summary = "Update price")
    @PutMapping(value = "/variant/{variantId}/price/update")
    @PreAuthorize("@vldModuleProduct.priceManagement(true)")
    public AppResponse<ProductPriceDTO> updatePrice(@PathVariable("variantId") Long productVariantId, @RequestBody ProductPriceDTO pPrice) {
        return AppResponse.success(mvProductPriceService.update(pPrice, productVariantId));
    }

    @Operation(summary = "Check product variant already exists")
    @GetMapping("/variant/exists")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<Boolean> checkProductVariantAlreadyExists(@RequestParam("productId") Long productId,
                                                                 @RequestParam("colorId") Long colorId,
                                                                 @RequestParam("sizeId") Long sizeId,
                                                                 @RequestParam("fabricTypeId") Long fabricTypeId) {
        try {
            return AppResponse.success(mvProductVariantService.checkVariantExisted(productId, colorId, sizeId, fabricTypeId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Operation(summary = "Get history import/export storage of product variant")
    @GetMapping("/variant/{productVariantId}/storage-history")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductVariantTempDTO>> getStorageHistoryOfProduct(@PathVariable("productVariantId") Long productVariantId) {
        try {
            return AppResponse.success(mvProductVariantService.findStorageHistoryByVariantId(productVariantId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }
}