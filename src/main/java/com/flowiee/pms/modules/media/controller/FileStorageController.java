package com.flowiee.pms.modules.media.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.media.service.FileStorageService;
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
    ControllerHelper mvCHelper;

    @Operation(summary = "Xóa file", description = "Xóa theo id")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<String> delete(@PathVariable("id") Long fileId) {
        return mvCHelper.success(fileService.delete(fileId));
    }
}