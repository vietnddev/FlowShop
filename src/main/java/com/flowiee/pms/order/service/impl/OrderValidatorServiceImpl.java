package com.flowiee.pms.order.service.impl;

import com.flowiee.pms.cart.entity.Items;
import com.flowiee.pms.cart.entity.OrderCart;
import com.flowiee.pms.cart.service.CartService;
import com.flowiee.pms.customer.entity.Customer;
import com.flowiee.pms.customer.service.CustomerService;
import com.flowiee.pms.order.entity.Order;
import com.flowiee.pms.order.enums.OrderStatus;
import com.flowiee.pms.order.model.ChangeOrderStatusReq;
import com.flowiee.pms.order.model.CreateOrderReq;
import com.flowiee.pms.order.model.UpdateOrderReq;
import com.flowiee.pms.order.service.OrderValidatorService;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.promotion.entity.VoucherTicket;
import com.flowiee.pms.promotion.service.VoucherTicketService;
import com.flowiee.pms.shared.base.FlwSys;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.shared.util.SysConfigUtils;
import com.flowiee.pms.system.entity.Account;
import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.system.entity.SystemConfig;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.system.service.AccountService;
import com.flowiee.pms.system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderValidatorServiceImpl implements OrderValidatorService {
    private final VoucherTicketService mvVoucherTicketService;
    private final CustomerService mvCustomerService;
    private final CategoryService mvCategoryService;
    private final AccountService mvAccountService;
    private final CartService mvCartService;

    @Override
    public void validateCreateOrder(CreateOrderReq pRequest) {
        validateCart(pRequest.getCartId());
        validateCustomer(pRequest.getCustomerId(), pRequest.getRecipientPhone(), pRequest.getRecipientEmail(), pRequest.getRecipientPhone());
        validatePaymentMethod(pRequest.getPaymentMethodId());
        validateSalesChannel(pRequest.getSalesChannelId());
        validateSalesAssistant(pRequest.getSalesAssistantId());
        validateVoucher(pRequest.getCouponCode());
    }

    @Override
    public void validateUpdateOrder(UpdateOrderReq request, Long pOrderId) {

    }

    @Override
    public void validateStatusTransition(Order pOrder, OrderStatus pOldStatus, OrderStatus pNewStatus, ChangeOrderStatusReq pRequest) {
        switch (pNewStatus) {
            case CANCELLED:
                //validateCancelOrder(pOrder, pRequest.getCancellationReason());
                break;
            case REFUNDED:
                LocalDateTime lvSuccessfulDeliveryTime = pOrder.getDeliverySuccessTime();

                SystemConfig lvReturnPeriodDaysMdl = FlwSys.getSystemConfigs().get(ConfigCode.returnPeriodDays);
                if (SysConfigUtils.isValid(lvReturnPeriodDaysMdl))
                    throw new AppException("System has not configured the time allowed to return the order!");

                int lvReturnPeriodDays = lvReturnPeriodDaysMdl.getIntValue();
                if (!isWithinReturnPeriod(lvSuccessfulDeliveryTime, LocalDateTime.now(), lvReturnPeriodDays))
                    throw new AppException("The return period has expired!");

                break;
            case COMPLETED:
                //validateCompleteOrder(pOrder);
                break;
            case PROCESSING:
                // Không cần validate đặc biệt
                break;
            default:
                // Các status khác
                break;
        }
    }

    // ==================== PRIVATE VALIDATION METHODS ====================

    private void validateCart(Long pCartId) {
        OrderCart cart = mvCartService.findEntById(pCartId, false);
        if (cart == null) {
            throw new BadRequestException("Cart not found!");
        }

        List<Items> lvItems = cart.getListItems();
        if (CollectionUtils.isEmpty(lvItems)) {
            throw new BadRequestException("Cart is empty! At least one product required.");
        }

        for (Items item : lvItems) {
            ProductDetail variant = item.getProductDetail();
            if (variant == null) {
                throw new BadRequestException("Product item not found: " + item);
            }

            int cartQuantity = item.getQuantity();
            int availableQuantity = variant.getAvailableSalesQty();

            if (cartQuantity <= 0) {
                throw new BadRequestException(String.format("Invalid quantity for product %s: %d", variant.getVariantName(), cartQuantity));
            }

            if (cartQuantity > availableQuantity) {
                throw new BadRequestException(
                        String.format("Product %s only has %d in stock, but you requested %d!",
                                variant.getVariantName(), availableQuantity, cartQuantity)
                );
            }

        }
    }

    private void validateCustomer(Long pCustomerId, String pRecipientPhone, String pRecipientEmail, String pShippingAddress) {
        Customer customer = mvCustomerService.findEntById(pCustomerId, false);
        if (customer == null) {
            throw new BadRequestException("Customer not found!");
        }

        // Khách hàng nợ thì không cho tạo đơn mới
        if (Boolean.TRUE.equals(customer.getHasOutstandingBalance()) && customer.getOutstandingBalanceAmount() != null && BigDecimal.ZERO.compareTo(customer.getOutstandingBalanceAmount()) < 0) {
            throw new BadRequestException(String.format("Customer has outstanding debt: %.2f VND. Please clear debt before creating new order!",
                    customer.getOutstandingBalanceAmount())
            );
        }

        // Khách hàng nằm trong blacklist
        if (Boolean.TRUE.equals(customer.getIsBlackList())) {
            throw new BadRequestException("Customer is blacklisted! Cannot create order.");
        }

        // Khách hàng online cần có đủ thông tin giao hàng
        if (!customer.isWalkInCustomer()) {
            if (CoreUtils.isNullStr(pRecipientPhone)) {
                throw new BadRequestException("Recipient phone is required for online order!");
            }
            if (CoreUtils.isNullStr(pShippingAddress)) {
                throw new BadRequestException("Shipping address is required for online order!");
            }
            if (!CoreUtils.validateEmail(pRecipientEmail)) {
                throw new BadRequestException("Invalid email format!");
            }
        }
    }

    private void validatePaymentMethod(Long pPaymentMethodId) {
        Category paymentMethod = mvCategoryService.findEntById(pPaymentMethodId, false);
        if (paymentMethod == null) {
            throw new BadRequestException("Payment method not found!");
        }
        if (!paymentMethod.getStatus()) {
            throw new BadRequestException("Payment method is inactive!");
        }

        // Kiểm tra nếu là COD thì có điều kiện đặc biệt
        if ("COD".equals(paymentMethod.getCode())) {
            // COD chỉ áp dụng cho đơn hàng nội thành, hoặc giá trị không quá lớn
            // Có thể check thêm nhưng tạm thời để đơn giản
            log.debug("COD payment method selected");
        }
    }

    private void validateSalesChannel(Long  pSalesChannelId) {
        Category channel = mvCategoryService.findEntById(pSalesChannelId, false);
        if (channel == null) {
            throw new BadRequestException("Sales channel not found!");
        }
        if (!channel.getStatus()) {
            throw new BadRequestException("Sales channel is inactive!");
        }
    }

    private void validateSalesAssistant(Long   pSalesAssistantId) {
        Account assistant = mvAccountService.findEntById(pSalesAssistantId, false);
        if (assistant == null) {
            throw new BadRequestException("Sales assistant not found!");
        }
        if (assistant.isClosed()) {
            throw new BadRequestException("Sales assistant account is closed!");
        }
    }

    private void validateVoucher(String pCouponCode) {
        if (CoreUtils.isNullStr(pCouponCode)) {
            return;
        }

        VoucherTicket voucher = mvVoucherTicketService.findTicketByCode(pCouponCode);
        if (voucher == null) {
            throw new BadRequestException("Voucher code is invalid!");
        }

        // Kiểm tra hạn sử dụng
        LocalDateTime lvEndTime = voucher.getVoucherInfo().getEndTime();
        if (lvEndTime != null && lvEndTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Voucher has expired!");
        }

        // Kiểm tra đã dùng chưa
        if (voucher.isUsed()) {
            throw new BadRequestException("Voucher code has been used!");
        }
    }

    public boolean isWithinReturnPeriod(LocalDateTime fromTime, LocalDateTime toTime, int periodDays) {
        long daysBetween = Duration.between(fromTime, toTime).toDays();
        return daysBetween < periodDays;
    }
}