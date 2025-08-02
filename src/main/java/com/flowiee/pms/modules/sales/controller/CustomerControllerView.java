package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.sales.dto.CustomerContactDTO;
import com.flowiee.pms.modules.sales.dto.CustomerDTO;
import com.flowiee.pms.modules.sales.model.OrderReq;
import com.flowiee.pms.modules.sales.service.CustomerContactService;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.sales.service.CustomerService;
import com.flowiee.pms.modules.sales.service.OrderService;

import com.flowiee.pms.common.enumeration.ContactType;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.common.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/customer")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerControllerView extends BaseController {
    OrderService mvOrderService;
    CustomerService mvCustomerService;
    CustomerContactService mvCustomerContactService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.readCustomer(true)")
    public ModelAndView searchCustomer(@Nullable @RequestParam("name") String name,
                                       @Nullable @RequestParam("sex") String sex,
                                       @Nullable @RequestParam("birthday") String birthday,
                                       @Nullable @RequestParam("phone") String phone,
                                       @Nullable @RequestParam("email") String email,
                                       @Nullable @RequestParam("address") String address) {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_CUSTOMER.getTemplate());
        modelAndView.addObject("customer", new CustomerDTO());
        modelAndView.addObject("listCustomer", mvCustomerService.find(-1, -1, name, sex, birthday != null ? DateUtils.convertStringToDate(birthday, "YYYY/MM/dd") : null, phone, email, address));
        modelAndView.addObject("filter_name", name);
        modelAndView.addObject("filter_sex", sex);
        modelAndView.addObject("filter_birthday", birthday);
        modelAndView.addObject("filter_phone", phone);
        modelAndView.addObject("filter_email", email);
        modelAndView.addObject("filter_address", address);
        return baseView(modelAndView);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleSales.readCustomer(true)")
    public ModelAndView findCustomerDetail(@PathVariable("id") Long customerId) {
        CustomerDTO customerDTO = mvCustomerService.findById(customerId, true);

        List<CustomerContactDTO> listContacts = mvCustomerContactService.findContacts(customerId);
        listContacts.forEach(c -> {
            if (c.isPhoneContact()) c.setCode(ContactType.P.getLabel());
            if (c.isEmailContact()) c.setCode(ContactType.E.getLabel());
            if (c.isAddressContact()) c.setCode(ContactType.A.getLabel());
        });

        OrderReq lvOrderReq = OrderReq.builder().customerId(customerId).build();
        lvOrderReq.setPageNum(-1);
        lvOrderReq.setPageSize(-1);

        ModelAndView modelAndView = new ModelAndView(Pages.PRO_CUSTOMER_DETAIL.getTemplate());
        modelAndView.addObject("customerDetail", customerDTO);
        modelAndView.addObject("listCustomerContact", listContacts);
        modelAndView.addObject("listDonHang", mvOrderService.find(lvOrderReq).getContent());
        return baseView(modelAndView);
    }

    @PostMapping("/insert")
    @PreAuthorize("@vldModuleSales.insertCustomer(true)")
    public ModelAndView insertCustomer(@ModelAttribute CustomerDTO customer, HttpServletRequest request) {
        try {
            mvCustomerService.save(customer);
            return new ModelAndView("redirect:/customer");
        } catch (AppException e) {
            return refreshPage(request);
        }
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public ModelAndView updateCustomer(@ModelAttribute("customer") CustomerDTO customer, @PathVariable("id") Long customerId) {
        if (customer == null || customerId <= 0 || mvCustomerService.findById(customerId, true) == null) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        mvCustomerService.update(customer, customerId);
        return new ModelAndView("redirect:/customer");
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSales.deleteCustomer(true)")
    public ModelAndView deleteCustomer(@PathVariable("id") Long id) {
        mvCustomerService.delete(id);
        return new ModelAndView("redirect:/customer");
    }

    @PostMapping("/contact/insert")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public ModelAndView insertCustomerContact(@ModelAttribute("customerContact") CustomerContactDTO customerContact, HttpServletRequest request) {
        if (customerContact == null || customerContact.getCustomer() == null) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        mvCustomerContactService.save(customerContact);
        return refreshPage(request);
    }

    @PostMapping("/contact/update/{id}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public ModelAndView updateCustomerContact(@ModelAttribute("customerContact") CustomerContactDTO customerContact,
                                              @PathVariable("id") Long customerContactId,
                                              HttpServletRequest request) {
        if (customerContact == null || customerContact.getCustomer() == null) {
            throw new ResourceNotFoundException("Customer not found!");
        }
        mvCustomerContactService.update(customerContact, customerContactId);
        return refreshPage(request);
    }

    @PostMapping("/contact/delete/{id}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public ModelAndView updateCustomerContact(@PathVariable("id") Long customerContactId, HttpServletRequest request) {
        mvCustomerContactService.delete(customerContactId);
        return refreshPage(request);
    }

    @PostMapping("/contact/use-default/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public ModelAndView setCustomerContactUseDefault(@RequestParam("customerId") Long customerId,
                                                     @RequestParam("contactCode") String contactCode,
                                                     @PathVariable("contactId") Long contactId,
                                                     HttpServletRequest request) {
        mvCustomerContactService.enableContactUseDefault(customerId, contactCode, contactId);
        return refreshPage(request);
    }

    @PostMapping("/contact/undefault/{contactId}")
    @PreAuthorize("@vldModuleSales.updateCustomer(true)")
    public ModelAndView setCustomerContactUnUseDefault(@PathVariable("contactId") Long contactId, HttpServletRequest request) {
        if (contactId <= 0 || mvCustomerContactService.findById(contactId, true) == null) {
            throw new ResourceNotFoundException("Customer contact not found!");
        }
        mvCustomerContactService.disableContactUnUseDefault(contactId);
        return refreshPage(request);
    }
}