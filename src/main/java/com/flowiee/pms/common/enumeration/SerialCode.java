package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum SerialCode {
    ReceiptVoucher,
    PaymentVoucher,
    OrderReturn,
    TransactionGoodsImport,
    TransactionGoodsExport;
}