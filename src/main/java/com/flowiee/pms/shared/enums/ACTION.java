package com.flowiee.pms.shared.enums;

import java.util.Arrays;

public enum ACTION {
    READ_DASHBOARD("Xem dashboard",             MODULE.DASHBOARD),

    CTG_R("View list of system categories",     MODULE.CATEGORY),
    CTG_C("Create category",                    MODULE.CATEGORY),
    CTG_U("Update category",                    MODULE.CATEGORY),
    CTG_D("Delete category",                    MODULE.CATEGORY),
    CTG_I("Import category",                    MODULE.CATEGORY),
    CTG_E("Export category",                    MODULE.CATEGORY),

    PRO_PRD_R("View list of products",          MODULE.PRODUCT),
    PRO_PRD_C("Create product",                 MODULE.PRODUCT),
    PRO_PRD_U("Update product",                 MODULE.PRODUCT),
    PRO_PRD_D("Delete product",                 MODULE.PRODUCT),
    PRO_PRD_I("Import product",                 MODULE.PRODUCT),
    PRO_PRD_E("Export product",                 MODULE.PRODUCT),
    PRO_PRD_PRICE("Product's price",            MODULE.PRODUCT),
    PRO_PRD_RPT("View product's report",        MODULE.PRODUCT),
    PRO_CBO_R("View list of combos",            MODULE.PRODUCT),
    PRO_CBO_C("Create combo",                   MODULE.PRODUCT),
    PRO_CBO_U("Update combo",                   MODULE.PRODUCT),
    PRO_CBO_D("Delete combo",                   MODULE.PRODUCT),
    PRO_CART_C("Tạo giỏ hàng",                  MODULE.SALES),
    PRO_ORD_R("View list of orders",            MODULE.PRODUCT),
    PRO_ORD_C("Create order",                   MODULE.PRODUCT),
    PRO_ORD_U("Update order",                   MODULE.PRODUCT),
    PRO_ORD_D("Delete order",                   MODULE.PRODUCT),
    PRO_ORD_E("Export order",                   MODULE.PRODUCT),
    PRO_CUS_R("View list of customers",         MODULE.PRODUCT),
    PRO_CUS_C("Create customer",                MODULE.PRODUCT),
    PRO_CUS_U("Update customer",                MODULE.PRODUCT),
    PRO_CUS_D("Delete customer",                MODULE.PRODUCT),
    PRO_SUP_R("View list of suppliers",         MODULE.PRODUCT),
    PRO_SUP_C("Create supplier",                MODULE.PRODUCT),
    PRO_SUP_U("Update supplier",                MODULE.PRODUCT),
    PRO_SUP_D("Delete supplier",                MODULE.PRODUCT),
    PRO_VOU_R("View list of vouchers",          MODULE.PRODUCT),
    PRO_VOU_C("Create voucher",                 MODULE.PRODUCT),
    PRO_VOU_U("Update voucher",                 MODULE.PRODUCT),
    PRO_VOU_D("Delete voucher",                 MODULE.PRODUCT),
    PRO_PROMO_R("View list of promotions",      MODULE.PRODUCT),
    PRO_PROMO_C("Create promotion",             MODULE.PRODUCT),
    PRO_PROMO_U("Update promotion",             MODULE.PRODUCT),
    PRO_PROMO_D("Delete promotion",             MODULE.PRODUCT),
    PRO_GAL_R("Gallery",                        MODULE.PRODUCT),

    SLS_RCT_R("Xem danh sách phiếu thu",        MODULE.SALES),
    SLS_RCT_C("Tạo phiếu thu",                  MODULE.SALES),
    SLS_RCT_U("Cập nhật phiếu thu",             MODULE.SALES),
    SLS_PMT_R("Xem danh sách phiếu chi",        MODULE.SALES),
    SLS_PMT_C("Tạo phiếu chi",                  MODULE.SALES),
    SLS_PMT_U("Cập nhật phiếu chi",             MODULE.SALES),
    SLS_LEDGER("Xem sổ quỹ",                    MODULE.SALES),
    SLS_LP_R("Read list of loyalty program",    MODULE.SALES),
    SLS_LP_C("Create loyalty program",          MODULE.SALES),
    SLS_LP_U("Update loyalty program",          MODULE.SALES),
    SLS_LP_D("Delete loyalty program",          MODULE.SALES),

    STG_DASHBOARD("Xem dashboard STG",          MODULE.PRODUCT),
    STG_MAT_R("View list of materials",         MODULE.PRODUCT),
    STG_MAT_C("Create material",                MODULE.PRODUCT),
    STG_MAT_U("Update material",                MODULE.PRODUCT),
    STG_MAT_D("Delete material",                MODULE.PRODUCT),
    STG_TICKET_IM("Import goods into storage",  MODULE.PRODUCT),
    STG_TICKET_EX("Export goods",               MODULE.PRODUCT),

    STG_STORAGE("Storage management",           MODULE.STORAGE),
    STG_STG_C("Thêm mới kho",                   MODULE.STORAGE),
    STG_STG_U("Cập nhật kho",                   MODULE.STORAGE),
    STG_STG_D("Xóa kho",                        MODULE.STORAGE),

    SYS_LOGIN("Login",                          MODULE.SYSTEM),
    SYS_ROLE_R("View list of system rights",    MODULE.SYSTEM),
    SYS_ROLE_U("Update rights",                 MODULE.SYSTEM),
    SYS_LOG_R("View list of system logs",       MODULE.SYSTEM),
    SYS_ACC_R("View list of accounts",          MODULE.SYSTEM),
    SYS_ACC_C("Create account",                 MODULE.SYSTEM),
    SYS_ACC_U("Update account",                 MODULE.SYSTEM),
    SYS_ACC_D("Delete account",                 MODULE.SYSTEM),
    SYS_ACC_CH_PWD("Change password",           MODULE.SYSTEM),
    SYS_ACC_RS_PWD("Reset password",            MODULE.SYSTEM),
    SYS_ACC_SHARE_ROLE("Grant permissions",     MODULE.SYSTEM),
    SYS_GR_ACC_R("View list of account groups", MODULE.SYSTEM),
    SYS_GR_ACC_C("Create account group",        MODULE.SYSTEM),
    SYS_GR_ACC_U("Update account group",        MODULE.SYSTEM),
    SYS_GR_ACC_D("Delete account group",        MODULE.SYSTEM),

    SYS_BRCH_R("View list of branches",         MODULE.SYSTEM),
    SYS_BRCH_C("Create branch",                 MODULE.SYSTEM),
    SYS_BRCH_U("Update branch",                 MODULE.SYSTEM),
    SYS_BRCH_D("Delete branch",                 MODULE.SYSTEM),
    SYS_LREQ_R("Read list of leave requests",   MODULE.SYSTEM),
    SYS_LREQ_A("Approve/reject leave request",  MODULE.SYSTEM),

    SYS_CNF_R("View system config",             MODULE.SYSTEM),
    SYS_CNF_U("Update system config",           MODULE.SYSTEM),

    SYS_DATA_CRAWLER("Crawler temp data",       MODULE.SYSTEM),
    SYS_DATA_MERGE("Merge temp data",           MODULE.SYSTEM),
    SYS_REFRESH_APP("Refresh application",      MODULE.SYSTEM),
    SYS_DATA_BACKUP("Backup uploaded data",     MODULE.SYSTEM),
    SYS_DATA_RESTORE("Restore data",            MODULE.SYSTEM);

    private final String label;
    private final MODULE module;

    ACTION(String label, MODULE module) {
        this.label = label;
        this.module = module;
    }

    public String getActionKey() {
        return this.name();
    }

    public String getActionLabel() {
        return label;
    }

    public String getModuleKey() {
        return getModule().name();
    }

    public MODULE getModule() {
        return module;
    }

    public String getModuleLabel() {
        if (MODULE.CATEGORY.name().equals(module)) return MODULE.CATEGORY.getLabel();
        if (MODULE.PRODUCT.name().equals(module)) return MODULE.PRODUCT.getLabel();
        if (MODULE.SYSTEM.name().equals(module)) return MODULE.SYSTEM.getLabel();
        return null;
    }

    public static ACTION get(String pName) {
        return Arrays.stream(ACTION.values())
                .filter(a -> a.name().equals(pName))
                .findFirst()
                .orElse(null);
    }
}