package com.flowiee.pms.system.enums;

import lombok.Getter;

@Getter
public enum SerialCode {
    ReceiptVoucher,
    PaymentVoucher,
    OrderReturn,
    TransactionGoodsImport,
    TransactionGoodsExport;
}