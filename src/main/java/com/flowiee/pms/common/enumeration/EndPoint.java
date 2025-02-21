package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum EndPoint {
    URL_PROFILE("/sys/profile", "HEADER", true),
    URL_LOGIN("/sys/login", "HEADER", true),
    URL_LOGOUT("/sys/logout", "HEADER", true),
    URL_NOTIFICATION("/sys/notification", "HEADER", true),

    URL_CATEGORY("/system/category", "SIDEBAR", true),
    URL_PRODUCT("/san-pham", "SIDEBAR", true),
    URL_PRODUCT_ORDER("/sls/order", "SIDEBAR", true),
    URL_PRODUCT_CREATE_ORDER("/sls/order/ban-hang", "SIDEBAR", true),
    URL_PRODUCT_CUSTOMER("/customer", "SIDEBAR", true),
    URL_PRODUCT_SUPPLIER("/san-pham/supplier", "SIDEBAR", true),
    URL_PRODUCT_GALLERY("/gallery", "SIDEBAR", true),
    URL_PRODUCT_VOUCHER("/san-pham/voucher", "SIDEBAR", true),
    URL_PRODUCT_PROMOTION("/promotion", "SIDEBAR", true),
    URL_PRODUCT_COMBO("/product/combo", "SIDEBAR", true),
    URL_SALES_LEDGER("/ledger", "SIDEBAR", true),
    URL_SALES_LEDGER_RECEIPT("/ledger/trans/receipt", "SIDEBAR", true),
    URL_SALES_LEDGER_PAYMENT("/ledger/trans/payment", "SIDEBAR", true),
    URL_STG_DASHBOARD("/stg", "SIDEBAR", true),
    URL_STG_DOCUMENT("/stg/doc", "SIDEBAR", true),
    URL_STG_MATERIAL("/stg/material", "SIDEBAR", true),
    URL_STG_TICKET_IMPORT("/stg/ticket-import", "SIDEBAR", true),
    URL_STG_TICKET_EXPORT("/stg/ticket-export", "SIDEBAR", true),
    URL_SYS_CONFIG("/sys/config", "SIDEBAR", true),
    URL_SYS_LOG("/sys/log", "SIDEBAR", true),
    URL_SYS_GR_ACCOUNT("/sys/group-account", "SIDEBAR", true),
    URL_SYS_ACCOUNT("/sys/tai-khoan", "SIDEBAR", true),
    URL_SYS_BRANCH("/sys/branch", "SIDEBAR", true),
    URL_STG_STORAGE("/storage", "SIDEBAR", true),

    URL_STG_MATERIAL_IMPORT("/stg/material/import", "MAIN", true),
    URL_STG_MATERIAL_IMPORT_TEMPLATE( "/stg/material/template", "MAIN", true),
    URL_STG_MATERIAL_EXPORT("/stg/material/export", "MAIN", true);

    private final String value;
    private final String type;
    private final boolean status;

    EndPoint(String value, String type, boolean status) {
        this.value = value;
        this.type = type;
        this.status = status;
    }
}