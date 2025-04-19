package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.system.service.ConfigService;
import com.flowiee.pms.modules.system.service.SendOperatorNotificationService;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.product.repository.ProductDetailRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.log.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductQuantityServiceImpl extends BaseService implements ProductQuantityService {
    private final SendOperatorNotificationService sendOperatorNotificationService;
    private final ProductDetailRepository mvProductVariantRepository;
    private final SystemLogService mvSystemLogService;
    private final ConfigService mvConfigService;

    @Transactional
    @Override
    public void updateProductVariantQuantityIncrease(Integer pQuantity, Long pProductVariantId) {
        this.updateProductVariantQuantity(pQuantity, pProductVariantId, "I");
    }

    @Transactional
    @Override
    public void updateProductVariantQuantityDecrease(Integer pQuantity, Long pProductVariantId) {
        this.updateProductVariantQuantity(pQuantity, pProductVariantId, "D");
    }

    private void updateProductVariantQuantity(Integer pQuantity, Long productVariantId, String type) {
        ProductDetail lvProductDetail = mvProductVariantRepository.findById(productVariantId).orElseThrow(() -> new AppException("Product not found!"));
        Integer lvLowStockThreshold = lvProductDetail.getLowStockThreshold();
        int lvCurrentQuantity = lvProductDetail.getStorageQty();
        try {
            if ("I".equals(type)) {
                mvProductVariantRepository.updateQuantityIncrease(pQuantity, productVariantId);
            } else if ("D".equals(type)) {
                if (lvCurrentQuantity < pQuantity)
                    throw new BadRequestException("Hàng tồn kho không đủ số lượng!");

                lvProductDetail.setStorageQty(lvCurrentQuantity - pQuantity);
                lvProductDetail.setSoldQty(lvProductDetail.getSoldQty() + pQuantity);
                ProductDetail productDetailUpdated = mvProductVariantRepository.save(lvProductDetail);

                if (lvLowStockThreshold != null && productDetailUpdated.getStorageQty() <= lvLowStockThreshold) {
                    if (SysConfigUtils.isYesOption(ConfigCode.lowStockAlert)) {
                        sendOperatorNotificationService.notifyWarningLowStock(productDetailUpdated);
                    }
                }

                //Hết hàng
                if (productDetailUpdated.getAvailableSalesQty() == 0) {
                    productDetailUpdated.setStatus(ProductStatus.OOS);
                    productDetailUpdated.setOutOfStockDate(LocalDateTime.now());
                    mvProductVariantRepository.save(productDetailUpdated);
                }
            }
            mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, "Cập nhật số lượng sản phẩm", "productVariantId = " + productVariantId);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "product quantity"), ex);
        }
    }
}