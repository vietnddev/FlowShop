package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.model.PurchaseHistory;
import com.flowiee.pms.modules.sales.dto.CustomerDTO;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.service.CustomerService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.MessageCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/customer")
@Tag(name = "Customer API", description = "Quản lý khách hàng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerController extends BaseController {
    CustomerService mvCustomerService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Find all customers")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSales.readCustomer(true)")
    public AppResponse<List<CustomerDTO>> findCustomers(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                        @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                        @RequestParam(value = "name", required = false) String pName,
                                                        @RequestParam(value = "sex", required = false) String pSex,
                                                        @RequestParam(value = "birthday", required = false) Date pBirthday,
                                                        @RequestParam(value = "phone", required = false) String pPhone,
                                                        @RequestParam(value = "email", required = false) String pEmail,
                                                        @RequestParam(value = "address", required = false) String pAddress) {
        try {
            if (pageSize == null) pageSize = -1;
            if (pageNum == null) pageNum = 1;
            Page<CustomerDTO> customers = mvCustomerService.findAll(pageSize, pageNum - 1, pName, pSex, pBirthday, pPhone, pEmail, pAddress);
            return mvCHelper.success(customers.getContent(), pageNum, pageSize, customers.getTotalPages(), customers.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "customer"), ex);
        }
    }

    @Operation(summary = "Find detail customer")
    @GetMapping("/{customerId}")
    @PreAuthorize("@vldModuleSales.readCustomer(true)")
    public AppResponse<CustomerDTO> findDetailCustomer(@PathVariable("customerId") Long customerId) {
        CustomerDTO customer = mvCustomerService.findById(customerId, true);
        if (customer == null) {
            throw new ResourceNotFoundException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "customer"));
        }
        return mvCHelper.success(customer);
    }

    @Operation(summary = "Create customer")
    @PostMapping("/insert")
    @PreAuthorize("@vldModuleSales.insertCustomer(true)")
    public AppResponse<String> createCustomer(@RequestBody CustomerDTO customer) {
        try {
            if (customer == null) {
                throw new BadRequestException();
            }
            mvCustomerService.save(customer);
            return mvCHelper.success(MessageCode.CREATE_SUCCESS.getDescription());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "customer"), ex);
        }
    }

    @Operation(summary = "Update customer")
    @PutMapping("/update/{customerId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerDTO> updateCustomer(@RequestBody CustomerDTO customer, @PathVariable("customerId") Long customerId) {
        return mvCHelper.success(mvCustomerService.update(customer, customerId));
    }

    @Operation(summary = "Delete customer")
    @DeleteMapping("/delete/{customerId}")
    @PreAuthorize("@vldModuleSales.deleteCustomer(true)")
    public AppResponse<String> deleteCustomer(@PathVariable("customerId") Long customerId) {
        return mvCHelper.success(mvCustomerService.delete(customerId));
    }

    @Operation(summary = "Find the number of purchase of customer per month")
    @GetMapping("/purchase-history/{customerId}")
    @PreAuthorize("@vldModuleSales.readCustomer(true)")
    public AppResponse<List<PurchaseHistory>> findPurchaseHistory(@PathVariable("customerId") Long customerId, @RequestParam(value = "year", required = false) Integer year) {
        if (mvCustomerService.findById(customerId, true) == null) {
            throw new BadRequestException("Customer not found");
        }
        return mvCHelper.success(mvCustomerService.findPurchaseHistory(customerId, year, null));
    }
}