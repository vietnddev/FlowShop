package com.flowiee.pms.service.sales;

import com.flowiee.pms.entity.system.FileStorage;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface OrderGenerateQRCodeService {
    FileStorage findOrderQRCode(long orderId);

    void generateOrderQRCode(long orderId) throws IOException, WriterException;
}