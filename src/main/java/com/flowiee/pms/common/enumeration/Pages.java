package com.flowiee.pms.common.enumeration;

public enum Pages {
    CTG_CATEGORY("category",                            MODULE.SYSTEM),
    CTG_CATEGORY_DETAIL("category-detail",              MODULE.SYSTEM),
    SYS_LOGIN("login",                                  MODULE.SYSTEM),
    SYS_ACCOUNT("account",                              MODULE.SYSTEM),
    SYS_ACCOUNT_DETAIL("account-detail",                MODULE.SYSTEM),
    SYS_GR_ACC("group-account",                         MODULE.SYSTEM),
    SYS_GR_ACC_DETAIL("group-account-detail",           MODULE.SYSTEM),
    SYS_BRANCH("branch",                                MODULE.SYSTEM),
    SYS_LOG("log",                                      MODULE.SYSTEM),
    SYS_ROLE("role",                                    MODULE.SYSTEM),
    SYS_CONFIG("config",                                MODULE.SYSTEM),
    SYS_LANGUAGE("language",                           MODULE.SYSTEM),
    SYS_NOTIFICATION("notification",                    MODULE.SYSTEM),
    SYS_PROFILE("profile",                              MODULE.SYSTEM),
    SYS_UNAUTHORIZED("unauthorized",                    MODULE.SYSTEM),
    SYS_ERROR("error",                                  MODULE.SYSTEM),
    SYS_ERROR_BASIC("error-basic",                      MODULE.SYSTEM),
    SYS_DATA_TEMP("data-temp",                          MODULE.SYSTEM),

    PRO_DASHBOARD("dashboard",                          MODULE.PRODUCT),
    PRO_GALLERY("gallery",                              MODULE.PRODUCT),
    PRO_PRODUCT("product",                              MODULE.PRODUCT),
    PRO_PRODUCT_INFO("product-info",                    MODULE.PRODUCT),
    PRO_PRODUCT_VARIANT("product-variant",              MODULE.PRODUCT),
    PRO_PRODUCT_DAMAGED("product-damaged",              MODULE.PRODUCT),
    PRO_PRODUCT_HELD("product-held",                    MODULE.PRODUCT),
    PRO_VOUCHER("vouchers",                             MODULE.PRODUCT),
    PRO_VOUCHER_DETAIL("voucher-detail",                MODULE.PRODUCT),
    PRO_ORDER("order",                                  MODULE.PRODUCT),
    PRO_ORDER_DETAIL("order-detail",                    MODULE.PRODUCT),
    PRO_ORDER_SELL("sell",                              MODULE.PRODUCT),
    PRO_CUSTOMER("customer",                            MODULE.PRODUCT),
    PRO_CUSTOMER_DETAIL("customer-detail",              MODULE.PRODUCT),
    PRO_SUPPLIER("supplier",                            MODULE.PRODUCT),
    PRO_PROMOTION("promotion",                          MODULE.PRODUCT),
    PRO_PROMOTION_DETAIL("promotion-detail",            MODULE.PRODUCT),
    PRO_COMBO("product-combo",                          MODULE.PRODUCT),
    PRO_COMBO_DETAIL("product-combo-detail",            MODULE.PRODUCT),

    SLS_ORDER_RETURNS("order/returns",                  MODULE.SALES),
    SLS_ORDER_TRACKING("order-tracking",                MODULE.SALES),
    SLS_LEDGER("ledger/general-ledger",                 MODULE.SALES),
    SLS_LEDGER_TRANS("ledger/ledger-trans",             MODULE.SALES),
    SLS_LEDGER_TRANS_DETAIL("ledger/ledger-trans-detail", MODULE.SALES),
    SLS_LOYALTY_PROGRAM("loyalty-program/loyalty-program", MODULE.SALES),
    SLS_LOYALTY_PROGRAM_DETAIL("loyalty-program/loyalty-program-detail", MODULE.SALES),

    STG_MATERIAL("material",                            MODULE.STORAGE),
    STG_TRANS_GOODS("transaction-goods",               MODULE.STORAGE),
    STG_TRANS_GOODS_DETAIL("transaction-goods-detail", MODULE.STORAGE),
    STG_TRANS_EXPORT("transaction-goods",               MODULE.STORAGE),
    STG_TRANS_EXPORT_DETAIL("transaction-goods-detail", MODULE.STORAGE),
    STG_STORAGE("storage",                              MODULE.STORAGE),
    STG_STORAGE_DETAIL("storage-detail",                MODULE.STORAGE);

    private final String template;
    private final MODULE module;

    Pages(String template, MODULE module) {
        this.template = template;
        this.module = module;
    }

    public String getTemplate() {
        if (this.equals(SYS_LOGIN)) {
            return "login";
        }
        if (this.equals(PRO_DASHBOARD)) {
            return "pages/dashboard/dashboard";
        }
        String basePath = "";
        if (module.equals(MODULE.SYSTEM)) {
            basePath = "pages/system";
        } else if (module.equals(MODULE.PRODUCT)) {
            basePath = "pages/product";
        } else if (module.equals(MODULE.SALES)) {
            basePath = "pages/sales";
        } else if (module.equals(MODULE.STORAGE)) {
            basePath = "pages/storage";
        }
        return basePath + "/" + template;
    }
}