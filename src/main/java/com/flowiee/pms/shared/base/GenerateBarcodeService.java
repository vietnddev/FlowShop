package com.flowiee.pms.shared.base;

import com.flowiee.pms.shared.enums.FileExtension;
import com.flowiee.pms.shared.enums.MODULE;
import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.shared.util.FileUtils;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.shared.util.SecurityUtils;
import com.flowiee.pms.system.entity.Account;
import com.google.zxing.WriterException;

import java.io.IOException;

public abstract class GenerateBarcodeService {
    protected FileExtension mvBarcodeFormat = FileExtension.PNG;
    protected int mvBarcodeWidth = 300;
    protected int mvBarcodeHeight = 100;

    public abstract FileStorage generateBarcode(Long pProductVariantId) throws WriterException, IOException;

    public abstract FileStorage generateBarcode(Long pProductVariantId, int width, int height) throws WriterException, IOException;

    protected String getStorageName(long pCurrentTime, String pQRCodeName) {
        return pCurrentTime + "_" + pQRCodeName;
    }

    protected FileStorage initBarcodeEnt(BaseEntity baseEntity, MODULE pModule, Long pOrderId, Long pProductVariantId) {
        return FileStorage.builder()
                .module(pModule.name())
                .originalName(getImageName(baseEntity))
                .customizeName(getImageName(baseEntity))
                .storageName(getImageStorageName())
                .extension(getImageExtension())
                .directoryPath(CommonUtils.getPathDirectory(pModule).substring(CommonUtils.getPathDirectory(pModule).indexOf("uploads")))
                .uploadBy(new Account(SecurityUtils.getCurrentUser().getId()))
                .isActive(false)
                .order(pOrderId != null ? new Order(pOrderId) : null)
                .productDetail(pProductVariantId != null ? new ProductDetail(pProductVariantId) : null)
                .fileType(getCodeType())
                .build();
    }

    protected String getCodeType() {
        return null;
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

    protected String getImageExtension() {
        return null;
    }
}