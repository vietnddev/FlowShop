package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.base.entity.BaseEntity;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.repository.system.FileStorageRepository;
import com.flowiee.pms.base.service.GenerateQRCodeService;
import com.flowiee.pms.service.sales.OrderGenerateQRCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderGenerateQRCodeServiceImpl extends GenerateQRCodeService implements OrderGenerateQRCodeService {
    private final FileStorageRepository mvFileStorageRepository;
    private final OrderRepository mvOrderRepository;

    @Override
    public FileStorage findOrderQRCode(long orderId) {
        return mvFileStorageRepository.findQRCodeOfOrder(orderId);
    }

    @Override
    public void generateOrderQRCode(long orderId) throws IOException, WriterException {
        Order lvOrder = mvOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(new Object[] {"order"}, null, null));
        String lvTrackingCode = UUID.randomUUID().toString();
        lvOrder.setTrackingCode(lvTrackingCode);

        FileStorage lvQRCodeModel = getFileModel(lvOrder, MODULE.SALES, orderId, null);
        mvFileStorageRepository.save(lvQRCodeModel);

        Path lvGenPath = Paths.get(super.getGenPath(MODULE.SALES) + "/" + lvQRCodeModel.getStorageName());
        generateQRCode(getGenContent(lvTrackingCode), mvQRCodeFormat, lvGenPath);

        mvOrderRepository.save(lvOrder);
    }

    @Override
    protected String getImageName(BaseEntity baseEntity) {
        return "qrcode_order_" + baseEntity.getId() + ".png";
    }

    @Override
    protected String getGenContent(Object pObj) {
        return CommonUtils.getServerURL() + "/sls/order/tracking?code=" + pObj.toString();
    }
}