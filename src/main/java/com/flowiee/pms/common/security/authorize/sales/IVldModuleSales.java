package com.flowiee.pms.common.security.authorize.sales;

public interface IVldModuleSales {
    boolean dashboard(boolean throwException);

    boolean importGoods(boolean throwException);

    boolean exportGoods(boolean throwException);

    boolean readOrder(boolean throwException);

    boolean insertOrder(boolean throwException);

    boolean updateOrder(boolean throwException);

    boolean deleteOrder(boolean throwException);

    boolean readCustomer(boolean throwException);

    boolean insertCustomer(boolean throwException);

    boolean updateCustomer(boolean throwException);

    boolean deleteCustomer(boolean throwException);

    boolean readVoucher(boolean throwException);

    boolean insertVoucher(boolean throwException);

    boolean updateVoucher(boolean throwException);

    boolean deleteVoucher(boolean throwException);

    boolean readSupplier(boolean throwException);

    boolean insertSupplier(boolean throwException);

    boolean updateSupplier(boolean throwException);

    boolean deleteSupplier(boolean throwException);

    boolean readPromotion(boolean throwException);

    boolean insertPromotion(boolean throwException);

    boolean updatePromotion(boolean throwException);

    boolean deletePromotion(boolean throwException);

    boolean readLedgerTransaction(boolean throwException);

    boolean insertLedgerTransaction(boolean throwException);

    boolean updateLedgerTransaction(boolean throwException);

    boolean readGeneralLedger(boolean throwException);

    boolean readLoyaltyProgram(boolean throwException);

    boolean insertLoyaltyProgram(boolean throwException);

    boolean updateLoyaltyProgram(boolean throwException);

    boolean deleteLoyaltyProgram(boolean throwException);
}