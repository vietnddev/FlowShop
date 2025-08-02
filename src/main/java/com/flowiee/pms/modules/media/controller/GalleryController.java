package com.flowiee.pms.modules.media.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.service.ProductImageService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/product")
@Tag(name = "Gallery API", description = "Gallery management")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GalleryController extends BaseController {
    ProductImageService mvProductImageService;

    @Operation(summary = "Find images of all products")
    @GetMapping("/images/all")
    @PreAuthorize("@vldModuleProduct.readGallery(true)")
    public AppResponse<List<FileStorage>> viewGallery() {
        try {
            return AppResponse.success(mvProductImageService.getImageOfProduct(null));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "gallery"), ex);
        }
    }
}