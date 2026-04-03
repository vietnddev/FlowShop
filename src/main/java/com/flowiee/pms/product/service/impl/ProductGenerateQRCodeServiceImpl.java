package com.flowiee.pms.product.service.impl;

import com.flowiee.pms.shared.base.BaseEntity;
import com.flowiee.pms.shared.base.GenerateQRCodeService;
import com.flowiee.pms.shared.enums.MODULE;
import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.shared.exception.EntityNotFoundException;
import com.flowiee.pms.product.repository.ProductDetailRepository;
import com.flowiee.pms.media.repository.FileStorageRepository;
import com.flowiee.pms.product.service.ProductGenerateQRCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ProductGenerateQRCodeServiceImpl extends GenerateQRCodeService implements ProductGenerateQRCodeService {
    private final ProductDetailRepository mvProductVariantRepository;
    private final FileStorageRepository mvFileStorageRepository;

    @Override
    public void generateProductVariantQRCode(long productVariantId) throws IOException, WriterException {
        ProductDetail lvProduct = mvProductVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new EntityNotFoundException(new Object[] {"product variant"}, null, null));

        FileStorage lvQRCodeModel = initQRCodeEnt(lvProduct, MODULE.PRODUCT, null, productVariantId);
        FileStorage lvQRCodeSaved = mvFileStorageRepository.save(lvQRCodeModel);

        Path lvGenPath = Paths.get(super.getGenPath(MODULE.PRODUCT) + "/" + lvQRCodeSaved.getStorageName());
        generateQRCode(getGenContent(lvProduct), mvQRCodeFormat, lvGenPath);
    }

    @Override
    protected String getImageName(BaseEntity baseEntity) {
        return "qrcode_order_" + baseEntity.getId() + ".png";
    }

    @Override
    protected String getGenContent(Object pObj) {
        return CommonUtils.getServerURL() + "/product/" + null;
    }
}