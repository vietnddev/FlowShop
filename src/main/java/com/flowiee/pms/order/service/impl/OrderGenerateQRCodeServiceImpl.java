package com.flowiee.pms.order.service.impl;

import com.flowiee.pms.shared.base.BaseEntity;
import com.flowiee.pms.shared.enums.MODULE;
import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.shared.exception.EntityNotFoundException;
import com.flowiee.pms.order.repository.OrderRepository;
import com.flowiee.pms.media.repository.FileStorageRepository;
import com.flowiee.pms.shared.base.GenerateQRCodeService;
import com.flowiee.pms.order.service.OrderGenerateQRCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        //String lvTrackingCode = UUID.randomUUID().toString();
        //lvOrder.setTrackingCode(lvTrackingCode);

        FileStorage lvQRCodeModel = initQRCodeEnt(lvOrder, MODULE.SALES, orderId, null);
        mvFileStorageRepository.save(lvQRCodeModel);

        Path lvGenPath = Paths.get(super.getGenPath(MODULE.SALES) + "/" + lvQRCodeModel.getStorageName());
        generateQRCode(getGenContent(lvOrder.getTrackingCode()), mvQRCodeFormat, lvGenPath);

        //mvOrderRepository.save(lvOrder);
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