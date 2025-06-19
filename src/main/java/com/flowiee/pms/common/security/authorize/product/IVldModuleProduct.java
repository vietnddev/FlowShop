package com.flowiee.pms.common.security.authorize.product;

interface IVldModuleProduct {
    boolean readProduct(boolean throwException);

    boolean insertProduct(boolean throwException);

    boolean updateProduct(boolean throwException);

    boolean deleteProduct(boolean throwException);

    boolean importProduct(boolean throwException);

    boolean readCombo(boolean throwException);

    boolean insertCombo(boolean throwException);

    boolean updateCombo(boolean throwException);

    boolean deleteCombo(boolean throwException);

    boolean readMaterial(boolean throwException);

    boolean insertMaterial(boolean throwException);

    boolean updateMaterial(boolean throwException);

    boolean deleteMaterial(boolean throwException);

    boolean updateImage(boolean throwException);

    boolean priceManagement(boolean throwException);

    boolean report(boolean throwException);
    
    boolean readGallery(boolean throwException);

    boolean updateReview(boolean throwException);

    boolean deleteReview(boolean throwException);
}