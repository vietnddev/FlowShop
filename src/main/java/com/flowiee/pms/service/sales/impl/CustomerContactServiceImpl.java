package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.base.service.BaseGService;
import com.flowiee.pms.entity.sales.CustomerContact;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.DataInUseException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.model.dto.CustomerContactDTO;
import com.flowiee.pms.repository.sales.CustomerContactRepository;
import com.flowiee.pms.repository.sales.CustomerRepository;
import com.flowiee.pms.service.sales.CustomerContactService;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.service.system.SystemLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerContactServiceImpl extends BaseGService<CustomerContact, CustomerContactDTO, CustomerContactRepository> implements CustomerContactService {
    private final CustomerRepository mvCustomerRepository;
    private final SystemLogService mvSystemLogService;

    public CustomerContactServiceImpl(CustomerContactRepository pEntityRepository, CustomerRepository pCustomerRepository, SystemLogService pSystemLogService) {
        super(CustomerContact.class, CustomerContactDTO.class, pEntityRepository);
        this.mvCustomerRepository = pCustomerRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<CustomerContactDTO> findAll() {
        return super.findAll();
    }

    @Override
    public CustomerContactDTO save(CustomerContactDTO pCustomerContact) {
        if (pCustomerContact == null || pCustomerContact.getCustomer() == null) {
            throw new BadRequestException();
        }
        if (mvCustomerRepository.findById(pCustomerContact.getCustomer().getId()).isEmpty()) {
            throw new BadRequestException("Customer contact not found!");
        }
        String lvContactType = pCustomerContact.getCode();
        String lvContactValue = pCustomerContact.getValue();
        CustomerContact contactExists = mvEntityRepository.findByContactTypeAndValue(lvContactType, lvContactValue);
        if (contactExists != null) {
            throw new BadRequestException(String.format("Customer contact %s existed!", lvContactType));
        }
        pCustomerContact.setUsed(false);

        return super.save(pCustomerContact);
    }

    @Override
    public CustomerContactDTO update(CustomerContactDTO pContact, Long contactId) {
        if (pContact == null || pContact.getCustomer() == null) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        CustomerContact lvContact = super.findById(contactId).orElseThrow(() -> new BadRequestException());
        lvContact.setCode(pContact.getCode());
        lvContact.setValue(pContact.getValue());
        lvContact.setNote(pContact.getNote());
        lvContact.setIsDefault(pContact.getIsDefault());
        return  convertDTO(mvEntityRepository.save(lvContact));
    }

    @Override
    public String delete(Long contactId) {
        CustomerContactDTO customerContact = this.findById(contactId, true);
        if (customerContact.isUsed()) {
            throw new DataInUseException("This contact has been used!");
        }
        mvEntityRepository.deleteById(contactId);

        String contactCode = customerContact.getCode();
        String customerName = customerContact.getCustomer().getCustomerName();
        mvSystemLogService.writeLogDelete(MODULE.SALES, ACTION.PRO_CUS_D, MasterObject.CustomerContact, "Xóa %s của khách hàng %s".formatted(contactCode, customerName), customerContact.getValue());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<CustomerContactDTO> findContacts(Long customerId) {
        if (mvCustomerRepository.findById(customerId).isEmpty()) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        return convertDTOs(mvEntityRepository.findByCustomerId(customerId));
    }

    @Override
    public CustomerContactDTO findById(Long pContactId, boolean pThrowException) {
        return super.findById(pContactId, pThrowException);
    }

    @Override
    public CustomerContactDTO findContactPhoneUseDefault(Long customerId) {
        return convertDTO(mvEntityRepository.findPhoneUseDefault(customerId));
    }

    @Override
    public CustomerContactDTO findContactEmailUseDefault(Long customerId) {
        return convertDTO(mvEntityRepository.findEmailUseDefault(customerId));
    }

    @Override
    public CustomerContactDTO findContactAddressUseDefault(Long customerId) {
        return convertDTO(mvEntityRepository.findAddressUseDefault(customerId));
    }

    @Override
    public CustomerContactDTO enableContactUseDefault(Long customerId, String contactCode, Long contactId) {
        if (mvCustomerRepository.findById(customerId).isEmpty()) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        if (this.findById(contactId, true) == null) {
            throw new ResourceNotFoundException("Customer contact not found!");
        }
        CustomerContact customerContactUsingDefault = switch (contactCode) {
            case "P" -> mvEntityRepository.findPhoneUseDefault(customerId);
            case "E" -> mvEntityRepository.findEmailUseDefault(customerId);
            case "A" -> mvEntityRepository.findAddressUseDefault(customerId);
            default -> throw new IllegalStateException("Unexpected value: " + contactCode);
        };
        if (customerContactUsingDefault != null) {
            customerContactUsingDefault.setIsDefault("N");
            mvEntityRepository.save(customerContactUsingDefault);
        }
        CustomerContact customerContactToUseDefault = super.findById(contactId).orElseThrow(() -> new BadRequestException());
        customerContactToUseDefault.setIsDefault("Y");
        return convertDTO(mvEntityRepository.save(customerContactToUseDefault));
    }

    @Override
    public CustomerContactDTO disableContactUnUseDefault(Long contactId) {
        CustomerContact customerContact = super.findById(contactId).orElseThrow(() -> new BadRequestException());
        customerContact.setIsDefault("N");
        return convertDTO(mvEntityRepository.save(customerContact));
    }
}