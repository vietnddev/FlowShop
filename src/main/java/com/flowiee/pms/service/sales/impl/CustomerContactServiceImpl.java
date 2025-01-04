package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.CustomerContact;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.DataInUseException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.repository.sales.CustomerContactRepository;
import com.flowiee.pms.repository.sales.CustomerRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.CustomerContactService;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerContactServiceImpl extends BaseService implements CustomerContactService {
    CustomerContactRepository mvCustomerContactRepository;
    CustomerRepository mvCustomerRepository;

    @Override
    public List<CustomerContact> findAll() {
        return mvCustomerContactRepository.findAll();
    }

    @Override
    public CustomerContact save(CustomerContact customerContact) {
        if (customerContact == null || customerContact.getCustomer() == null) {
            throw new BadRequestException();
        }
        if (mvCustomerRepository.findById(customerContact.getCustomer().getId()).isEmpty()) {
            throw new BadRequestException("Customer contact not found!");
        }
        String lvContactType = customerContact.getCode();
        String lvContactValue = customerContact.getValue();
        CustomerContact contactExists = mvCustomerContactRepository.findByContactTypeAndValue(lvContactType, lvContactValue);
        if (contactExists != null) {
            throw new BadRequestException(String.format("Customer contact %s existed!", lvContactType));
        }
        customerContact.setUsed(false);

        return mvCustomerContactRepository.save(customerContact);
    }

    @Override
    public CustomerContact update(CustomerContact pContact, Long contactId) {
        if (pContact == null || pContact.getCustomer() == null) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        CustomerContact contact = this.findById(contactId, true);
        contact.setCode(pContact.getCode());
        contact.setValue(pContact.getValue());
        contact.setNote(pContact.getNote());
        contact.setIsDefault(pContact.getIsDefault());
        return mvCustomerContactRepository.save(contact);
    }

    @Override
    public String delete(Long contactId) {
        CustomerContact customerContact = this.findById(contactId, true);
        if (customerContact.isUsed()) {
            throw new DataInUseException("This contact has been used!");
        }
        mvCustomerContactRepository.deleteById(contactId);

        String contactCode = customerContact.getCode();
        String customerName = customerContact.getCustomer().getCustomerName();
        systemLogService.writeLogDelete(MODULE.SALES, ACTION.PRO_CUS_D, MasterObject.CustomerContact, "Xóa %s của khách hàng %s".formatted(contactCode, customerName), customerContact.getValue());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<CustomerContact> findContacts(Long customerId) {
        if (mvCustomerRepository.findById(customerId).isEmpty()) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        return mvCustomerContactRepository.findByCustomerId(customerId);
    }

    @Override
    public CustomerContact findById(Long contactId, boolean pThrowException) {
        Optional<CustomerContact> entityOptional = mvCustomerContactRepository.findById(contactId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"customer contact"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public CustomerContact findContactPhoneUseDefault(Long customerId) {
        return mvCustomerContactRepository.findPhoneUseDefault(customerId);
    }

    @Override
    public CustomerContact findContactEmailUseDefault(Long customerId) {
        return mvCustomerContactRepository.findEmailUseDefault(customerId);
    }

    @Override
    public CustomerContact findContactAddressUseDefault(Long customerId) {
        return mvCustomerContactRepository.findAddressUseDefault(customerId);
    }

    @Override
    public CustomerContact enableContactUseDefault(Long customerId, String contactCode, Long contactId) {
        if (mvCustomerRepository.findById(customerId).isEmpty()) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        if (this.findById(contactId, true) == null) {
            throw new ResourceNotFoundException("Customer contact not found!");
        }
        CustomerContact customerContactUsingDefault = switch (contactCode) {
            case "P" -> mvCustomerContactRepository.findPhoneUseDefault(customerId);
            case "E" -> mvCustomerContactRepository.findEmailUseDefault(customerId);
            case "A" -> mvCustomerContactRepository.findAddressUseDefault(customerId);
            default -> throw new IllegalStateException("Unexpected value: " + contactCode);
        };
        if (customerContactUsingDefault != null) {
            customerContactUsingDefault.setIsDefault("N");
            mvCustomerContactRepository.save(customerContactUsingDefault);
        }
        CustomerContact customerContactToUseDefault = this.findById(contactId, true);
        if (customerContactToUseDefault == null) {
            throw new BadRequestException();
        }
        customerContactToUseDefault.setIsDefault("Y");
        return this.update(customerContactToUseDefault, customerContactToUseDefault.getId());
    }

    @Override
    public CustomerContact disableContactUnUseDefault(Long contactId) {
        CustomerContact customerContact = this.findById(contactId, true);
        if (customerContact == null) {
            throw new BadRequestException();
        }
        customerContact.setIsDefault("N");
        return this.update(customerContact, customerContact.getId());
    }
}