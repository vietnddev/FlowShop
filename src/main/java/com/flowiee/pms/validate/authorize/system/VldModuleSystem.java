package com.flowiee.pms.validate.authorize.system;

import com.flowiee.pms.base.auth.BaseAuthorize;
import com.flowiee.pms.common.enumeration.ACTION;
import org.springframework.stereotype.Component;

@Component
public class VldModuleSystem extends BaseAuthorize implements IVldModuleSystem {
    @Override
    public boolean readPermission(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ROLE_R, throwException);
    }

    @Override
    public boolean readAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACC_R, throwException);
    }

    @Override
    public boolean insertAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACC_C, throwException);
    }

    @Override
    public boolean updateAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACC_U, throwException);
    }

    @Override
    public boolean deleteAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACC_D, throwException);
    }

    @Override
    public boolean readLog(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_LOG_R, throwException);
    }

    @Override
    public boolean readConfig(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_CNF_R, throwException);
    }

    @Override
    public boolean updateConfig(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_CNF_U, throwException);
    }

    @Override
    public boolean readGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_R, throwException);
    }

    @Override
    public boolean insertGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_C, throwException);
    }

    @Override
    public boolean updateGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_U, throwException);
    }

    @Override
    public boolean deleteGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_D, throwException);
    }

    @Override
    public boolean readBranch(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_BRCH_R, throwException);
    }

    @Override
    public boolean insertBranch(boolean throwException) {
        vldAdminRole();
        return super.isAuthorized(ACTION.SYS_BRCH_C, throwException);
    }

    @Override
    public boolean updateBranch(boolean throwException) {
        vldAdminRole();
        return super.isAuthorized(ACTION.SYS_BRCH_U, throwException);
    }

    @Override
    public boolean deleteBranch(boolean throwException) {
        vldAdminRole();
        return super.isAuthorized(ACTION.SYS_BRCH_D, throwException);
    }

    @Override
    public boolean readLeaveRequests(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_BRCH_D, throwException);
    }

    @Override
    public boolean approveLeaveRequest(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_BRCH_D, throwException);
    }

    @Override
    public boolean rejectLeaveRequest(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_BRCH_D, throwException);
    }
}