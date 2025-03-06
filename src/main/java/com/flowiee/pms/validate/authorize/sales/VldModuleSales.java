package com.flowiee.pms.validate.authorize.sales;

import com.flowiee.pms.base.BaseAuthorize;
import com.flowiee.pms.common.enumeration.ACTION;
import org.springframework.stereotype.Component;

@Component
public class VldModuleSales extends BaseAuthorize implements IVldModuleSales {
    @Override
    public boolean dashboard(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DASHBOARD, throwException);
    }

    @Override
    public boolean importGoods(boolean throwException) {
        return super.isAuthorized(ACTION.STG_TICKET_IM, throwException);
    }

    @Override
    public boolean exportGoods(boolean throwException) {
        return super.isAuthorized(ACTION.STG_TICKET_EX, throwException);
    }

    @Override
    public boolean readOrder(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_ORD_R, throwException);
    }

    @Override
    public boolean insertOrder(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_ORD_C, throwException);
    }

    @Override
    public boolean updateOrder(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_ORD_U, throwException);
    }

    @Override
    public boolean deleteOrder(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_ORD_D, throwException);
    }

    @Override
    public boolean readCustomer(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CUS_R, throwException);
    }

    @Override
    public boolean insertCustomer(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CUS_C, throwException);
    }

    @Override
    public boolean updateCustomer(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CUS_U, throwException);
    }

    @Override
    public boolean deleteCustomer(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_CUS_D, throwException);
    }

    @Override
    public boolean readVoucher(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_VOU_R, throwException);
    }

    @Override
    public boolean insertVoucher(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_VOU_C, throwException);
    }

    @Override
    public boolean updateVoucher(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_VOU_U, throwException);
    }

    @Override
    public boolean deleteVoucher(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_VOU_D, throwException);
    }

    @Override
    public boolean readSupplier(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_SUP_R, throwException);
    }

    @Override
    public boolean insertSupplier(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_SUP_C, throwException);
    }

    @Override
    public boolean updateSupplier(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_SUP_U, throwException);
    }

    @Override
    public boolean deleteSupplier(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_SUP_D, throwException);
    }

    @Override
    public boolean readPromotion(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PROMO_R, throwException);
    }

    @Override
    public boolean insertPromotion(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PROMO_C, throwException);
    }

    @Override
    public boolean updatePromotion(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PROMO_U, throwException);
    }

    @Override
    public boolean deletePromotion(boolean throwException) {
        return super.isAuthorized(ACTION.PRO_PROMO_D, throwException);
    }

    @Override
    public boolean readLedgerTransaction(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_RCT_R, throwException);
    }

    @Override
    public boolean insertLedgerTransaction(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_RCT_C, throwException);
    }

    @Override
    public boolean updateLedgerTransaction(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_RCT_U, throwException);
    }

    @Override
    public boolean readGeneralLedger(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_LEDGER, throwException);
    }

    @Override
    public boolean readLoyaltyProgram(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_LEDGER, throwException);
    }

    @Override
    public boolean insertLoyaltyProgram(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_LEDGER, throwException);
    }

    @Override
    public boolean updateLoyaltyProgram(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_LEDGER, throwException);
    }

    @Override
    public boolean deleteLoyaltyProgram(boolean throwException) {
        return super.isAuthorized(ACTION.SLS_LEDGER, throwException);
    }
}