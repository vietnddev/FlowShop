package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.modules.inventory.entity.ProductHistory;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.model.ProductSearchRequest;
import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.inventory.dto.ProductRelatedDTO;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.inventory.service.ProductHistoryService;
import com.flowiee.pms.modules.inventory.service.ProductInfoService;
import com.flowiee.pms.modules.inventory.service.ProductRelatedService;
import com.flowiee.pms.modules.system.service.ExportService;
import com.flowiee.pms.modules.system.service.ImportService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.TemplateExport;
import com.flowiee.pms.modules.inventory.util.ProductConvert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/product")
@Tag(name = "Product API", description = "Quản lý sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductController extends BaseController {
    @Autowired
    @NonFinal
    @Qualifier("productExportServiceImpl")
    ExportService mvExportService;
    @Autowired
    @NonFinal
    @Qualifier("productImportServiceImpl")
    ImportService mvImportService;
    ProductInfoService mvProductInfoService;
    ProductHistoryService mvProductHistoryService;
    ProductRelatedService mvProductRelatedService;

    @Operation(summary = "Find all products")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductDTO>> findProducts(@RequestParam(name = Constants.PAGE_SIZE, required = false, defaultValue = Constants.DEFAULT_PSIZE) Integer pageSize,
                                                      @RequestParam(name = Constants.PAGE_NUM, required = false, defaultValue = Constants.DEFAULT_PNUM) Integer pageNum,
                                                      @RequestParam(value = "txtSearch", required = false) String txtSearch,
                                                      @RequestParam(value = "brandId", required = false) Long pBrand,
                                                      @RequestParam(value = "productTypeId", required = false) Long pProductType,
                                                      @RequestParam(value = "colorId", required = false) Long pColor,
                                                      @RequestParam(value = "sizeId", required = false) Long pSize,
                                                      @RequestParam(value = "unitId", required = false) Long pUnit,
                                                      @RequestParam(value = "gender", required = false) String pGender,
                                                      @RequestParam(value = "salesOff", required = false) Boolean pIsSalesOff,
                                                      @RequestParam(value = "hotTrend", required = false) Boolean pIsHotTrend,
                                                      @RequestParam(value = "fullInfo", required = false) Boolean fullInfo) {
        try {
            if (fullInfo != null && !fullInfo) {
                return AppResponse.success(ProductConvert.convertToDTOs(mvProductInfoService.findProductsIdAndProductName()));
            }
            Page<ProductDTO> productPage = mvProductInfoService.findAll(ProductSearchRequest.builder()
                    .pageSize(pageSize).pageNum(pageNum - 1).txtSearch(txtSearch)
                    .brandId(pBrand).productTypeId(pProductType).colorId(pColor)
                    .sizeId(pSize).unitId(pUnit).gender(pGender)
                    .isSaleOff(pIsSalesOff).isHotTrend(pIsHotTrend).build(), true);
            return AppResponse.paged(productPage);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Operation(summary = "Find detail products")
    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<ProductDTO> findDetailProduct(@PathVariable("id") Long productId) {
        ProductDTO product = mvProductInfoService.findById(productId, true);
        if (product == null) {
            throw new ResourceNotFoundException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "product"));
        }
        return AppResponse.success(product);
    }

    @Operation(summary = "Create clothes product")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public AppResponse<ProductDTO> createProduct(@RequestBody ProductDTO pDto) {
        return AppResponse.success(mvProductInfoService.save(pDto));
    }

    @Operation(summary = "Update product")
    @PutMapping("/update/{id}")
    @PreAuthorize("@vldModuleProduct.updateProduct(true)")
    public AppResponse<ProductDTO> updateProduct(@RequestBody ProductDTO product, @PathVariable("id") Long productId) {
        return AppResponse.success(mvProductInfoService.update(product, productId));
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleProduct.deleteProduct(true)")
    public AppResponse<String> deleteProduct(@PathVariable("id") Long productId) {
        return AppResponse.success(mvProductInfoService.delete(productId));
    }

    @Operation(summary = "Get histories of product")
    @GetMapping(value = "/{productId}/history")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<ProductHistory>> getHistoryOfProduct(@PathVariable("productId") Long productId) {
        return AppResponse.success(mvProductHistoryService.findByProduct(productId));
    }

    @Operation(summary = "Add related product")
    @PostMapping("/{productId}/related/{relatedProductId}")
    public AppResponse<String> addRelatedProduct(@PathVariable Long productId, @PathVariable Long relatedProductId) {
        mvProductRelatedService.add(productId, relatedProductId);
        return AppResponse.success("Related product added successfully!");
    }

    @Operation(summary = "Get related product")
    @GetMapping("/{productId}/related")
    public AppResponse<List<ProductRelatedDTO>> getRelatedProducts(@PathVariable Long productId) {
        return AppResponse.success(mvProductRelatedService.get(productId));
    }

    @Operation(summary = "Delete related product")
    @GetMapping("/related/{relationId}")
    public AppResponse<String> removeRelatedProduct(@PathVariable Long relationId) {
        mvProductRelatedService.remove(relationId);
        return AppResponse.success("Related product deleted successfully!");
    }

    @GetMapping("/discontinued")
    public AppResponse<List<ProductDTO>> getDiscontinuedProducts() {
        return AppResponse.success(mvProductInfoService.getDiscontinuedProducts());
    }

    @GetMapping("/import/template")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ResponseEntity<?> downloadTemplate() {
        EximResult model = mvExportService.exportToExcel(TemplateExport.IE_LIST_OF_PRODUCTS, null, true);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }

    @PostMapping("/import")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public String importData(@RequestParam("file") MultipartFile file) {
        EximResult eximResult = mvImportService.importFromExcel(TemplateExport.IM_LIST_OF_PRODUCTS, file);
        return eximResult.getResultStatus();
    }

    @PostMapping("/import/approve")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public String approveImportData() {
        return mvImportService.approveData();
    }

    @GetMapping("/export")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public ResponseEntity<?> exportData(@RequestParam(name = "productType", required = false) Long pProductTypeId,
                                        @RequestParam(name = "color", required = false) Long pColorId,
                                        @RequestParam(name = "size", required = false) Long pSizeId,
                                        @RequestParam(name = "fabricType", required = false) Long pFabricTypeId) {
        ProductVariantDTO condition = new ProductVariantDTO();
        condition.setProductTypeId(pProductTypeId);
        condition.setColorId(pColorId);
        condition.setSizeId(pSizeId);
        condition.setFabricTypeId(pFabricTypeId);

        EximResult result = mvExportService.exportToExcel(TemplateExport.IE_LIST_OF_PRODUCTS, condition, false);

        return ResponseEntity.ok().headers(result.getHttpHeaders()).body(result.getContent());
    }
}