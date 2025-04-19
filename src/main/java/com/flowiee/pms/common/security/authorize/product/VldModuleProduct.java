package com.flowiee.pms.common.security.authorize.product;

import com.flowiee.pms.common.base.BaseAuthorize;
import com.flowiee.pms.common.enumeration.ACTION;
import org.springframework.stereotype.Component;

@Component
public class VldModuleProduct extends BaseAuthorize implements IVldModuleProduct {
    @Override
    public boolean readProduct(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_R, throwException);
    }

    @Override
    public boolean insertProduct(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_C, throwException);
    }

    @Override
    public boolean updateProduct(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_U, throwException);
    }

    @Override
    public boolean deleteProduct(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_D, throwException);
    }

    @Override
    public boolean importProduct(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_I, throwException);
    }

    @Override
    public boolean readCombo(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CBO_R, throwException);
    }

    @Override
    public boolean insertCombo(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CBO_C, throwException);
    }

    @Override
    public boolean updateCombo(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CBO_U, throwException);
    }

    @Override
    public boolean deleteCombo(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CBO_D, throwException);
    }

    @Override
    public boolean updateImage(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_U, throwException);
    }

    @Override
    public boolean priceManagement(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_PRICE, throwException);
    }

    @Override
    public boolean report(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PRD_RPT, throwException);
    }

    @Override
    public boolean readGallery(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_GAL_R, throwException);
    }

    @Override
    public boolean readMaterial(boolean throwException) {
        return super.isAuthorized(ACTION.STG_MAT_R, throwException);
    }

    @Override
    public boolean insertMaterial(boolean throwException) {
        return super.isAuthorized(ACTION.STG_MAT_C, throwException);
    }

    @Override
    public boolean updateMaterial(boolean throwException) {
        return super.isAuthorized(ACTION.STG_MAT_U, throwException);
    }

    @Override
    public boolean deleteMaterial(boolean throwException) {
        return super.isAuthorized(ACTION.STG_MAT_D, throwException);
    }

    @Override
    public boolean updateReview(boolean throwException) {
        return true;
    }

    @Override
    public boolean deleteReview(boolean throwException) {
        return true;
    }
}