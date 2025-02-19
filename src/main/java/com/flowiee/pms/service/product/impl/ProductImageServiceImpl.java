package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.system.Core;
import com.flowiee.pms.entity.product.ProductCombo;
import com.flowiee.pms.entity.product.ProductDamaged;
import com.flowiee.pms.entity.sales.TicketExport;
import com.flowiee.pms.entity.sales.TicketImport;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.repository.product.ProductDamagedRepository;
import com.flowiee.pms.service.product.ProductComboService;
import com.flowiee.pms.service.system.FileStorageService;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import com.flowiee.pms.repository.system.FileStorageRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductImageService;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.service.sales.TicketExportService;
import com.flowiee.pms.service.sales.TicketImportService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.FileUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductImageServiceImpl extends BaseService implements ProductImageService {
    FileStorageService mvFileStorageService;
    TicketExportService mvTicketExportService;
    TicketImportService mvTicketImportService;
    ProductComboService mvProductComboService;
    ProductVariantService mvProductVariantService;
    FileStorageRepository mvFileStorageRepository;
    ProductDamagedRepository mvProductDamagedRepository;

    @Override
    public List<FileStorage> getImageOfProduct(Long productId) {
        return mvFileStorageRepository.findAllImages(MODULE.PRODUCT.name(), productId, null);
    }

    @Override
    public List<FileStorage> getImageOfProductVariant(Long productDetailId) {
        return mvFileStorageRepository.findAllImages(MODULE.PRODUCT.name(), null, productDetailId);
    }

    @Override
    @Transactional
    public FileStorage saveImageProduct(MultipartFile fileUpload, long pProductId, boolean makeActive) throws IOException {
        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.PRODUCT.name(), pProductId);
        fileInfo.setActive(makeActive);
        return mvFileStorageService.save(fileInfo);
    }

    @Override
    @Transactional
    public FileStorage saveImageProductVariant(MultipartFile fileUpload, long pProductVariantId) throws IOException {
        ProductVariantDTO productDetail = mvProductVariantService.findById(pProductVariantId, true);

        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.PRODUCT.name(), productDetail.getProductId());
        fileInfo.setProductDetail(productDetail);
        FileStorage imageSaved = mvFileStorageService.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.PRODUCT) + "/" + imageSaved.getStorageName());
        fileUpload.transferTo(path);

        return imageSaved;
    }

    @Override
    public FileStorage saveImageProductCombo(MultipartFile fileUpload, long productComboId) throws IOException {
        ProductCombo productCombo = mvProductComboService.findById(productComboId, true);

        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.PRODUCT.name(), null);
        fileInfo.setProductCombo(productCombo);
        FileStorage imageSaved = mvFileStorageService.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.PRODUCT) + "/" + imageSaved.getStorageName());
        fileUpload.transferTo(path);

        return imageSaved;
    }

    @Override
    public FileStorage saveImageProductDamaged(MultipartFile fileUpload, long productDamagedId) throws IOException {
        Optional<ProductDamaged> productDamaged = mvProductDamagedRepository.findById(productDamagedId);
        if (productDamaged.isEmpty()) {
            throw new BadRequestException();
        }

        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.PRODUCT.name(), null);
        fileInfo.setProductDamaged(productDamaged.get());
        FileStorage imageSaved = mvFileStorageService.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.PRODUCT) + "/" + imageSaved.getStorageName());
        fileUpload.transferTo(path);

        return imageSaved;
    }

    @Override
    public FileStorage saveImageTicketImport(MultipartFile fileUpload, long ticketImportId) throws IOException {
        TicketImport ticketImport = mvTicketImportService.findById(ticketImportId, true);

        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.STORAGE.name(), null);
        fileInfo.setTicketImport(ticketImport);
        FileStorage imageSaved = mvFileStorageService.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.STORAGE) + "/" + imageSaved.getStorageName());
        fileUpload.transferTo(path);

        return imageSaved;
    }

    @Override
    public FileStorage saveImageTicketExport(MultipartFile fileUpload, long ticketExportId) throws IOException {
        TicketExport ticketExport = mvTicketExportService.findById(ticketExportId, true);

        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.STORAGE.name(), null);
        fileInfo.setTicketExport(ticketExport);
        FileStorage imageSaved = mvFileStorageService.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.STORAGE) + "/" + imageSaved.getStorageName());
        fileUpload.transferTo(path);

        return imageSaved;
    }

    @Override
    public FileStorage setImageActiveOfProduct(Long pProductId, Long pImageId) {
        FileStorage imageToActive = mvFileStorageService.findById(pImageId, true);

        //Bỏ image default hiện tại
        FileStorage imageActiving = mvFileStorageRepository.findActiveImage(pProductId, null);
        if (imageActiving != null) {
            imageActiving.setActive(false);
            mvFileStorageRepository.save(imageActiving);
        }
        //Active lại image theo id được truyền vào
        imageToActive.setActive(true);
        return mvFileStorageRepository.save(imageToActive);
    }

    @Override
    public FileStorage setImageActiveOfProductVariant(Long pProductVariantId, Long pImageId) {
        FileStorage imageToActive = mvFileStorageService.findById(pImageId, true);

        //Bỏ image default hiện tại
        FileStorage imageActivating = mvFileStorageRepository.findActiveImage(null, pProductVariantId);
        if (ObjectUtils.isNotEmpty(imageActivating)) {
            imageActivating.setActive(false);
            mvFileStorageRepository.save(imageActivating);
        }
        //Active lại image theo id được truyền vào
        imageToActive.setActive(true);
        return mvFileStorageRepository.save(imageToActive);
    }

    @Override
    public FileStorage findImageActiveOfProduct(long pProductId) {
        return mvFileStorageRepository.findActiveImage(pProductId, null);
    }

    @Override
    public FileStorage findImageActiveOfProductVariant(long pProductVariantId) {
        return mvFileStorageRepository.findActiveImage(null, pProductVariantId);
    }

    @Transactional
    @Override
    public FileStorage changeImageProduct(MultipartFile fileAttached, long fileId) {
        FileStorage fileOptional = mvFileStorageService.findById(fileId, true);

        FileStorage fileToChange = fileOptional;
        //Delete file vật lý cũ
        try {
            File file = new File(Core.getResourceUploadPath() + FileUtils.getImageUrl(fileToChange, true));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error("File not found!", e);
        }
        //Update thông tin file mới
        fileToChange.setOriginalName(fileAttached.getOriginalFilename());
        fileToChange.setCustomizeName(fileAttached.getOriginalFilename());
        fileToChange.setStorageName(FileUtils.genRandomFileName());
        fileToChange.setFileSize(fileAttached.getSize());
        fileToChange.setExtension(FileUtils.getFileExtension(fileAttached.getOriginalFilename()));
        fileToChange.setContentType(fileAttached.getContentType());
        fileToChange.setDirectoryPath(CommonUtils.getPathDirectory(MODULE.PRODUCT).substring(CommonUtils.getPathDirectory(MODULE.PRODUCT).indexOf("uploads")));
        fileToChange.setAccount(CommonUtils.getUserPrincipal().toEntity());
        FileStorage imageSaved = mvFileStorageRepository.save(fileToChange);

        //Lưu file mới vào thư mục chứa file upload
        try {
            Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.PRODUCT) + "/" + imageSaved.getStorageName());
            fileAttached.transferTo(path);
        } catch (Exception e) {
            logger.error("Lưu file change vào thư mục chứa file upload thất bại! \n" + e.getCause().getMessage(), e);
        }

        return imageSaved;
    }
}