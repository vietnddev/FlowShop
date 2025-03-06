package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.entity.sales.CustomerContact;

import java.util.List;

public interface CustomerContactService extends BaseCurdService<CustomerContact> {
    List<CustomerContact> findContacts(Long customerId);

    CustomerContact findContactPhoneUseDefault(Long customerId);

    CustomerContact findContactEmailUseDefault(Long customerId);

    CustomerContact findContactAddressUseDefault(Long customerId);

    CustomerContact enableContactUseDefault(Long customerId, String type, Long contactId);

    CustomerContact disableContactUnUseDefault(Long contactId);
}