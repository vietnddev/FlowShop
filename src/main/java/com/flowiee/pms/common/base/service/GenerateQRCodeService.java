package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.common.enumeration.FileExtension;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.staff.entity.Account;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public abstract class GenerateQRCodeService {
    @Autowired
    private UserSession userSession;

    protected FileExtension mvQRCodeFormat = FileExtension.PNG;
    protected int mvQRCodeWidth = 200;
    protected int mvQRCodeHeight = 200;

    protected void generateQRCode(String pContent, FileExtension pFormat, Path pPath) throws WriterException, IOException {
        generateQRCode(pContent, pFormat, mvQRCodeWidth, mvQRCodeHeight, pPath);
    }

    protected void generateQRCode(String pContent, FileExtension pFormat, int pWidth, int pHeight, Path pPath) throws WriterException, IOException {
        QRCodeWriter lvQrCodeWriter = new QRCodeWriter();
        BitMatrix lvBitMatrix = lvQrCodeWriter.encode(pContent, BarcodeFormat.QR_CODE, pWidth, pHeight);
        MatrixToImageWriter.writeToPath(lvBitMatrix, pFormat.name(), pPath);
    }

    protected String getCodeType() {
        return FileStorage.QRCODE;
    }

    protected String getImageExtension() {
        return mvQRCodeFormat.getKey();
    }

    protected String getStorageName(long pCurrentTime, String pQRCodeName) {
        return pCurrentTime + "_" + pQRCodeName;
    }

    protected FileStorage initQRCodeEnt(BaseEntity baseEntity, MODULE pModule, Long pOrderId, Long pProductVariantId) {
        return FileStorage.builder()
                .module(pModule.name())
                .originalName(getImageName(baseEntity))
                .customizeName(getImageName(baseEntity))
                .storageName(getImageStorageName())
                .extension(getImageExtension())
                .directoryPath(CommonUtils.getPathDirectory(pModule).substring(CommonUtils.getPathDirectory(pModule).indexOf("uploads")))
                .uploadBy(new Account(userSession.getUserPrincipal().getId()))
                .isActive(false)
                .order(pOrderId != null ? new Order(pOrderId) : null)
                .productDetail(pProductVariantId != null ? new ProductDetail(pProductVariantId) : null)
                .fileType(getCodeType())
                .build();
    }

    protected String getImageName(BaseEntity baseEntity) {
        return null;
    }

    protected String getImageStorageName() {
        return FileUtils.genRandomFileName() + "." + getImageExtension();
    }

    protected String getGenContent(Object pObj) {
        return null;
    }

    protected String getGenPath(MODULE pModule) {
        return CommonUtils.getPathDirectory(pModule);
    }
}