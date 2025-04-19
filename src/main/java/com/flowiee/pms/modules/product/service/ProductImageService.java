package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.modules.media.entity.FileStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductImageService {
    List<FileStorage> getImageOfProduct(Long pProductId);

    List<FileStorage> getImageOfProductVariant(Long pProductVariantId);

    FileStorage saveImageProduct(MultipartFile fileUpload, long pProductId, boolean makeActive) throws IOException;

    FileStorage saveImageProductVariant(MultipartFile fileUpload, long pProductId) throws IOException;

    FileStorage saveImageProductCombo(MultipartFile fileUpload, long productComboId) throws IOException;

    FileStorage saveImageProductDamaged(MultipartFile fileUpload, long productDamagedId) throws IOException;

    FileStorage saveImageTicketImport(MultipartFile fileUpload, long ticketImportId) throws IOException;

    FileStorage saveImageTicketExport(MultipartFile fileUpload, long ticketExportId) throws IOException;

    FileStorage setImageActiveOfProduct(Long pProductId, Long pImageId);

    FileStorage setImageActiveOfProductVariant(Long pProductVariantId, Long pImageId);

    FileStorage findImageActiveOfProduct(long pProductId);

    Map<Long, FileStorage> getImageActiveOfProductVariants(List<Long> pProductVariantIds);

    FileStorage changeImageProduct(MultipartFile fileToChange, long fileId);
}