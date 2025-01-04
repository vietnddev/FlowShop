package com.flowiee.pms.common.enumeration;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum CategoryType {
    SIZE("size", "SIZE", "Kích cỡ"),
    COLOR("color", "COLOR", "Màu sắc"),
    UNIT("unit", "UNIT", "Đơn vị tính"),
    BRAND("brand", "BRAND", "Thương hiệu"),
    COLLECTION("collection", "COLLECTION", "Bộ sưu tập"),
    ORDER_TYPE("order-type", "ORDER_TYPE", "Loại đơn hàng"),
    FABRIC_TYPE("fabric-type", "FABRIC_TYPE", "Chất liệu vải"),
    PRODUCT_TYPE("product-type", "PRODUCT_TYPE", "Loại sản phẩm"),
    RECEIPT_TYPE("receipt-type", "RECEIPT_TYPE", "Loại phiếu thu"),
    PAYMENT_TYPE("payment-type", "PAYMENT_TYPE", "Loại phiếu chi"),
    DOCUMENT_TYPE("document-type", "DOCUMENT_TYPE", "Loại tài liệu"),
    SALES_CHANNEL("sales-channel", "SALES_CHANNEL", "Kênh bán hàng"),
    SHIP_METHOD("ship-method", "SHIP_METHOD", "Hình thức vận chuyển"),
    ORDER_STATUS("order-status", "ORDER_STATUS", "Trạng thái đơn hàng"),
    GROUP_CUSTOMER("group-customer", "GROUP_CUSTOMER", "Nhóm khách hàng"),
    GROUP_OBJECT("group-object", "GROUP_OBJECT", "Nhóm đối tượng/ đối tác"),
    PAYMENT_METHOD("payment-method", "PAYMENT_METHOD", "Hình thức thanh toán"),
    PAYMENT_STATUS("payment-status", "PAYMENT_STATUS", "Trạng thái thanh toán"),
    COMMUNE("commune", "COMMUNE", "Xã"),
    DISTRICT("district", "DISTRICT", "Huyện"),
    PROVINCE("province", "PROVINCE", "Tỉnh"),
    ORDER_CANCEL_REASON("order-cancel-reason", "ORDER_CANCEL_REASON", "Lý do hủy đơn hàng"),
    PRODUCT_STATUS("product-status", "PRODUCT_STATUS", "Trạng thái đơn hàng");

    private final String key;
    private final String name;
    @Setter
    private String label;

    CategoryType(String key, String name, String label) {
        this.key = key;
        this.name = name;
        this.label = label;
    }
}