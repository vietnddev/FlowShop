package com.flowiee.pms.customer.service.impl;

import com.flowiee.pms.customer.entity.Customer;
import com.flowiee.pms.customer.enums.ContactType;
import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.request.BaseParameter;
import com.flowiee.pms.customer.entity.CustomerContact;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.exception.DataInUseException;
import com.flowiee.pms.shared.exception.ResourceNotFoundException;
import com.flowiee.pms.customer.dto.CustomerContactDTO;
import com.flowiee.pms.customer.repository.CustomerContactRepository;
import com.flowiee.pms.customer.repository.CustomerRepository;
import com.flowiee.pms.customer.service.CustomerContactService;
import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.system.service.SystemLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerContactServiceImpl extends BaseService<CustomerContact, CustomerContactDTO, CustomerContactRepository> implements CustomerContactService {
    private final CustomerRepository mvCustomerRepository;
    private final SystemLogService mvSystemLogService;

    public CustomerContactServiceImpl(CustomerContactRepository pEntityRepository, CustomerRepository pCustomerRepository, SystemLogService pSystemLogService) {
        super(CustomerContact.class, CustomerContactDTO.class, pEntityRepository);
        this.mvCustomerRepository = pCustomerRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<CustomerContactDTO>find(BaseParameter pParam) {
        return super.find(pParam);
    }

    @Override
    public CustomerContactDTO save(CustomerContactDTO pCustomerContact) {
        if (pCustomerContact == null) {
            throw new BadRequestException();
        }

        String lvContactType = pCustomerContact.getCode();
        String lvContactValue = pCustomerContact.getValue();

        if (pCustomerContact.getCustomer() == null) {
            throw new BadRequestException();
        }

        Long lvCustomerId = pCustomerContact.getCustomer().getId();
        if (lvCustomerId == null) {
            throw new BadRequestException();
        }

        if (CoreUtils.isNullStr(lvContactType) || CoreUtils.isNullStr(lvContactValue)) {
            throw new BadRequestException();
        }

        if (ContactType.E.name().equals(lvContactType) && !CoreUtils.validateEmail(lvContactValue)) {
            throw new BadRequestException("Email format is invalid!");
        }

        if (ContactType.P.name().equals(lvContactType) && !isValidPhoneNumber(lvContactValue)) {
            throw new BadRequestException("Phone number format is invalid!");
        }

        // Check duplicate
        if (mvEntityRepository.findByContactTypeAndValue(lvContactType, lvContactValue) != null) {
            throw new BadRequestException(String.format("%s '%s' is already used by another customer!", lvContactType, lvContactValue));
        }

        if (mvCustomerRepository.findById(lvCustomerId).isEmpty()) {
            throw new BadRequestException("Customer contact not found!");
        }

        if (!ContactType.A.name().equals(lvContactType)) {
            CustomerContact contactExists = mvEntityRepository.findByContactTypeAndValue(lvContactType, lvContactValue);
            if (contactExists != null) {
                throw new BadRequestException(String.format("Customer contact %s existed!", lvContactType));
            }
        }

        return super.convertDTO(mvEntityRepository.save(CustomerContact.builder()
                .customer(new Customer(lvCustomerId))
                .code(lvContactType)
                .value(lvContactValue)
                .note("N/A")
                .isDefault("Y")
                .status(true)
                .isUsed(false)
                .build()));
    }

    @Override
    public CustomerContactDTO update(CustomerContactDTO pContact, Long contactId) {
        if (pContact == null || pContact.getCustomer() == null) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        CustomerContact lvContact = super.findEntById(contactId, true);
        lvContact.setCode(pContact.getCode());
        lvContact.setValue(pContact.getValue());
        lvContact.setNote(pContact.getNote());
        lvContact.setStatus(pContact.isStatus());
        lvContact.setIsDefault(pContact.getIsDefault());
        return  convertDTO(mvEntityRepository.save(lvContact));
    }

    @Override
    public boolean delete(Long contactId) {
        CustomerContact customerContact = this.findEntById(contactId, true);
        if (customerContact.isUsed()) {
            throw new DataInUseException("This contact has been used!");
        }
        mvEntityRepository.deleteById(contactId);

        String contactCode = customerContact.getCode();
        String customerName = customerContact.getCustomer().getCustomerName();
        mvSystemLogService.writeLogDelete(ACTION.PRO_CUS_D, MasterObject.CustomerContact, "Xóa %s của khách hàng %s".formatted(contactCode, customerName), customerContact.getValue());

        return true;
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
        return super.findDtoById(pContactId, pThrowException);
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
            case "P" -> mvEntityRepository.findContactDefault(customerId, ContactType.P.name());
            case "E" -> mvEntityRepository.findContactDefault(customerId, ContactType.E.name());
            case "A" -> mvEntityRepository.findContactDefault(customerId, ContactType.A.name());
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

    private boolean isValidPhoneNumber(String phone) {
        // Basic validation: at least 8 digits, only numbers and maybe +, -
        if (phone == null) return false;
        return phone.matches("^[+]?[0-9\\-\\s]{8,20}$");
    }
}