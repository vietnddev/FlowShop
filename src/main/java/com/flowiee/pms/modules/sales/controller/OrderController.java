package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.DateTimeUtil;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.system.model.EximResult;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.modules.sales.model.CreateOrderReq;
import com.flowiee.pms.modules.sales.model.UpdateOrderReq;
import com.flowiee.pms.modules.system.service.ExportService;
import com.flowiee.pms.modules.sales.service.OrderPayService;
import com.flowiee.pms.modules.sales.service.OrderProcessService;
import com.flowiee.pms.modules.sales.service.OrderReadService;
import com.flowiee.pms.modules.sales.service.OrderWriteService;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.OrderStatus;
import com.flowiee.pms.common.enumeration.TemplateExport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sls/order")
@Tag(name = "Order API", description = "Quản lý đơn hàng")
@RequiredArgsConstructor
public class OrderController extends BaseController {
    private final OrderProcessService mvOrderProcessService;
    private final OrderReadService mvOrderReadService;
    private final OrderWriteService mvOrderWriteService;
    private final OrderPayService mvOrderPayService;
    @Autowired
    @NonFinal
    @Qualifier("orderExportServiceImpl")
    private ExportService mvExportService;
    private final ControllerHelper mvCHelper;

    @Operation(summary = "Find all orders")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSales.readOrder(true)")
    public AppResponse<List<OrderDTO>> findAllOrders(@RequestParam(value = "orderId", required = false) Long pOrderId,
                                                     @RequestParam(value = "paymentMethodId", required = false) Long pPaymentMethodId,
                                                     @RequestParam(value = "orderStatusId", required = false) OrderStatus pOrderStatus,
                                                     @RequestParam(value = "salesChannelId", required = false) Long pSalesChannelId,
                                                     @RequestParam(value = "groupCustomerId", required = false) Long pGroupCustomerId,
                                                     @RequestParam(value = "sellerId", required = false) Long pSellerId,
                                                     @RequestParam(value = "customerId", required = false) Long pCustomerId,
                                                     @RequestParam(value = "branchId", required = false) Long pBranchId,
                                                     @RequestParam(value = "dateFilter", required = false) String pDateFilter,
                                                     @RequestParam(value = "txtSearch", required = false) String pTxtSearch,
                                                     @RequestParam(name = "fromDate", required = false) String pFromDate,
                                                     @RequestParam(name = "toDate", required = false) String pToDate,
                                                     @RequestParam(name = Constants.PAGE_SIZE, required = false, defaultValue = Constants.DEFAULT_PSIZE) int pageSize,
                                                     @RequestParam(name = Constants.PAGE_NUM, required = false, defaultValue = Constants.DEFAULT_PNUM) int pageNum) {
        try {
            LocalDateTime lvFromDate = DateTimeUtil.MIN_TIME;
            if (!CoreUtils.isNullStr(pFromDate)) {
                lvFromDate = DateTimeUtil.convertStringToDateTime(pFromDate, DateTimeUtil.FORMAT_DATE);
            }
            LocalDateTime lvToDate = DateTimeUtil.MAX_TIME;
            if (!CoreUtils.isNullStr(pFromDate)) {
                lvToDate = DateTimeUtil.convertStringToDateTime(pToDate, DateTimeUtil.FORMAT_DATE);
            }
            Page<OrderDTO> orderPage = mvOrderReadService.findAll(pageSize, pageNum - 1, pTxtSearch, pOrderId, pPaymentMethodId, pOrderStatus, pSalesChannelId, pSellerId, pCustomerId, pBranchId, pGroupCustomerId,
                    pDateFilter, lvFromDate, lvToDate, null);
            return mvCHelper.success(orderPage.getContent(), pageNum, pageSize, orderPage.getTotalPages(), orderPage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "order"), ex);
        }
    }

    @Operation(summary = "Find detail order")
    @GetMapping("/{orderId}")
    @PreAuthorize("@vldModuleSales.readOrder(true)")
    public AppResponse<OrderDTO> findOrderDetail(@PathVariable("orderId") Long pOrderId) {
        OrderDTO lvOrder = mvOrderReadService.findById(pOrderId, true);
        return mvCHelper.success(lvOrder);
    }

    @Operation(summary = "Create new order")
    @PostMapping("/insert")
    public AppResponse<OrderDTO> createOrder(@RequestBody CreateOrderReq request) {
        try {
            return mvCHelper.success(mvOrderWriteService.createOrder(request));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "order") + "\n" + ex.getMessage(), ex);
        }
    }

    @Operation(summary = "Update order")
    @PutMapping("/update/{orderId}")
    @PreAuthorize("@vldModuleSales.updateOrder(true)")
    public AppResponse<OrderDTO> update(@RequestBody UpdateOrderReq pRequest, @PathVariable("orderId") Long pOrderId) {
        return mvCHelper.success(mvOrderWriteService.updateOrder(pRequest, pOrderId));
    }

    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("@vldModuleSales.deleteOrder(true)")
    public AppResponse<String> deleteOrder(@PathVariable("orderId") Long orderId) {
        //Check them trang thai
        return mvCHelper.success(mvOrderWriteService.deleteOrder(orderId));
    }

    @PutMapping("/do-pay/{orderId}")
    @PreAuthorize("@vldModuleSales.updateOrder(true)")
    public AppResponse<String> doPayOrder(@PathVariable("orderId") Long orderId,
                                          @RequestParam(value = "paymentTime", required = false) String paymentTime,
                                          @RequestParam("paymentMethod") Long paymentMethod,
                                          @RequestParam("paymentAmount") BigDecimal paymentAmount,
                                          @RequestParam(value = "paymentNote", required = false) String paymentNote) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        return mvCHelper.success(mvOrderPayService.doPay(orderId, LocalDateTime.parse(paymentTime, formatter), paymentMethod, paymentAmount, paymentNote));
    }

    @GetMapping("/export")
    @PreAuthorize("@vldModuleSales.readOrder(true)")
    public ResponseEntity<InputStreamResource> exportToExcel() {
        EximResult model = mvExportService.exportToExcel(TemplateExport.EX_LIST_OF_ORDERS, null, false);
        return ResponseEntity.ok().headers(model.getHttpHeaders()).body(model.getContent());
    }

    @GetMapping("/scan/QR-Code/{code}")
    public ModelAndView findOrderInfoByQRCode(@PathVariable("code") String pOrderCode) {
        try {
            Order lvOrder = mvOrderReadService.getOrderByCode(pOrderCode);
            if (lvOrder == null) {
                throw new EntityNotFoundException(new Object[]{"order"}, null, null);
            }
            return new ModelAndView().addObject("orderInfo", lvOrder);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "scan order"), ex);
        }
    }

    @PostMapping("/process/{type}/{orderId}")
    @PreAuthorize("@vldModuleSales.updateOrder(true)")
    public AppResponse<String> processOrder(@PathVariable("type") String pType, @PathVariable("orderId") Long pOrderId) {
        OrderDTO lvOrder = mvOrderReadService.findById(pOrderId, true);
        switch (CoreUtils.trim(pType).toLowerCase()) {
            case "cancel":
                mvOrderProcessService.cancelOrder(lvOrder, "");
                break;
            case "return":
                mvOrderProcessService.returnOrder(lvOrder);
                break;
            case "complete":
                mvOrderProcessService.completeOrder(lvOrder);
                break;
            default:
                throw new BadRequestException("Process type is invalid!");
        }
        return mvCHelper.success("Successfully!");
    }
}