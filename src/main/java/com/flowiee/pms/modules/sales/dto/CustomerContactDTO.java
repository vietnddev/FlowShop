package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.common.enumeration.ContactType;
import com.flowiee.pms.modules.sales.entity.Customer;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;

@Data
public class CustomerContactDTO extends BaseDTO implements Serializable {
    Customer customer;
    String code;
    String value;
    String note;
    String isDefault;
    boolean status;
    boolean isUsed;

    public boolean isPhoneContact() {
        Assert.notNull(code, "Contact type is null!");
        return ContactType.P.name().equals(code);
    }

    public boolean isEmailContact() {
        Assert.notNull(code, "Contact type is null!");
        return ContactType.E.name().equals(code);
    }

    public boolean isAddressContact() {
        Assert.notNull(code, "Contact type is null!");
        return ContactType.A.name().equals(code);
    }
}