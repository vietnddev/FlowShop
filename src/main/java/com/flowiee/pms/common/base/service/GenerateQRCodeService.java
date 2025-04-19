package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.enumeration.FileExtension;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.Path;

public abstract class GenerateQRCodeService extends BaseGenerateService {
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

    @Override
    protected String getCodeType() {
        return FileStorage.QRCODE;
    }

    @Override
    protected String getImageExtension() {
        return mvQRCodeFormat.getKey();
    }
}