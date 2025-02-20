package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.base.entity.BaseEntity;
import com.flowiee.pms.base.service.GenerateQRCodeService;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.repository.system.FileStorageRepository;
import com.flowiee.pms.service.product.ProductGenerateQRCodeService;
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

        FileStorage lvQRCodeModel = getFileModel(lvProduct, MODULE.PRODUCT, null, productVariantId);
        FileStorage lvQRCodeSaved = mvFileStorageRepository.save(lvQRCodeModel);

        Path lvGenPath = Paths.get(super.getGenPath(MODULE.PRODUCT) + "/" + lvQRCodeSaved.getStorageName());
        generateQRCode(getGenContent(lvProduct), mvQRCodeFormat, lvGenPath);
    }

    @Override
    protected String getImageName(BaseEntity baseEntity) {
        return "qrcode_order_" + baseEntity.getId() + ".png";
    }

    @Override
    protected String getGenContent(BaseEntity baseEntity) {
        return CommonUtils.getServerURL() + "/product/" + baseEntity.getId();
    }
}