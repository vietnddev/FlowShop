package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.StartUp;
import com.flowiee.pms.modules.inventory.entity.*;
import com.flowiee.pms.modules.inventory.service.*;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.inventory.dto.ProductComboDTO;
import com.flowiee.pms.modules.inventory.repository.ProductDamagedRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.media.service.FileStorageService;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.FileUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    TransactionGoodsService mvTransactionGoodsService;
    FileStorageService mvFileStorageService;
    ProductComboService mvProductComboService;
    ProductVariantService mvProductVariantService;
    FileStorageRepository mvFileStorageRepository;
    ProductDamagedRepository mvProductDamagedRepository;
    UserSession userSession;

    private Logger logger = LoggerFactory.getLogger(getClass());

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
        fileInfo.setProductDetail(new ProductDetail(productDetail.getId()));
        FileStorage imageSaved = mvFileStorageService.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.PRODUCT) + "/" + imageSaved.getStorageName());
        fileUpload.transferTo(path);

        return imageSaved;
    }

    @Override
    public FileStorage saveImageProductCombo(MultipartFile fileUpload, long productComboId) throws IOException {
        ProductComboDTO productCombo = mvProductComboService.findById(productComboId, true);

        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.PRODUCT.name(), null);
        fileInfo.setProductCombo(new ProductCombo(productCombo.getId()));
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
    public FileStorage saveImageTransactionGoods(MultipartFile fileUpload, long ticketImportId) throws IOException {
        TransactionGoods lvTransactionGoods = mvTransactionGoodsService.findEntById(ticketImportId, true);

        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.STORAGE.name(), null);
        fileInfo.setTransactionGoods(lvTransactionGoods);
        FileStorage imageSaved = mvFileStorageService.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.STORAGE) + "/" + imageSaved.getStorageName());
        fileUpload.transferTo(path);

        return imageSaved;
    }

    @Override
    public FileStorage setImageActiveOfProduct(Long pProductId, Long pImageId) {
        FileStorage imageToActive = mvFileStorageService.findById(pImageId, true);

        //Bỏ image default hiện tại
        Optional<FileStorage> imageActiving = mvFileStorageRepository.findProductImageActive(pProductId, null);
        if (imageActiving.isPresent()) {
            imageActiving.get().setActive(false);
            mvFileStorageRepository.save(imageActiving.get());
        }
        //Active lại image theo id được truyền vào
        imageToActive.setActive(true);
        return mvFileStorageRepository.save(imageToActive);
    }

    @Override
    public FileStorage setImageActiveOfProductVariant(Long pProductVariantId, Long pImageId) {
        FileStorage imageToActive = mvFileStorageService.findById(pImageId, true);

        //Bỏ image default hiện tại
        Optional<FileStorage> imageActivating = mvFileStorageRepository.findProductImageActive(null, pProductVariantId);
        if (imageActivating.isPresent()) {
            imageActivating.get().setActive(false);
            mvFileStorageRepository.save(imageActivating.get());
        }
        //Active lại image theo id được truyền vào
        imageToActive.setActive(true);
        return mvFileStorageRepository.save(imageToActive);
    }

    @Override
    public FileStorage findImageActiveOfProduct(long pProductId) {
        return mvFileStorageRepository.findProductImageActive(pProductId, null).orElse(null);
    }

    @Override
    public FileStorage findImageActiveOfProductVariant(long pProductVariantId) {
        return mvFileStorageRepository.findProductImageActive(null, pProductVariantId).orElse(null);
    }

    @Override
    public Map<Long, FileStorage> getImageActiveOfProductVariants(List<Long> pProductVariantIds) {
        if (CollectionUtils.isEmpty(pProductVariantIds)) {
            return Map.of();
        }

        List<FileStorage> lvImageList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < pProductVariantIds.size(); i += batchSize) {
            List<Long> batch = new ArrayList<>(pProductVariantIds.subList(i, Math.min(i + batchSize, pProductVariantIds.size())));
            lvImageList.addAll(mvFileStorageRepository.findProductVariantImageActive(batch));
        }

        //Map<productId, imageUrl>
        Map<Long, FileStorage> lvImageMap = lvImageList.stream()
                .filter(img -> img.getProductDetail() != null)
                .collect(Collectors.toMap(
                        img -> img.getProductDetail().getId(),
                        img -> img,
                        (existing, replacement) -> existing
                ));

        return lvImageMap;
    }

    @Transactional
    @Override
    public FileStorage changeImageProduct(MultipartFile fileAttached, long fileId) {
        FileStorage fileOptional = mvFileStorageService.findById(fileId, true);

        FileStorage fileToChange = fileOptional;
        //Delete file vật lý cũ
        try {
            File file = new File(StartUp.getResourceUploadPath() + FileUtils.getImageUrl(fileToChange, true));
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
        fileToChange.setAccount(userSession.getUserPrincipal().getEntity());
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