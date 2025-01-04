package com.flowiee.pms.controller.sales;

import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.entity.sales.CustomerContact;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.service.sales.CustomerContactService;
import com.flowiee.pms.service.sales.CustomerService;
import com.flowiee.pms.common.enumeration.ContactType;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/customer")
@Tag(name = "Customer API", description = "Quản lý thông tin liên hệ khách hàng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerContactController extends BaseController {
    CustomerService mvCustomerService;
    CustomerContactService mvCustomerContactService;

    @Operation(summary = "Find contacts of customer")
    @GetMapping("/{customerId}/contact")
    @PreAuthorize("@vldModuleSales.readCustomer(true)")
    public AppResponse<List<CustomerContact>> findContactsOfCustomer(@PathVariable("customerId") Long customerId) {
        try {
            if (customerId <= 0 || mvCustomerService.findById(customerId, true) == null) {
                throw new BadRequestException();
            }
            List<CustomerContact> listContacts = mvCustomerContactService.findContacts(customerId);
            for (CustomerContact c : listContacts) {
                if (c.isPhoneContact()) c.setCode(ContactType.P.getLabel());
                if (c.isEmailContact()) c.setCode(ContactType.E.getLabel());
                if (c.isAddressContact()) c.setCode(ContactType.A.getLabel());
            }
            return success(listContacts);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Create contact")
    @PostMapping("/contact/insert")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContact> insertContact(@RequestBody CustomerContact customerContact) {
        try {
            if (customerContact == null || customerContact.getCustomer() == null) {
                throw new BadRequestException();
            }
            return success(mvCustomerContactService.save(customerContact));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Update contact")
    @PutMapping("/contact/update/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContact> updateContact(@RequestBody CustomerContact customerContact, @PathVariable("contactId") Long contactId) {
        try {
            if (customerContact == null || customerContact.getCustomer() == null || mvCustomerContactService.findById(contactId, true) == null) {
                throw new BadRequestException();
            }
            return success(mvCustomerContactService.update(customerContact, contactId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Delete contact")
    @DeleteMapping("/contact/delete/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<String> deleteContact(@PathVariable("contactId") Long contactId) {
        return success(mvCustomerContactService.delete(contactId));
    }

    @Operation(summary = "Update contact use default")
    @PatchMapping("/contact/use-default/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContact> setContactUseDefault(@RequestParam("customerId") Long customerId,
                                                             @RequestParam("contactCode") String contactCode,
                                                             @PathVariable("contactId") Long contactId) {
        try {
            if (customerId <= 0 || contactId <= 0 || mvCustomerService.findById(customerId, true) == null || mvCustomerContactService.findById(contactId, true) == null) {
                throw new BadRequestException();
            }
            return success(mvCustomerContactService.enableContactUseDefault(customerId, contactCode, contactId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Update contact un-use default")
    @PatchMapping("/contact/undefault/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContact> setContactUnUseDefault(@PathVariable("contactId") Long contactId) {
        try {
            if (contactId <= 0 || mvCustomerContactService.findById(contactId, true) == null) {
                throw new BadRequestException();
            }
            return success(mvCustomerContactService.disableContactUnUseDefault(contactId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }
}