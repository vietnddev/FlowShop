package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.service.system.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.prefix}/file")
@Tag(name = "File API", description = "Quản lý file đính kèm và hình ảnh sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileStorageController extends BaseController {
    FileStorageService fileService;

    @Operation(summary = "Xóa file", description = "Xóa theo id")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<String> delete(@PathVariable("id") Long fileId) {
        return success(fileService.delete(fileId));
    }
}