package com.flowiee.pms.controller.sales;

import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.service.sales.*;
import com.flowiee.pms.base.controller.BaseController;
import com.flowiee.pms.service.category.CategoryService;

import com.flowiee.pms.common.enumeration.CategoryType;
import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderControllerView extends BaseController {
    OrderReadService mvOrderReadService;
    OrderWriteService mvOrderWriteService;
    CategoryService mvCategoryService;
    OrderItemsService mvOrderItemsService;
    VoucherTicketService mvVoucherTicketService;
    OrderPrintInvoiceService mvPrintInvoiceService;

    List<OrderStatus> mvOrderStatusCanModifyItem = List.of(OrderStatus.PEND, OrderStatus.CONF, OrderStatus.PROC);
    List<OrderStatus> mvOrderStatusCanDeleteItem = List.of(OrderStatus.PEND, OrderStatus.CONF, OrderStatus.PROC);
    List<OrderStatus> mvOrderStatusDoesNotAllowModify = List.of(OrderStatus.DLVD, OrderStatus.CNCL, OrderStatus.RTND);

    @GetMapping
    @PreAuthorize("@vldModuleSales.readOrder(true)")
    public ModelAndView viewAllOrders() {
        setupSearchTool(true, List.of("BRANCH", CategoryType.GROUP_CUSTOMER, CategoryType.PAYMENT_METHOD, CategoryType.ORDER_STATUS, CategoryType.SALES_CHANNEL, "DATE_FILTER"));
        return baseView(new ModelAndView(Pages.PRO_ORDER.getTemplate()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleSales.readOrder(true)")
    public ModelAndView findDonHangDetail(@PathVariable("id") Long orderId) {
        OrderDTO lvOrderDetail = OrderDTO.fromOrder(mvOrderReadService.findById(orderId, true));
        OrderStatus lvOrderStatus = lvOrderDetail.getOrderStatus();

        List<OrderStatus> orderStatusList = new ArrayList<>(List.of(lvOrderStatus));
        orderStatusList.addAll(OrderStatus.getAll(lvOrderStatus));

        Map<String, String> statusMap = new LinkedHashMap<>();
        for (OrderStatus status : orderStatusList) {
            statusMap.put(status.name(), status.getName());
        }

        ModelAndView modelAndView = new ModelAndView(Pages.PRO_ORDER_DETAIL.getTemplate());
        modelAndView.addObject("orderDetailId", orderId);
        modelAndView.addObject("orderDetail", lvOrderDetail);
        modelAndView.addObject("listOrderDetail", lvOrderDetail.getListOrderDetailDTO());
        modelAndView.addObject("listPaymentMethod", mvCategoryService.findSubCategory(CategoryType.PAYMENT_METHOD, null, null, -1, -1).getContent());
        modelAndView.addObject("orderStatus", statusMap);
        modelAndView.addObject("allowEditItem", mvOrderStatusCanModifyItem.contains(lvOrderStatus));
        modelAndView.addObject("allowEditGeneral", !mvOrderStatusDoesNotAllowModify.contains(lvOrderStatus));
        modelAndView.addObject("allowDoPay", !mvOrderStatusDoesNotAllowModify.contains(lvOrderStatus));

        return baseView(modelAndView);
    }
    
    @PostMapping("/ban-hang/cart/add-voucher/{code}")
    @PreAuthorize("@vldModuleSales.readVoucher(true)")
    public ModelAndView checkToUse(@PathVariable("code") String code) {
        ModelAndView modelAndView = new ModelAndView("redirect:/order/ban-hang");
        modelAndView.addObject("ticket_code", code);
        modelAndView.addObject("ticket_status", mvVoucherTicketService.checkTicketToUse(code));
        modelAndView.addObject("ticket_info", mvVoucherTicketService.findTicketByCode(code));
        return modelAndView;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSales.deleteOrder(true)")
    public ModelAndView delete(@PathVariable("id") Long orderId) {
        mvOrderWriteService.deleteOrder(orderId);
        return new ModelAndView("redirect:/order");
    }

    @GetMapping("/abc")
    public ModelAndView getOrderInfoByScanQRCode() {
        return null;
    }

    @GetMapping("/print-invoice/{id}")
    public void exportToPDF(@PathVariable("id") Long pOrderId, HttpServletResponse response) {
        try {
            Order lvOrder = mvOrderReadService.findById(pOrderId, true);
            mvPrintInvoiceService.printInvoicePDF(lvOrder, null, true, response);
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @PostMapping("/{orderId}/item/add")
    @PreAuthorize("@vldModuleSales.updateOrder(true)")
    public ModelAndView addItem(@PathVariable("orderId") long orderId, @RequestParam("productVariantSelectedId") String[] productVariantSelectedId) {
        Order lvOrder = mvOrderReadService.findById(orderId, true);
        if (!mvOrderStatusCanModifyItem.contains(lvOrder.getOrderStatus())) {
            throw new BadRequestException("Đơn hàng đang ở trạng thái không cho phép chỉnh sửa!");
        }
        if (productVariantSelectedId == null || productVariantSelectedId.length == 0) {
            throw new BadRequestException("Vui lòng chọn sản phẩm!");
        }
        mvOrderItemsService.save(lvOrder, Arrays.stream(productVariantSelectedId).toList());
        return new ModelAndView("redirect:/order/" + orderId);
    }

    @PostMapping("/{orderId}/item/update/{itemId}")
    @PreAuthorize("@vldModuleSales.updateOrder(true)")
    public ModelAndView updateItem(@PathVariable("orderId") long orderId, @PathVariable("itemId") long itemId, @ModelAttribute("items") OrderDetail item) {
        Order lvOrder = mvOrderReadService.findById(orderId, true);
        if (!mvOrderStatusCanModifyItem.contains(lvOrder.getOrderStatus())) {
            throw new BadRequestException("Đơn hàng đang ở trạng thái không cho phép chỉnh sửa!");
        }
        if (item.getQuantity() == 0) {
            throw new BadRequestException("Số lượng không được nhỏ hơn 1!");
        }
        mvOrderItemsService.update(item, itemId);
        return new ModelAndView("redirect:/order/" + orderId);
    }

    @PostMapping("/{orderId}/item/delete/{itemId}")
    @PreAuthorize("@vldModuleSales.updateOrder(true)")
    public ModelAndView deleteItem(@PathVariable("orderId") long orderId, @PathVariable("itemId") long itemId) {
        Order lvOrder = mvOrderReadService.findById(orderId, true);
        if (!mvOrderStatusCanDeleteItem.contains(lvOrder.getOrderStatus())) {
            throw new BadRequestException("Đơn hàng đang ở trạng thái không cho phép xóa!");
        }
        mvOrderItemsService.delete(itemId);
        return new ModelAndView("redirect:/order/" + orderId);
    }
}