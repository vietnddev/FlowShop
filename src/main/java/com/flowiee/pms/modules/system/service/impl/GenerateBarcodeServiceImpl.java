package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.common.base.service.GenerateBarcodeService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.FileExtension;
import com.flowiee.pms.common.enumeration.MODULE;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class GenerateBarcodeServiceImpl extends GenerateBarcodeService {
    private final ProductDetailRepository mvProductVariantRepository;
    private final FileStorageRepository mvFileStorageRepository;

    private byte[] generateBarcode(String pContent, FileExtension pFormat, Path pPath) throws WriterException, IOException {
        return generateBarcode(pContent, pFormat, mvBarcodeWidth, mvBarcodeHeight, pPath);
    }

    private byte[] generateBarcode(String pContent, FileExtension pFormat, int pWidth, int pHeight, Path pPath) throws WriterException, IOException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(pContent, BarcodeFormat.CODE_128, pWidth, pHeight);
        MatrixToImageWriter.writeToPath(bitMatrix, pFormat.name(), pPath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, mvBarcodeFormat.name(), outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public FileStorage generateBarcode(Long pProductVariantId) throws WriterException, IOException {
        return generateBarcode(pProductVariantId, mvBarcodeWidth, mvBarcodeHeight);
    }

    @Override
    public FileStorage generateBarcode(Long pProductVariantId, int width, int height) throws WriterException, IOException {
        ProductDetail lvProduct = mvProductVariantRepository.findById(pProductVariantId)
                .orElseThrow(() -> new EntityNotFoundException(new Object[] {"product variant"}, null, null));

        FileStorage lvBarcodeModel = initBarcodeEnt(lvProduct, MODULE.PRODUCT, null, pProductVariantId);
        FileStorage lvBarcodeSaved = mvFileStorageRepository.save(lvBarcodeModel);

        Path lvGenPath = Paths.get(super.getGenPath(MODULE.PRODUCT) + "/" + lvBarcodeSaved.getStorageName());
        byte[] contentByteArr = generateBarcode(getGenContent(lvProduct), mvBarcodeFormat, lvGenPath);
        String contentBase64 = Base64.getEncoder().encodeToString(contentByteArr);

        lvBarcodeSaved.setExtension(mvBarcodeFormat.getKey());
        lvBarcodeSaved.setContent(contentByteArr);
        lvBarcodeSaved.setContentBase64(contentBase64);

        return mvFileStorageRepository.save(lvBarcodeSaved);
    }

    @Override
    protected String getCodeType() {
        return FileStorage.BARCODE;
    }

    @Override
    protected String getImageName(BaseEntity baseEntity) {
        return "barcode_productVariant_" + baseEntity.getId() + "." + mvBarcodeFormat.getKey();
    }

    @Override
    protected String getGenContent(Object pObj) {
        return CommonUtils.getServerURL() + "/product/" + null;
    }

    @Override
    protected String getImageExtension() {
        return mvBarcodeFormat.getKey();
    }
}