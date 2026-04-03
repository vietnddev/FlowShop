package com.flowiee.pms.customer.enums;

import lombok.Getter;

@Getter
public enum CustomerGroup {
    G1("Khách mua hàng vãng lai"),
    G2("khách hàng thân thiết"),
    G3("Khách hàng VIP");

    CustomerGroup(String label) {
        this.label = label;
    }

    private final String label;
}