package com.flowiee.pms.order.service;

import com.flowiee.pms.media.entity.FileStorage;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface OrderGenerateQRCodeService {
    FileStorage findOrderQRCode(long orderId);

    void generateOrderQRCode(long orderId) throws IOException, WriterException;
}