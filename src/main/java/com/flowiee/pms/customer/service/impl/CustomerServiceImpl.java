package com.flowiee.pms.customer.service.impl;

import com.flowiee.pms.customer.enums.ContactType;
import com.flowiee.pms.customer.mapper.CustomerConvert;
import com.flowiee.pms.customer.model.CustomerRequest;
import com.flowiee.pms.customer.model.CustomerSummaryProjection;
import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.exception.EntityNotFoundException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.customer.dto.CustomerContactDTO;
import com.flowiee.pms.customer.service.CustomerContactService;
import com.flowiee.pms.customer.service.CustomerService;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.ledger.model.PurchaseHistory;
import com.flowiee.pms.customer.dto.CustomerDTO;
import com.flowiee.pms.customer.entity.CustomerContact;
import com.flowiee.pms.shared.exception.DataInUseException;
import com.flowiee.pms.customer.entity.Customer;
import com.flowiee.pms.customer.repository.CustomerContactRepository;
import com.flowiee.pms.customer.repository.CustomerRepository;
import com.flowiee.pms.order.repository.OrderRepository;

import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.system.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl extends BaseService<Customer, CustomerDTO, CustomerRepository> implements CustomerService {
    private final CustomerContactRepository mvCustomerContactRepository;
    private final CustomerContactService mvCustomerContactService;
    private final OrderRepository mvOrderRepository;
    private final SystemLogService mvSystemLogService;

    public CustomerServiceImpl(CustomerRepository pEntityRepository, CustomerContactRepository pCustomerContactRepository,
                               CustomerContactService pCustomerContactService, OrderRepository pOrderRepository,
                               SystemLogService pSystemLogService) {
        super(Customer.class, CustomerDTO.class, pEntityRepository);
        this.mvCustomerContactRepository = pCustomerContactRepository;
        this.mvCustomerContactService = pCustomerContactService;
        this.mvOrderRepository = pOrderRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public Page<CustomerDTO> find(int pageSize, int pageNum, CustomerRequest pRequest) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("customerName").ascending());
        List<CustomerSummaryProjection> lvCustomerSummaryProjectionList = mvEntityRepository.findCustomerSummaryProjection(pRequest.getCustomerIds(),
                CoreUtils.trim(pRequest.getName()), pRequest.getSex(), pRequest.getBirthday(), CoreUtils.trim(pRequest.getPhone()),
                CoreUtils.trim(pRequest.getEmail()), CoreUtils.trim(pRequest.getAddress()), pRequest.getIsVIP(), pRequest.getIsBlackList(),
                pRequest.getCreatedAtFrom(), pRequest.getCreatedAtTo(), pageable).getContent();
        List<CustomerDTO> lvCustomerDTOs = CustomerConvert.toDTOs(lvCustomerSummaryProjectionList);

        return new PageImpl<>(lvCustomerDTOs, pageable, lvCustomerDTOs.size());
    }

    @Override
    public List<CustomerDTO> findCustomerNewInMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().getDayOfMonth());
        return this.find(9999, 0, CustomerRequest.builder()
                .createdAtFrom(startOfMonth)
                .createdAtTo(endOfMonth)
                .build())
                .getContent();
    }

    @Override
    public CustomerDTO findById(Long pId, boolean pThrowException) {
        List<CustomerDTO> lvDTOs = this.find(1, 0, CustomerRequest.builder()
                .customerIds(List.of(pId))
                .build())
                .getContent();
        if (CollectionUtils.isEmpty(lvDTOs) && pThrowException) {
            throw new EntityNotFoundException(
                    new Object[]{String.format("%s with Id %s", Customer.class.getSimpleName(), pId)},
                    null, null
            );
        }
        return lvDTOs.get(0);
    }

    @Transactional
    @Override
    public CustomerDTO save(CustomerDTO dto) {
        validateCustomerCreationInput(dto);

        Customer customerInserted = mvEntityRepository.save(Customer.builder()
                .customerName(dto.getCustomerName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .isBlackList(false)
                .bonusPoints(0)
                .isVIP(false)
                .build());

        saveContactIfPresent(customerInserted.getId(), CoreUtils.trim(dto.getPhoneDefault()), ContactType.P);
        saveContactIfPresent(customerInserted.getId(), CoreUtils.trim(dto.getEmailDefault()), ContactType.E);
        saveContactIfPresent(customerInserted.getId(), CoreUtils.trim(dto.getAddressDefault()), ContactType.A);

        mvSystemLogService.writeLogCreate(ACTION.PRO_CUS_C, MasterObject.Customer, "Thêm mới khách hàng", customerInserted.getCustomerName());
        log.info("Created customer: {}", customerInserted.toString());

        return CustomerConvert.toDto(customerInserted);
    }

    private void validateCustomerCreationInput(CustomerDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Customer request is invalid!");
        }

        String customerName = CoreUtils.trim(dto.getCustomerName());
        if (CoreUtils.isNullStr(customerName)) {
            throw new BadRequestException("Customer name cannot be empty!");
        }
        if (CoreUtils.isAnySpecialCharacter(customerName)) {
            throw new BadRequestException("Customer name cannot contain special characters!");
        }

        LocalDate birthday = dto.getDateOfBirth();
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new BadRequestException("Date of birth cannot be in the future!");
        }
    }

    private void saveContactIfPresent(Long customerId, String value, ContactType type) {
        if (CoreUtils.isNullStr(value)) {
            return;
        }

        CustomerContactDTO contact = new CustomerContactDTO();
        contact.setCustomer(new Customer(customerId));
        contact.setCode(type.name());
        contact.setValue(value);
        contact.setIsDefault("Y");
        contact.setStatus(true);

        mvCustomerContactService.save(contact);
    }

    @Transactional
    @Override
    public CustomerDTO update(CustomerDTO pCustomer, Long customerId) {
        Customer lvCustomer = this.findEntById(customerId, true);
        lvCustomer.setCustomerName(pCustomer.getCustomerName());
        lvCustomer.setGender(pCustomer.getGender());
        lvCustomer.setDateOfBirth(pCustomer.getDateOfBirth());
        lvCustomer.setIsVIP(pCustomer.getIsVIP());
        Customer customerUpdated = mvEntityRepository.save(lvCustomer);

        updateContactIfPresent(lvCustomer.getId(), pCustomer.getPhoneDefault(), ContactType.P);
        updateContactIfPresent(lvCustomer.getId(), pCustomer.getEmailDefault(), ContactType.E);
        updateContactIfPresent(lvCustomer.getId(), pCustomer.getAddressDefault(), ContactType.A);

        mvSystemLogService.writeLogUpdate(ACTION.PRO_CUS_U, MasterObject.Customer, "Cập nhật thông tin khách hàng", pCustomer.toString());
        log.info("Update customer info {}", pCustomer.toString());

        return CustomerConvert.toDto(customerUpdated);
    }

    private void updateContactIfPresent(Long pCustomerId, String pValue, ContactType pType) {
        if (CoreUtils.isNullStr(pValue)) {
            return;
        }

        String lvNewValue = CoreUtils.trim(pValue);

        CustomerContact lvCurrentContact = mvCustomerContactRepository.findContactDefault(pCustomerId, pType.name());
        if (lvCurrentContact == null) {
            saveContactIfPresent(pCustomerId, CoreUtils.trim(lvNewValue), pType);
        } else {
            String lvCurrentValue = lvCurrentContact.getValue();
            if (!lvCurrentValue.equals(lvNewValue)) {
                lvCurrentContact.setValue(lvNewValue);
                mvCustomerContactRepository.save(lvCurrentContact);
            }
        }
    }

    @Override
    public boolean delete(Long id) {
        CustomerDTO customer = this.findById(id, true);
        if (customer.getTotalOrders() > 0) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        mvEntityRepository.deleteById(customer.getId());

        mvSystemLogService.writeLogDelete(ACTION.PRO_CUS_D, MasterObject.Customer, "Delete customer/ Xóa khách hàng", customer.getCustomerName());
        log.info("Deleted customer id={}", customer.getId());

        return true;
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
        return this.find(pageSize, pageNum, CustomerRequest.builder().isVIP(true).build());
    }

    @Override
    public Page<CustomerDTO> getBlackListCustomers(int pageSize, int pageNum) {
        return this.find(pageSize, pageNum, CustomerRequest.builder().isBlackList(true).build());
    }
}