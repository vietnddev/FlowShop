package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.common.base.service.GenerateQRCodeService;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.product.repository.ProductDetailRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
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
    protected String getGenContent(Object pObj) {
        return CommonUtils.getServerURL() + "/product/" + null;
    }
}