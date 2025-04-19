package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.security.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseGenerateService extends BaseService {
    @Autowired
    private UserSession userSession;

    protected String getStorageName(long pCurrentTime, String pQRCodeName) {
        return pCurrentTime + "_" + pQRCodeName;
    }

    protected FileStorage getFileModel(BaseEntity baseEntity, MODULE pModule, Long pOrderId, Long pProductVariantId) {
        return FileStorage.builder()
                .module(pModule.name())
                .originalName(getImageName(baseEntity))
                .customizeName(getImageName(baseEntity))
                .storageName(getImageStorageName())
                .extension(getImageExtension())
                .directoryPath(CommonUtils.getPathDirectory(pModule).substring(CommonUtils.getPathDirectory(pModule).indexOf("uploads")))
                .account(new Account(userSession.getUserPrincipal().getId()))
                .isActive(false)
                .order(pOrderId != null ? new Order(pOrderId) : null)
                .productDetail(pProductVariantId != null ? new ProductDetail(pProductVariantId) : null)
                .fileType(getCodeType())
                .build();
    }

    protected String getCodeType() {
        return null;
    }

    protected String getImageName(BaseEntity baseEntity) {
        return null;
    }

    protected String getImageStorageName() {
        return FileUtils.genRandomFileName() + "." + getImageExtension();
    }

    protected String getGenContent(Object pObj) {
        return null;
    }

    protected String getGenPath(MODULE pModule) {
        return CommonUtils.getPathDirectory(pModule);
    }

    protected String getImageExtension() {
        return null;
    }
}