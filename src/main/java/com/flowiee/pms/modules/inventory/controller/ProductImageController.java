package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.media.dto.FileDTO;
import com.flowiee.pms.modules.inventory.service.ProductImageService;
import com.flowiee.pms.modules.inventory.service.ProductInfoService;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.modules.media.service.FileStorageService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/product")
@Tag(name = "Product's image API", description = "Quản lý hình ảnh sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductImageController extends BaseController {
    ProductInfoService mvProductInfoService;
    FileStorageService mvFileStorageService;
    ProductImageService mvProductImageService;
    ProductVariantService mvProductVariantService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Upload images of product")
    @PostMapping(value = "/{productId}/uploads-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<FileStorage> uploadImageOfProduct(@RequestParam("file") MultipartFile file, @PathVariable("productId") Long productId) {
        try {
            if (productId <= 0 || mvProductInfoService.findById(productId, true) == null) {
                //throw new BadRequestException();
            }
            if (file.isEmpty()) {
                throw new FileNotFoundException();
            }
            return mvCHelper.success(mvProductImageService.saveImageProduct(file, productId, false));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "image"), ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Upload images of product variant")
    @PostMapping(value = "/{productId}/variant/uploads-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<FileStorage> uploadImageOfProductVariant(@RequestParam("file") MultipartFile file, @PathVariable("productId") Long productVariantId) {
        try {
            if (productVariantId <= 0 || mvProductVariantService.findById(productVariantId, true) == null) {
                throw new BadRequestException();
            }
            if (file.isEmpty()) {
                throw new FileNotFoundException();
            }
            return mvCHelper.success(mvProductImageService.saveImageProductVariant(file, productVariantId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "image"), ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Update product image is use default")
    @PutMapping(value = "/{productId}/active-image/{imageId}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<FileStorage> activeImageOfProduct(@PathVariable("productId") Long productId, @PathVariable("imageId") Long imageId) {
        try {
            if (productId == null || productId <= 0 || imageId == null || imageId <= 0) {
                throw new BadRequestException();
            }
            return mvCHelper.success(mvProductImageService.setImageActiveOfProduct(productId, imageId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "update image of product"), ex);
        }
    }

    @Operation(summary = "Change image of product")
    @PutMapping(value = "/{productId}/change-image/{imageId}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<FileStorage> changeImageOfProduct(@RequestParam("file") MultipartFile file, @PathVariable("imageId") Long imageId) {
        try {
            if (imageId <= 0 || mvFileStorageService.findById(imageId, true) == null) {
                throw new BadRequestException("Image not found");
            }
            if (file.isEmpty()) {
                throw new BadRequestException("File attach not found!");
            }
            return mvCHelper.success(mvProductImageService.changeImageProduct(file, imageId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "update image of product"), ex);
        }
    }

    @Operation(summary = "Update variant image is use default")
    @PutMapping(value = "/variant/{productVariantId}/active-image/{imageId}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<FileStorage> activeImageOfProductVariant(@PathVariable("productVariantId") Long productVariantId, @PathVariable("imageId") Long imageId) {
        try {
            if (productVariantId == null || productVariantId <= 0 || imageId == null || imageId <= 0) {
                throw new BadRequestException();
            }
            return mvCHelper.success(mvProductImageService.setImageActiveOfProductVariant(productVariantId, imageId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "update image of product"), ex);
        }
    }

    @PostMapping(value = "/change-image/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<FileStorage> changeFile(@RequestParam("file") MultipartFile fileUpload, @PathVariable("imageId") Long imageId) {
        try {
            if (imageId <= 0 || mvFileStorageService.findById(imageId, true) == null) {
                throw new BadRequestException();
            }
            if (fileUpload.isEmpty()) {
                throw new FileNotFoundException();
            }
            return mvCHelper.success(mvProductImageService.changeImageProduct(fileUpload, imageId));
        } catch (Exception ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Find images of product")
    @GetMapping("{productId}/images")
    @PreAuthorize("@vldModuleProduct.readProduct(true)")
    public AppResponse<List<FileDTO>> getImagesOfProduct(@PathVariable("productId") Long productId) {
        try {
            List<FileStorage> images = mvProductImageService.getImageOfProduct(productId);
            return mvCHelper.success(FileDTO.fromFileStorages(images), 1, 0, 1, images.size());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "gallery"), ex);
        }
    }
}