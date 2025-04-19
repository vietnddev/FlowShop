package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.dto.CustomerContactDTO;
import com.flowiee.pms.modules.sales.service.CustomerContactService;
import com.flowiee.pms.modules.sales.service.CustomerService;
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
    ControllerHelper mvCHelper;

    @Operation(summary = "Find contacts of customer")
    @GetMapping("/{customerId}/contact")
    @PreAuthorize("@vldModuleSales.readCustomer(true)")
    public AppResponse<List<CustomerContactDTO>> findContactsOfCustomer(@PathVariable("customerId") Long customerId) {
        try {
            if (customerId <= 0 || mvCustomerService.findById(customerId, true) == null) {
                throw new BadRequestException();
            }
            List<CustomerContactDTO> listContacts = mvCustomerContactService.findContacts(customerId);
            for (CustomerContactDTO c : listContacts) {
                if (c.isPhoneContact()) c.setCode(ContactType.P.getLabel());
                if (c.isEmailContact()) c.setCode(ContactType.E.getLabel());
                if (c.isAddressContact()) c.setCode(ContactType.A.getLabel());
            }
            return mvCHelper.success(listContacts);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Create contact")
    @PostMapping("/contact/insert")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContactDTO> insertContact(@RequestBody CustomerContactDTO customerContact) {
        try {
            if (customerContact == null || customerContact.getCustomer() == null) {
                throw new BadRequestException();
            }
            return mvCHelper.success(mvCustomerContactService.save(customerContact));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Update contact")
    @PutMapping("/contact/update/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContactDTO> updateContact(@RequestBody CustomerContactDTO customerContact, @PathVariable("contactId") Long contactId) {
        try {
            if (customerContact == null || customerContact.getCustomer() == null || mvCustomerContactService.findById(contactId, true) == null) {
                throw new BadRequestException();
            }
            return mvCHelper.success(mvCustomerContactService.update(customerContact, contactId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Delete contact")
    @DeleteMapping("/contact/delete/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<String> deleteContact(@PathVariable("contactId") Long contactId) {
        return mvCHelper.success(mvCustomerContactService.delete(contactId));
    }

    @Operation(summary = "Update contact use default")
    @PatchMapping("/contact/use-default/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContactDTO> setContactUseDefault(@RequestParam("customerId") Long customerId,
                                                                @RequestParam("contactCode") String contactCode,
                                                                @PathVariable("contactId") Long contactId) {
        try {
            if (customerId <= 0 || contactId <= 0 || mvCustomerService.findById(customerId, true) == null || mvCustomerContactService.findById(contactId, true) == null) {
                throw new BadRequestException();
            }
            return mvCHelper.success(mvCustomerContactService.enableContactUseDefault(customerId, contactCode, contactId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }

    @Operation(summary = "Update contact un-use default")
    @PatchMapping("/contact/undefault/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public AppResponse<CustomerContactDTO> setContactUnUseDefault(@PathVariable("contactId") Long contactId) {
        try {
            if (contactId <= 0 || mvCustomerContactService.findById(contactId, true) == null) {
                throw new BadRequestException();
            }
            return mvCHelper.success(mvCustomerContactService.disableContactUnUseDefault(contactId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "contact"), ex);
        }
    }
}