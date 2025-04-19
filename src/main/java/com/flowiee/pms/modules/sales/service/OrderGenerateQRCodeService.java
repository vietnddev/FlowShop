package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.modules.media.entity.FileStorage;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface OrderGenerateQRCodeService {
    FileStorage findOrderQRCode(long orderId);

    void generateOrderQRCode(long orderId) throws IOException, WriterException;
}