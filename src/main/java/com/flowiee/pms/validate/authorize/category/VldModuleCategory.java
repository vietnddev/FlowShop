package com.flowiee.pms.validate.authorize.category;

import com.flowiee.pms.base.BaseAuthorize;
import com.flowiee.pms.common.enumeration.ACTION;
import org.springframework.stereotype.Component;

@Component
public class VldModuleCategory extends BaseAuthorize implements IVldModuleCategory {
    @Override
    public boolean readCategory(boolean throwException) {
        return super.isAuthorized(ACTION.CTG_R, throwException);
    }

    @Override
    public boolean insertCategory(boolean throwException) {
        return super.isAuthorized(ACTION.CTG_C, throwException);
    }

    @Override
    public boolean updateCategory(boolean throwException) {
        return super.isAuthorized(ACTION.CTG_U, throwException);
    }

    @Override
    public boolean deleteCategory(boolean throwException) {
        return super.isAuthorized(ACTION.CTG_D, throwException);
    }

    @Override
    public boolean importCategory(boolean throwException) {
        return super.isAuthorized(ACTION.CTG_I, throwException);
    }
}