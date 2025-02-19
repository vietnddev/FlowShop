package com.flowiee.pms.controller.product;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.ProductHistory;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.model.dto.ProductVariantTempDTO;
import com.flowiee.pms.service.product.ProductHistoryService;
import com.flowiee.pms.service.product.ProductPriceService;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/product")
@Tag(name = "Product API", description = "Quản lý biến thể sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductVariantController extends BaseController {
    ProductVariantService mvProductVariantService;
    ProductHistoryService mvProductHistoryService;
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
        Page<ProductVariantDTO> data = mvProductVariantService.findAll(CoreUtils.coalesce(pageSize), CoreUtils.coalesce(pageNum) - 1, pTxtSearch, productId, null, pBrandId, pColorId, pSizeId, fabricTypeId, readyForSales, true);
        return success(data.getContent(), data.getNumber() + 1, data.getSize(), data.getTotalPages(), data.getTotalElements());
    }

    @Operation(summary = "Find all variants of product")
    @GetMapping("/{productId}/variants")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductVariantDTO>> findVariantsOfProduct(@PathVariable("productId") Long productId) {
        return success(mvProductVariantService.findAll(-1, -1, null, productId, null, null, null, null, null, null, false).getContent());
    }

    @Operation(summary = "Find detail product variant")
    @GetMapping("/variant/{id}")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<ProductVariantDTO> findDetailProductVariant(@PathVariable("id") Long productVariantId) {
        ProductVariantDTO productVariant = mvProductVariantService.findById(productVariantId, true);
        return success(productVariant);
    }

    @Operation(summary = "Create product variant")
    @PostMapping("/variant/create")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public AppResponse<ProductDetail> createProductVariant(@RequestBody ProductVariantDTO productVariantDTO) {
        try {
            return success(mvProductVariantService.save(productVariantDTO));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "productVariant"), ex);
        }
    }

    @Operation(summary = "Update product variant")
    @PutMapping("/variant/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public AppResponse<ProductDetail> updateProductVariant(@RequestBody ProductVariantDTO productVariant, @PathVariable("id") Long productVariantId) {
        if (mvProductVariantService.findById(productVariantId, true) == null) {
            throw new ResourceNotFoundException("Product variant not found!");
        }
        return success(mvProductVariantService.update(productVariant, productVariantId));
    }

    @Operation(summary = "Delete product variant")
    @DeleteMapping("/variant/delete/{id}")
    @PreAuthorize("@vldModuleProduct.deleteProduct(true)")
    public AppResponse<String> deleteProductVariant(@PathVariable("id") Long productVariantId) {
        return success(mvProductVariantService.delete(productVariantId));
    }

    @Operation(summary = "Get price history of product detail")
    @GetMapping(value = "/variant/price/history/{Id}")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductHistory>> getHistoryPriceOfProductDetail(@PathVariable("Id") Long productVariantId) {
        if (mvProductVariantService.findById(productVariantId, true) == null) {
            throw new ResourceNotFoundException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product history"));
        }
        return success(mvProductHistoryService.findPriceChange(productVariantId));
    }

    @Operation(summary = "Update price")
    @PutMapping(value = "/variant/price/update/{productVariantId}")
    @PreAuthorize("@vldModuleProduct.priceManagement(true)")
    public AppResponse<String> updatePrice(@PathVariable("productVariantId") Long productVariantId,
                                           @RequestParam(value = "originalPrice", required = false) BigDecimal originalPrice,
                                           @RequestParam(value = "discountPrice", required = false) BigDecimal discountPrice) {
        try {
            if (mvProductVariantService.findById(productVariantId, true) == null) {
                throw new BadRequestException();
            }
            return success(mvProductPriceService.updateProductPrice(productVariantId, originalPrice, discountPrice));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "price"), ex);
        }
    }

    @Operation(summary = "Check product variant already exists")
    @GetMapping("/variant/exists")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<Boolean> checkProductVariantAlreadyExists(@RequestParam("productId") Long productId,
                                                                 @RequestParam("colorId") Long colorId,
                                                                 @RequestParam("sizeId") Long sizeId,
                                                                 @RequestParam("fabricTypeId") Long fabricTypeId) {
        try {
            return success(mvProductVariantService.isProductVariantExists(productId, colorId, sizeId, fabricTypeId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Operation(summary = "Get history import/export storage of product variant")
    @GetMapping("/variant/{productVariantId}/storage-history")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductVariantTempDTO>> getStorageHistoryOfProduct(@PathVariable("productVariantId") Long productVariantId) {
        try {
            return success(mvProductVariantService.findStorageHistory(productVariantId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Operation(summary = "Get list of product out of stock")
    @GetMapping("/variant/out-of-stock")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductVariantDTO>> getProductsOutOfStock(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                      @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        Page<ProductVariantDTO> productVariantDTOPage = mvProductVariantService.getProductsOutOfStock(-1, -1);
        return success(productVariantDTOPage.getContent(), pageNum, pageSize, productVariantDTOPage.getTotalPages(), productVariantDTOPage.getTotalElements());
    }
}