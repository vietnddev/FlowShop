package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.ICurdService;
import com.flowiee.pms.modules.sales.entity.Customer;
import com.flowiee.pms.modules.sales.model.PurchaseHistory;
import com.flowiee.pms.modules.sales.dto.CustomerDTO;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface CustomerService extends ICurdService<CustomerDTO> {
    Customer findEntById(Long pCustomerId, boolean pThrowException);

    Page<CustomerDTO> findAll(int pageSize, int pageNum, String name, String sex, Date birthday, String phone, String email, String address);

    List<CustomerDTO> findCustomerNewInMonth();

    List<PurchaseHistory> findPurchaseHistory(Long customerId, Integer year, Integer month);

    Page<CustomerDTO> getVIPCustomers(int pageSize, int pageNum);

    Page<CustomerDTO> getBlackListCustomers(int pageSize, int pageNum);
}