package com.flowiee.pms.service.product;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface ProductGenerateQRCodeService {
    void generateProductVariantQRCode(long productVariantId) throws IOException, WriterException;
}