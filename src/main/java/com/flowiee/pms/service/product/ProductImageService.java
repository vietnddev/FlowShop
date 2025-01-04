package com.flowiee.pms.service.product;

import com.flowiee.pms.entity.system.FileStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    FileStorage findImageActiveOfProductVariant(long pProductVariantId);

    FileStorage changeImageProduct(MultipartFile fileToChange, long fileId);
}