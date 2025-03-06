package com.flowiee.pms.service.sales;

import com.flowiee.pms.base.BaseCurdService;
import com.flowiee.pms.model.PurchaseHistory;
import com.flowiee.pms.model.dto.CustomerDTO;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface CustomerService extends BaseCurdService<CustomerDTO> {
    Page<CustomerDTO> findAll(int pageSize, int pageNum, String name, String sex, Date birthday, String phone, String email, String address);

    List<CustomerDTO> findCustomerNewInMonth();

    List<PurchaseHistory> findPurchaseHistory(Long customerId, Integer year, Integer month);

    Page<CustomerDTO> getVIPCustomers(int pageSize, int pageNum);

    Page<CustomerDTO> getBlackListCustomers(int pageSize, int pageNum);
}