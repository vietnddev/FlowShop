package com.flowiee.pms.common.security.authorize.dashboard;

import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.base.BaseAuthorize;
import org.springframework.stereotype.Component;

@Component
public class VldDashboard extends BaseAuthorize implements IVldDashboard {
    @Override
    public boolean readDashboard(boolean throwException) {
        return super.isAuthorized(ACTION.READ_DASHBOARD, throwException);
    }
}