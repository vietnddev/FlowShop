package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.common.enumeration.ContactType;
import com.flowiee.pms.modules.sales.dto.CustomerContactDTO;

import java.util.List;

public interface CustomerContactService extends ICurdService<CustomerContactDTO> {
    List<CustomerContactDTO> findContacts(Long customerId);

    CustomerContactDTO findContactPhoneUseDefault(Long customerId);

    CustomerContactDTO findContactEmailUseDefault(Long customerId);

    CustomerContactDTO findContactAddressUseDefault(Long customerId);

    CustomerContactDTO findContact(Long customerId, ContactType contactType);

    CustomerContactDTO enableContactUseDefault(Long customerId, String type, Long contactId);

    CustomerContactDTO disableContactUnUseDefault(Long contactId);
}