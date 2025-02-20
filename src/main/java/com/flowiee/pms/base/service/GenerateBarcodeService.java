package com.flowiee.pms.base.service;

import com.flowiee.pms.common.enumeration.FileExtension;
import com.flowiee.pms.entity.system.FileStorage;
import com.google.zxing.WriterException;

import java.io.IOException;

public abstract class GenerateBarcodeService extends BaseGenerateService {
    protected FileExtension mvBarcodeFormat = FileExtension.PNG;
    protected int mvBarcodeWidth = 300;
    protected int mvBarcodeHeight = 100;

    public abstract FileStorage generateBarcode(Long pProductVariantId) throws WriterException, IOException;

    public abstract FileStorage generateBarcode(Long pProductVariantId, int width, int height) throws WriterException, IOException;
}