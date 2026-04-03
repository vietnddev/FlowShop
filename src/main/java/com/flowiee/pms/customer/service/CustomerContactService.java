package com.flowiee.pms.customer.service;

import com.flowiee.pms.shared.base.ICurdService;
import com.flowiee.pms.customer.enums.ContactType;
import com.flowiee.pms.customer.dto.CustomerContactDTO;

import java.util.List;

public interface CustomerContactService extends ICurdService<CustomerContactDTO> {
    List<CustomerContactDTO> findContacts(Long customerId);

    CustomerContactDTO findContactUseDefault(Long customerId, ContactType contactType);

    CustomerContactDTO enableContactUseDefault(Long customerId, String type, Long contactId);

    CustomerContactDTO disableContactUnUseDefault(Long contactId);
}