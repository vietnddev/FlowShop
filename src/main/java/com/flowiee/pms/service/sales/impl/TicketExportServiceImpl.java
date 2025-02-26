package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.ProductVariantExim;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.product.ProductHistory;
import com.flowiee.pms.entity.sales.TicketExport;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.repository.product.ProductDetailTempRepository;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.repository.sales.TicketExportRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.ProductHistoryService;
import com.flowiee.pms.service.product.ProductQuantityService;
import com.flowiee.pms.service.sales.OrderItemsService;
import com.flowiee.pms.service.sales.TicketExportService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TicketExportServiceImpl extends BaseService implements TicketExportService {
    OrderRepository             mvOrderRepository;
    OrderItemsService           mvOrderItemsService;
    TicketExportRepository      mvTicketExportRepository;
    ProductHistoryService       mvProductHistoryService;
    ProductQuantityService      mvProductQuantityService;
    ProductDetailTempRepository mvProductVariantTempRepository;

    @Override
    public List<TicketExport> findAll() {
        return this.findAll(-1, -1, null).getContent();
    }

    @Override
    public Page<TicketExport> findAll(int pageSize, int pageNum, Long storageId) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("exportTime").descending());
        Page<TicketExport> ticketExportPage = mvTicketExportRepository.findAll(storageId, pageable);
        for (TicketExport ticketExport : ticketExportPage.getContent()) {
            BigDecimal[] totalValueAndItems = getTotalValueAndItems(ticketExport.getListProductVariantTemp());
            BigDecimal lvTotalValue = totalValueAndItems[0];
            int lvTotalItems = totalValueAndItems[1].intValue();
            String lvNote = ticketExport.getNote() != null ? ticketExport.getNote() : "";

            ticketExport.setTotalValue(lvTotalValue);
            ticketExport.setTotalItems(lvTotalItems);
            ticketExport.setStorageName(ticketExport.getStorage().getName());
            ticketExport.setNote(lvNote);
        }
        return ticketExportPage;
    }

    @Override
    public TicketExport findById(Long entityId, boolean pThrowException) {
        Optional<TicketExport> ticketExportOpt = mvTicketExportRepository.findById(entityId);
        if (ticketExportOpt.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"ticket export"}, null, null);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        BigDecimal[] totalValueAndItems = getTotalValueAndItems(ticketExportOpt.get().getListProductVariantTemp());
        BigDecimal lvTotalValue = totalValueAndItems[0];
        int lvTotalItems = totalValueAndItems[1].intValue();

        ticketExportOpt.get().setTotalValue(lvTotalValue);
        ticketExportOpt.get().setTotalItems(lvTotalItems);
        ticketExportOpt.get().setExportTimeStr(ticketExportOpt.get().getExportTime().format(formatter));

        return ticketExportOpt.orElse(null);
    }

    @Override
    public TicketExport save(TicketExport ticket) {
        //TicketExport ticketExportSaved = ticketExportRepository.save(ticket);
        //Code for order status
        //PR -> Preparing
        //WS -> Waiting shipper
        //DE -> Delivering
        //DO -> Done
        //Update lại số lượng của sản phẩm còn trong kho
//        orderService.findOrderDetailsByOrderId(ticket.getOrderId()).forEach(d -> {
//            productService.updateProductVariantQuantity(productService.findProductVariantById(d.getProductVariant().getId()).getSoLuongKho() - d.getSoLuong(), d.getProductVariant().getId());
//        });
        return mvTicketExportRepository.save(ticket);
    }

    @Transactional
    @Override
    public TicketExport save(OrderDTO orderDTO) {
        if (!ObjectUtils.isNotEmpty(orderDTO) || mvOrderRepository.findById(orderDTO.getId()).isEmpty()) {
            throw new BadRequestException("Đơn hàng không tồn tại");
        }
        TicketExport ticketExportSaved = this.save(TicketExport.builder()
                .title("Xuất hàng cho đơn " + orderDTO.getCode())
                .exporter(mvUserSession.getUserPrincipal().getUsername())
                .exportTime(LocalDateTime.now())
                .status(TicketExportStatus.DRAFT.name())
                .build());
        mvOrderRepository.updateTicketExportInfo(orderDTO.getId(), ticketExportSaved.getId());
        return ticketExportSaved;
    }

    @Transactional
    @Override
    public TicketExport createDraftTicketExport(long storageId, String title, String orderCode) {
        Order order = null;
        if (ObjectUtils.isNotEmpty(orderCode)) {
            order = mvOrderRepository.findByOrderCode(orderCode);
            if (order == null) {
                throw new BadRequestException("Mã đơn hàng không tồn tại!");
            } else {
                if (order.getTicketExport() != null) {
                    throw new BadRequestException("Đơn hàng này đã được tạo phiếu xuất kho!");
                }
            }
        }
        TicketExport ticketExportSaved = this.save(TicketExport.builder()
                .title(title)
                .status(TicketExportStatus.DRAFT.name())
                .exporter(mvUserSession.getUserPrincipal().getUsername())
                .exportTime(LocalDateTime.now())
                .storage(new Storage(storageId))
                .note(order != null ? "Phiếu xuất hàng cho đơn " + order.getCode() : "")
                .build());
        if (order != null) {
            mvOrderRepository.updateTicketExportInfo(order.getId(), ticketExportSaved.getId());
            List<OrderDetail> orderDetails = mvOrderItemsService.findByOrderId(order.getId());
            for (OrderDetail item : orderDetails) {
                mvProductVariantTempRepository.save(ProductVariantExim.builder()
                        .ticketExport(ticketExportSaved)
                        .productVariant(item.getProductDetail())
                        .sellPrice(item.getPrice())
                        .quantity(item.getQuantity())
                        .note(item.getNote())
                        .build());
            }
        }
        return ticketExportSaved;
    }

    @Transactional
    @Override
    public TicketExport update(TicketExport pTicket, Long ticketExportId) {
        pTicket.setId(ticketExportId);
        TicketExport ticketExport = this.findById(ticketExportId, true);

        TicketExport ticketExportToUpdate = ticketExport;
        if (ticketExportToUpdate.isCompletedStatus() || ticketExportToUpdate.isCancelStatus()) {
            throw new BadRequestException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }
        ticketExportToUpdate.setTitle(pTicket.getTitle());
        ticketExportToUpdate.setNote(pTicket.getNote());
        ticketExportToUpdate.setStatus(pTicket.getStatus());
        TicketExport ticketExportUpdated = mvTicketExportRepository.save(ticketExportToUpdate);

        if (TicketExportStatus.COMPLETED.name().equals(ticketExportUpdated.getStatus())) {
            for (ProductVariantExim productVariantExim : ticketExportUpdated.getListProductVariantTemp()) {
                ProductDetail lvProductVariant = productVariantExim.getProductVariant();
                int soldQtyInOrder = productVariantExim.getQuantity();
                mvProductQuantityService.updateProductVariantQuantityDecrease(soldQtyInOrder, lvProductVariant.getId());
                //Save log
                int storageQty = lvProductVariant.getStorageQty();
                int soldQty = lvProductVariant.getSoldQty();
                ProductHistory productHistory = ProductHistory.builder()
                    .product(lvProductVariant.getProduct())
                    .productDetail(lvProductVariant)
                    .title("Cập nhật số lượng cho [" + lvProductVariant.getVariantName() + "] - " + pTicket.getTitle())
                    .field("Storage Qty | Sold Qty")
                    .oldValue(storageQty + " | " + soldQty)
                    .newValue((storageQty - soldQtyInOrder) +  " | " + (soldQty + soldQtyInOrder))
                    .build();
                mvProductHistoryService.save(productHistory);
            }
        }
        return ticketExportUpdated;
    }

    @Override
    public String delete(Long ticketExportId) {
        TicketExport ticketExportToDelete = this.findById(ticketExportId, true);
        if (TicketExportStatus.COMPLETED.name().equals(ticketExportToDelete.getStatus())) {
            throw new BadRequestException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        Order order = mvOrderRepository.findByTicketExport(ticketExportId);
        if (order != null) {
            order.setTicketExport(null);
            mvOrderRepository.save(order);
        }
        mvTicketExportRepository.deleteById(ticketExportId);

        systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_TICKET_EX, MasterObject.TicketExport, "Xóa phiếu xuất hàng", ticketExportToDelete.getTitle());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private BigDecimal[] getTotalValueAndItems(List<ProductVariantExim> pProductVariantEximList) {
        BigDecimal totalValue = BigDecimal.ZERO;
        int totalItems = 0;
        if (pProductVariantEximList != null) {
            for (ProductVariantExim p : pProductVariantEximList) {
                BigDecimal lvProductVariantTempQty = new BigDecimal(p.getQuantity());
                if (p.getTicketImport() != null && p.getPurchasePrice() != null) {
                    totalValue = totalValue.add(p.getPurchasePrice().multiply(lvProductVariantTempQty));
                }
                if (p.getTicketExport() != null && p.getSellPrice() != null) {
                    totalValue = totalValue.add(p.getSellPrice().multiply(lvProductVariantTempQty));
                }
                totalItems = totalItems + p.getQuantity();
            }
        }
        return new BigDecimal[] {totalValue, BigDecimal.valueOf(totalItems)};
    }
}