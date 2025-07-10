package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.sales.dto.CustomerContactDTO;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.sales.model.OrderReq;
import com.flowiee.pms.modules.sales.service.CustomerContactService;
import com.flowiee.pms.modules.sales.service.CustomerService;
import com.flowiee.pms.modules.system.service.ConfigService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.sales.model.PurchaseHistory;
import com.flowiee.pms.modules.sales.dto.CustomerDTO;
import com.flowiee.pms.modules.sales.entity.CustomerContact;
import com.flowiee.pms.common.exception.DataInUseException;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.repository.CustomerContactRepository;
import com.flowiee.pms.modules.sales.repository.CustomerRepository;
import com.flowiee.pms.modules.sales.repository.OrderRepository;

import com.flowiee.pms.modules.system.service.SystemLogService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseService<Customer, CustomerDTO, CustomerRepository> implements CustomerService {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final CustomerContactRepository mvCustomerContactRepository;
    private final CustomerContactService mvCustomerContactService;
    private final OrderRepository mvOrderRepository;
    private final ConfigService mvConfigService;
    private final UserSession userSession;
    private final SystemLogService mvSystemLogService;
    private final ModelMapper mvModelMapper;

    public CustomerServiceImpl(CustomerRepository pEntityRepository, CustomerContactRepository pCustomerContactRepository,
                               CustomerContactService pCustomerContactService, OrderRepository pOrderRepository,
                               ConfigService pConfigService, UserSession pUserSession, SystemLogService pSystemLogService,
                               ModelMapper pModelMapper) {
        super(Customer.class, CustomerDTO.class, pEntityRepository);
        this.mvCustomerContactRepository = pCustomerContactRepository;
        this.mvCustomerContactService = pCustomerContactService;
        this.mvOrderRepository = pOrderRepository;
        this.mvConfigService = pConfigService;
        this.userSession = pUserSession;
        this.mvSystemLogService = pSystemLogService;
        this.mvModelMapper = pModelMapper;
    }

    @Override
    public Page<CustomerDTO> findAll(int pageSize, int pageNum, String name, String sex, Date birthday, String phone, String email, String address) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("customerName").ascending());
        Page<Customer> customers = mvEntityRepository.findAll(name, sex, birthday, phone, email, address, pageable);
        List<CustomerDTO> customerDTOs = super.convertDTOs(customers.getContent());

        for (CustomerDTO lvDTO : customerDTOs) {
            List<Order> lvOrders = mvOrderRepository.findByCustomer(lvDTO.getId());

            int lvTotalPurchaseCount = lvOrders.size();
            LocalDate lvLastOrder = CollectionUtils.isEmpty(lvOrders) ? null : lvOrders.getFirst().getOrderTime().toLocalDate();
            BigDecimal lvTotalPurchaseAmount = OrderUtils.calAmount(lvOrders);
            BigDecimal lvOutstandingBalanceAmount = new BigDecimal("0.01");

            lvDTO.setLastOrder(lvLastOrder);
            lvDTO.setTotalPurchasedCount(lvTotalPurchaseCount);
            lvDTO.setTotalPurchasedAmount(lvTotalPurchaseAmount);
            lvDTO.setOutstandingBalanceAmount(lvOutstandingBalanceAmount);

            setContactDefault(lvDTO);
        }

        return new PageImpl<>(customerDTOs, pageable, customerDTOs.size());
    }

    @Override
    public List<CustomerDTO> findCustomerNewInMonth() {
        List<CustomerDTO> customerDTOs = CustomerDTO.fromCustomers(mvEntityRepository.findCustomerNewInMonth());
        this.setContactDefaults(customerDTOs);
        return customerDTOs;
    }

    @Override
    public Customer findEntById(Long pId, boolean pThrowException) {
        return super.findEntById(pId, pThrowException);
    }

    @Override
    public CustomerDTO findById(Long pId, boolean pThrowException) {
        CustomerDTO lvCustomer = super.findDtoById(pId, pThrowException);
        this.setContactDefaults(List.of(lvCustomer));
        return lvCustomer;
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

        customer.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : userSession.getUserPrincipal().getId());
        customer.setBonusPoints(0);
        customer.setIsBlackList(false);
        customer.setIsVIP(dto.getIsVIP());
        Customer customerInserted = mvEntityRepository.save(customer);

        CustomerContactDTO customerContact = new CustomerContactDTO();
        customerContact.setCustomer(new Customer(customerInserted.getId()));
        customerContact.setValue(dto.getPhoneDefault());
        customerContact.setIsDefault("Y");
        customerContact.setStatus(true);

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

        mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_CUS_C, MasterObject.Customer, "Thêm mới khách hàng", customer.toString());
        LOG.info("Create customer: {}", customer.toString());

        return CustomerDTO.fromCustomer(customerInserted);
    }

    @Transactional
    @Override
    public CustomerDTO update(CustomerDTO pCustomer, Long customerId) {
        Customer lvCustomer = this.findEntById(customerId, true);
        //lvCustomer.set
        //lvCustomer.set
        //lvCustomer.set
        Customer customerUpdated = mvEntityRepository.save(lvCustomer);

        CustomerContact phoneDefault = null;
        CustomerContact emailDefault = null;
        CustomerContact addressDefault = null;
        if (pCustomer.getPhoneDefault() != null || pCustomer.getEmailDefault() != null || pCustomer.getAddressDefault() != null) {
            List<CustomerContact> contacts = mvCustomerContactRepository.findByCustomerId(customerId);
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
                mvCustomerContactService.save(mvModelMapper.map(phoneDefault, CustomerContactDTO.class));
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
                mvCustomerContactService.save(mvModelMapper.map(emailDefault, CustomerContactDTO.class));
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
                mvCustomerContactService.save(mvModelMapper.map(addressDefault, CustomerContactDTO.class));
            }
        }
        mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_CUS_U, MasterObject.Customer, "Cập nhật thông tin khách hàng", pCustomer.toString());
        LOG.info("Update customer info {}", pCustomer.toString());
        return CustomerDTO.fromCustomer(customerUpdated);
    }

    @Override
    public String delete(Long id) {
        Customer customer = this.findEntById(id, true);
        List<Order> orderOfCustomer = mvOrderRepository.findByCustomer(customer.getId());
        if (ObjectUtils.isNotEmpty(orderOfCustomer)) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }
        mvEntityRepository.deleteById(customer.getId());

        mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_CUS_D, MasterObject.Customer, "Xóa khách hàng", customer.getCustomerName());
        LOG.info("Deleted customer id={}", customer.getId());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private void setContactDefaults(List<CustomerDTO> customerDTOs) {
        for (CustomerDTO c : customerDTOs) {
            setContactDefault(c);
        }
    }

    private void setContactDefault(CustomerDTO pCustomerDTO) {
        CustomerContactDTO phoneDefault = mvCustomerContactService.findContactUseDefault(pCustomerDTO.getId(), ContactType.P);
        CustomerContactDTO emailDefault = mvCustomerContactService.findContactUseDefault(pCustomerDTO.getId(), ContactType.E);
        CustomerContactDTO addressDefault = mvCustomerContactService.findContactUseDefault(pCustomerDTO.getId(), ContactType.A);
        pCustomerDTO.setPhoneDefault(phoneDefault != null ? phoneDefault.getValue() : "");
        pCustomerDTO.setEmailDefault(emailDefault != null ? emailDefault.getValue() : "");
        pCustomerDTO.setAddressDefault(addressDefault != null ? addressDefault.getValue() : "");
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
        Page<Customer> customers = mvEntityRepository.findVIPCustomers(pageable);
        List<CustomerDTO> customerDTOs = CustomerDTO.fromCustomers(customers.getContent());
        this.setContactDefaults(customerDTOs);

        return new PageImpl<>(customerDTOs, pageable, customerDTOs.size());
    }

    @Override
    public Page<CustomerDTO> getBlackListCustomers(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Customer> customers = mvEntityRepository.findBlackListCustomers(pageable);
        List<CustomerDTO> customerDTOs = CustomerDTO.fromCustomers(customers.getContent());
        this.setContactDefaults(customerDTOs);

        return new PageImpl<>(customerDTOs, pageable, customerDTOs.size());
    }
}