package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum TemplateExport {
    IM_LIST_OF_PRODUCTS("Template_I_Product.xlsx", "I", MODULE.PRODUCT, ""),
    EX_LIST_OF_PRODUCTS("Template_E_Product.xlsx", "E", MODULE.PRODUCT, ""),
    IE_LIST_OF_PRODUCTS("Template_IE_Product.xlsx", "IE", MODULE.PRODUCT, ""),
    EX_LIST_OF_ORDERS("Template_E_Order.xlsx", "E", MODULE.SALES, ""),
    IM_LIST_OF_CATEGORIES("Template_IE_DM_Category.xlsx", "I", MODULE.CATEGORY, ""),
    EX_LIST_OF_CATEGORIES("Template_IE_DM_Category.xlsx", "E", MODULE.CATEGORY, ""),
    EX_STORAGE_ITEMS("Template_E_Storage.xlsx", "E", MODULE.STORAGE, ""),
    EX_LEDGER_TRANSACTIONS("Template_E_GeneralLedger.xlsx", "E", MODULE.STORAGE, ""),
    EX_LIST_OF_LOGS("Template_E_Log.xlsx", "E", MODULE.SYSTEM, "");

    private final String templateName;
    private final String type;
    private final MODULE module;
    private final String entity;

    TemplateExport(String templateName, String type, MODULE module, String entity) {
        this.templateName = templateName;
        this.type = type;
        this.module = module;
        this.entity = entity;
    }
}