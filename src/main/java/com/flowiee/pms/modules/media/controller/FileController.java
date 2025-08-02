package com.flowiee.pms.modules.media.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.service.*;
import com.flowiee.pms.modules.media.service.FileStorageService;

import com.flowiee.pms.common.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Tag(name = "File API", description = "Quản lý file đính kèm và hình ảnh sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileController extends BaseController {
    FileStorageService    fileService;
    TicketImportService   ticketImportService;
    TicketExportService   ticketExportService;
    ProductInfoService productInfoService;
    ProductImageService productImageService;
    ProductComboService productComboService;
    ProductVariantService productVariantService;
    ProductDamagedService productDamagedService;

    @PostMapping("/uploads/san-pham/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView uploadImageOfProductBase(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable("id") Long productId) throws Exception {
        if (productId <= 0 || productInfoService.findById(productId, true) == null) {
            throw new ResourceNotFoundException("Product not found!");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach not found!");
        }
        productImageService.saveImageProduct(file, productId, false);
        return refreshPage(request);
    }

    @PostMapping("/uploads/bien-the-san-pham/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView uploadImageOfProductVariant(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable("id") Long productVariantId) throws Exception {
        if (productVariantId <= 0 || productVariantService.findById(productVariantId, true) == null) {
            throw new ResourceNotFoundException("Product variant not found!");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach not found!");
        }
        productImageService.saveImageProductVariant(file, productVariantId);
        return refreshPage(request);
    }

    @PostMapping("/file/change-image-sanpham/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView changeFile(@RequestParam("file") MultipartFile file, @PathVariable("id") Long fileId, HttpServletRequest request) {
        if (fileId <= 0 || fileService.findById(fileId, true) == null) {
            throw new ResourceNotFoundException("Image not found");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach not found!");
        }
        productImageService.changeImageProduct(file, fileId);
        return refreshPage(request);
    }

    @PostMapping("/uploads/ticket-import/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView uploadImageOfTicketImport(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable("id") Long ticketImportId) throws Exception {
        if (ticketImportId <= 0 || ticketImportService.findById(ticketImportId, true) == null) {
            throw new ResourceNotFoundException("Ticket import not found!");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach not found!");
        }
        productImageService.saveImageTicketImport(file, ticketImportId);
        return refreshPage(request);
    }

    @PostMapping("/uploads/ticket-export/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView uploadImageOfTicketExport(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable("id") Long ticketExportId) throws Exception {
        if (ticketExportId <= 0 || ticketExportService.findById(ticketExportId, true) == null) {
            throw new ResourceNotFoundException("Ticket export not found!");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach not found!");
        }
        productImageService.saveImageTicketExport(file, ticketExportId);
        return refreshPage(request);
    }

    @PostMapping("/uploads/product-combo/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView uploadImageOfProductCombo(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable("id") Long productComboId) throws Exception {
        if (productComboService.findById(productComboId, true) == null) {
            throw new ResourceNotFoundException("Combo not found!");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach doesn't empty!");
        }
        productImageService.saveImageProductCombo(file, productComboId);
        return refreshPage(request);
    }

    @PostMapping("/uploads/product-damaged/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView uploadImageOfProductDamaged(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable("id") Long productDamagedId) throws Exception {
        if (productDamagedService.findById(productDamagedId, true) == null) {
            throw new ResourceNotFoundException("Product damaged not found!");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach doesn't empty!");
        }
        productImageService.saveImageProductDamaged(file, productDamagedId);
        return refreshPage(request);
    }

    @GetMapping("/uploads/**")//http://host:port/uploads/product/2024/10/3/83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg
    public ResponseEntity<Resource> handleFileRequest(HttpServletRequest request) throws MalformedURLException {
        isAuthenticated();
        //product/2024/10/3/83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg
        String pathToFile = extractPathFromPattern(request);
        //D:\Image\ uploads \ product\2024\10\3\83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg
        Path filePath = Paths.get(FileUtils.getFileUploadPath() + File.separator + pathToFile);
        //URL [file:/D:/Image/uploads/product/2024/10/3/83cbb1e4-37e9-41f1-8892-1d470ceb0f7c.jpg]
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String extractPathFromPattern(HttpServletRequest request) {
        // Lấy URI gốc từ request
        String fullPath = request.getRequestURI();
        // Bỏ đi phần '/uploads/' ở đầu
        return fullPath.substring("/uploads/".length());
    }

    @Operation(summary = "Xóa file", description = "Xóa theo id")
    @DeleteMapping("${app.api.prefix}/file/delete/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public AppResponse<String> delete(@PathVariable("id") Long fileId) {
        return AppResponse.success(fileService.delete(fileId));
    }
}