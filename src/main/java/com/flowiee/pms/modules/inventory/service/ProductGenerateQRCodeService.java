package com.flowiee.pms.modules.inventory.service;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface ProductGenerateQRCodeService {
    void generateProductVariantQRCode(long productVariantId) throws IOException, WriterException;
}