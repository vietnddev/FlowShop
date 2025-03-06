package com.flowiee.pms.validate.authorize.storage;

import com.flowiee.pms.base.BaseAuthorize;
import com.flowiee.pms.common.enumeration.ACTION;
import org.springframework.stereotype.Component;

@Component
public class VldModuleStorage extends BaseAuthorize implements IVldModuleStorage {
    @Override
    public boolean dashboard(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DASHBOARD, throwException);
    }

    @Override
    public boolean readStorage(boolean throwException) {
        return super.isAuthorized(ACTION.STG_STORAGE, throwException);
    }

    @Override
    public boolean insertStorage(boolean throwException) {
        return super.isAuthorized(ACTION.STG_STORAGE, throwException);
    }

    @Override
    public boolean updateStorage(boolean throwException) {
        return super.isAuthorized(ACTION.STG_STORAGE, throwException);
    }

    @Override
    public boolean deleteStorage(boolean throwException) {
        return super.isAuthorized(ACTION.STG_STORAGE, throwException);
    }
}