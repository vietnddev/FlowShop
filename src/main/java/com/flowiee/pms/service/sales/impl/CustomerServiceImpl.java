package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.service.system.ConfigService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.model.PurchaseHistory;
import com.flowiee.pms.model.dto.CustomerDTO;
import com.flowiee.pms.entity.sales.CustomerContact;
import com.flowiee.pms.exception.DataInUseException;
import com.flowiee.pms.entity.sales.Customer;
import com.flowiee.pms.repository.sales.CustomerContactRepository;
import com.flowiee.pms.repository.sales.CustomerRepository;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.CustomerContactService;
import com.flowiee.pms.service.sales.CustomerService;

import com.flowiee.pms.common.utils.CommonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerServiceImpl extends BaseService implements CustomerService {
    CustomerContactRepository mvCustomerContactRepository;
    CustomerContactService mvCustomerContactService;
    CustomerRepository mvCustomerRepository;
    OrderRepository mvOrderRepository;
    ConfigService mvConfigService;

    @Override
    public List<CustomerDTO> findAll() {
        return this.findAll(-1, -1, null, null, null, null, null, null).getContent();
    }

    @Override
    public Page<CustomerDTO> findAll(int pageSize, int pageNum, String name, String sex, Date birthday, String phone, String email, String address) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("customerName").ascending());
        Page<Customer> customers = mvCustomerRepository.findAll(name, sex, birthday, phone, email, address, pageable);
        List<CustomerDTO> customerDTOs = CustomerDTO.fromCustomers(customers.getContent());
        this.setContactDefault(customerDTOs);
        return new PageImpl<>(customerDTOs, pageable, customerDTOs.size());
    }

    @Override
    public List<CustomerDTO> findCustomerNewInMonth() {
        List<CustomerDTO> customerDTOs = CustomerDTO.fromCustomers(mvCustomerRepository.findCustomerNewInMonth());
        this.setContactDefault(customerDTOs);
        return customerDTOs;
    }

    @Override
    public CustomerDTO findById(Long id, boolean pThrowException) {
        Optional<Customer> customer = mvCustomerRepository.findById(id);
        if (customer.isPresent()) {
            CustomerDTO customerDTO = CustomerDTO.fromCustomer(customer.get());
            this.setContactDefault(List.of(customerDTO));
            return customerDTO;
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"customer"}, null, null);
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public CustomerDTO save(CustomerDTO dto) {
        if (dto == null) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        Customer customer = Customer.fromCustomerDTO(dto);
        String lvPhoneDefault = CoreUtils.trim(dto.getPhoneDefault());
        String lvEmailDefault = CoreUtils.trim(dto.getEmailDefault());
        String lvAddressDefault = CoreUtils.trim(dto.getAddressDefault());
        LocalDate lvBirthday = customer.getDateOfBirth();

        if (CoreUtils.isNullStr(customer.getCustomerName()))
            throw new BadRequestException("Customer name can't empty!");
        if (CoreUtils.isAnySpecialCharacter(customer.getCustomerName()))
            throw new BadRequestException("Customer name can't allow include special characters!");
        if (lvBirthday != null && lvBirthday.isAfter(LocalDate.now()))
            throw new BadRequestException("Date of birth can't in the future date!");

        customer.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : CommonUtils.getUserPrincipal().getId());
        customer.setBonusPoints(0);
        customer.setIsBlackList(false);
        customer.setIsVIP(dto.getIsVIP() != null ? dto.getIsVIP() : false);
        Customer customerInserted = mvCustomerRepository.save(customer);

        CustomerContact customerContact = CustomerContact.builder()
                .customer(new Customer(customerInserted.getId()))
                .value(dto.getPhoneDefault())
                .isDefault("Y")
                .status(true).build();

        if (lvPhoneDefault != null) {
            ContactType lvContactType = ContactType.P;

            if (!CoreUtils.validateEmail(lvPhoneDefault))
                throw new BadRequestException("Phone number invalid");
            if (!SysConfigUtils.isYesOption(ConfigCode.allowDuplicateCustomerPhoneNumber)) {
                if ( mvCustomerContactRepository.findByContactTypeAndValue(lvContactType.name(), lvPhoneDefault) != null) {
                    throw new BadRequestException(String.format("Phone %s already used!", dto.getEmailDefault()));
                }
            }

            customerContact.setCode(lvContactType.name());
            mvCustomerContactService.save(customerContact);
        }
        if (lvEmailDefault != null) {
            ContactType lvContactType = ContactType.E;

            if (!CoreUtils.validateEmail(lvEmailDefault))
                throw new BadRequestException("Email invalid");
            if (mvCustomerContactRepository.findByContactTypeAndValue(lvContactType.name(), lvEmailDefault) != null)
                throw new BadRequestException(String.format("Email %s already used!", lvEmailDefault));

            customerContact.setCode(lvContactType.name());
            mvCustomerContactService.save(customerContact);
        }
        if (lvAddressDefault != null) {
            ContactType lvContactType = ContactType.E;
            customerContact.setCode(lvContactType.name());
            mvCustomerContactService.save(customerContact);
        }

        systemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_CUS_C, MasterObject.Customer, "Thêm mới khách hàng", customer.toString());
        logger.info("Create customer: {}", customer.toString());

        return CustomerDTO.fromCustomer(customerInserted);
    }

    @Transactional
    @Override
    public CustomerDTO update(CustomerDTO pCustomer, Long customerId) {
        Customer lvCustomer = this.findById(customerId, true);
        //lvCustomer.set
        //lvCustomer.set
        //lvCustomer.set
        Customer customerUpdated = mvCustomerRepository.save(lvCustomer);

        CustomerContact phoneDefault = null;
        CustomerContact emailDefault = null;
        CustomerContact addressDefault = null;
        if (pCustomer.getPhoneDefault() != null || pCustomer.getEmailDefault() != null || pCustomer.getAddressDefault() != null) {
            List<CustomerContact> contacts = mvCustomerContactService.findContacts(customerId);
            for (CustomerContact contact : contacts) {
                boolean isStatus = contact.isStatus();
                boolean isDefault = "Y".equals(contact.getIsDefault());

                if (contact.isPhoneContact() && isDefault && isStatus) {
                    phoneDefault = contact;
                }
                if (contact.isEmailContact() && isDefault && isStatus) {
                    emailDefault = contact;
                }
                if (contact.isAddressContact() && isDefault && isStatus) {
                    addressDefault = contact;
                }
            }

            if (pCustomer.getPhoneDefault() != null && phoneDefault != null) {
                phoneDefault.setValue(pCustomer.getPhoneDefault());
                //customerContactService.update(phoneDefault, customerId);
                mvCustomerContactRepository.save(phoneDefault);
            } else if (pCustomer.getPhoneDefault() != null && !pCustomer.getPhoneDefault().isEmpty()) {
                phoneDefault = CustomerContact.builder()
                    .customer(new Customer(customerId))
                    .code(ContactType.P.name())
                    .value(pCustomer.getPhoneDefault())
                    .isDefault("Y")
                    .status(true).build();
                mvCustomerContactService.save(phoneDefault);
            }

            if (pCustomer.getEmailDefault() != null && emailDefault != null) {
                emailDefault.setValue(pCustomer.getEmailDefault());
                //customerContactService.update(emailDefault, customerId);
                mvCustomerContactRepository.save(emailDefault);
            } else if (pCustomer.getEmailDefault() != null && !pCustomer.getEmailDefault().isEmpty()) {
                emailDefault = CustomerContact.builder()
                    .customer(new Customer(customerId))
                    .code(ContactType.E.name())
                    .value(pCustomer.getEmailDefault())
                    .isDefault("Y")
                    .status(true).build();
                mvCustomerContactService.save(emailDefault);
            }

            if (pCustomer.getAddressDefault() != null && addressDefault != null) {
                addressDefault.setValue(pCustomer.getAddressDefault());
                //customerContactService.update(addressDefault, customerId);
                mvCustomerContactRepository.save(addressDefault);
            } else if (pCustomer.getAddressDefault() != null && !pCustomer.getAddressDefault().isEmpty()) {
                addressDefault = CustomerContact.builder()
                    .customer(new Customer(customerId))
                    .code(ContactType.A.name())
                    .value(pCustomer.getAddressDefault())
                    .isDefault("Y")
                    .status(true).build();
                mvCustomerContactService.save(addressDefault);
            }
        }
        systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_CUS_U, MasterObject.Customer, "Cập nhật thông tin khách hàng", pCustomer.toString());
        logger.info("Update customer info {}", pCustomer.toString());
        return CustomerDTO.fromCustomer(customerUpdated);
    }

    @Override
    public String delete(Long id) {
        CustomerDTO customer = this.findById(id, true);
        List<Order> orderOfCustomer = mvOrderRepository.findByCustomer(customer.getId());
        if (ObjectUtils.isNotEmpty(orderOfCustomer)) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }
        mvCustomerRepository.deleteById(customer.getId());

        systemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_CUS_D, MasterObject.Customer, "Xóa khách hàng", customer.getCustomerName());
        logger.info("Deleted customer id={}", customer.getId());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private void setContactDefault(List<CustomerDTO> customerDTOs) {
        for (CustomerDTO c : customerDTOs) {
            CustomerContact phoneDefault = mvCustomerContactService.findContactPhoneUseDefault(c.getId());
            CustomerContact emailDefault = mvCustomerContactService.findContactEmailUseDefault(c.getId());
            CustomerContact addressDefault = mvCustomerContactService.findContactAddressUseDefault(c.getId());
            c.setPhoneDefault(phoneDefault != null ? phoneDefault.getValue() : "");
            c.setEmailDefault(emailDefault != null ? emailDefault.getValue() : "");
            c.setAddressDefault(addressDefault != null ? addressDefault.getValue() : "");
        }
    }

    @Override
    public List<PurchaseHistory> findPurchaseHistory(Long customerId, Integer year, Integer month) {
        List<PurchaseHistory> purchaseHistories = new ArrayList<>();
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        List<Object[]> purchaseHistoriesRawValue = mvOrderRepository.findPurchaseHistory(customerId, year, month);
        //col 0 -> year
        //col 1 -> month
        //col 2 -> purchase qty
        //col 3 -> average value
        for (Object[] data : purchaseHistoriesRawValue) {
            PurchaseHistory ph = PurchaseHistory.builder()
                .customerId(customerId)
                .year(year)
                .month(Integer.parseInt(String.valueOf(data[1])))
                .purchaseQty(Integer.parseInt(String.valueOf(data[2])))
                .orderAvgValue(new BigDecimal(String.valueOf(data[3])))
                .build();
            purchaseHistories.add(ph);
        }
        return purchaseHistories;
    }

    @Override
    public Page<CustomerDTO> getVIPCustomers(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Customer> customers = mvCustomerRepository.findVIPCustomers(pageable);
        List<CustomerDTO> customerDTOs = CustomerDTO.fromCustomers(customers.getContent());
        this.setContactDefault(customerDTOs);

        return new PageImpl<>(customerDTOs, pageable, customerDTOs.size());
    }

    @Override
    public Page<CustomerDTO> getBlackListCustomers(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Customer> customers = mvCustomerRepository.findBlackListCustomers(pageable);
        List<CustomerDTO> customerDTOs = CustomerDTO.fromCustomers(customers.getContent());
        this.setContactDefault(customerDTOs);

        return new PageImpl<>(customerDTOs, pageable, customerDTOs.size());
    }
}