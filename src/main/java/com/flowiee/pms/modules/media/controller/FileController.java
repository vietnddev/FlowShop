package com.flowiee.pms.modules.media.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.enumeration.SystemDir;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.service.*;
import com.flowiee.pms.modules.media.service.FileStorageService;

import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.modules.system.service.FileBackupService;
import com.flowiee.pms.modules.system.service.FileRestoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Tag(name = "File API", description = "Quản lý file đính kèm và hình ảnh sản phẩm")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileController extends BaseController {
    FileStorageService fileService;
    TransactionGoodsService transactionGoodsService;
    ProductInfoService productInfoService;
    ProductImageService productImageService;
    ProductComboService productComboService;
    ProductVariantService productVariantService;
    ProductDamagedService productDamagedService;
    FileBackupService fileBackupService;
    FileRestoreService fileRestoreService;

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

    @PostMapping("/uploads/transaction-goods/{id}")
    @PreAuthorize("@vldModuleProduct.updateImage(true)")
    public ModelAndView uploadImageForTransactionGoodsImport(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable("id") Long ticketImportId) throws Exception {
        if (ticketImportId <= 0 || transactionGoodsService.findEntById(ticketImportId, true) == null) {
            throw new ResourceNotFoundException("Transaction goods invalid!");
        }
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File attach not found!");
        }
        productImageService.saveImageTransactionGoods(file, ticketImportId);
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
        Path filePath = Paths.get(FileUtils.getSystemDir(SystemDir.UPLOAD) + File.separator + pathToFile);
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

    @GetMapping("/file/backup")
    public ResponseEntity<Resource> backup() throws IOException {
        File zipFile = fileBackupService.createBackupZip();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFile.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipFile.length())
                .body(resource);
    }

    @PostMapping("/file/restore")
    public ResponseEntity<String> restore(@RequestParam("backupZip") MultipartFile backupZip) {
        try {
            fileRestoreService.restoreBackup(backupZip);
            return ResponseEntity.ok("Restore successful");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Restore failed: " + e.getMessage());
        }
    }
}