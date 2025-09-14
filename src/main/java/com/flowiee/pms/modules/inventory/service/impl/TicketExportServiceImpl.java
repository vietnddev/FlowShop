package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.inventory.dto.*;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductVariantExim;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.inventory.service.ProductHistoryService;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.modules.inventory.service.StorageService;
import com.flowiee.pms.modules.inventory.service.TicketExportService;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.inventory.repository.ProductDetailTempRepository;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.inventory.repository.TicketExportRepository;
import com.flowiee.pms.modules.sales.service.OrderItemsService;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TicketExportServiceImpl extends BaseService<TicketExport, TicketExportDTO, TicketExportRepository> implements TicketExportService {
    OrderRepository             mvOrderRepository;
    OrderItemsService           mvOrderItemsService;
    TicketExportRepository      mvTicketExportRepository;
    ProductHistoryService mvProductHistoryService;
    ProductVariantService       mvProductVariantService;
    ProductDetailTempRepository mvProductVariantTempRepository;
    UserSession mvUserSession;
    SystemLogService systemLogService;
    StorageService mvStorageService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public TicketExportServiceImpl(TicketExportRepository pTicketExportRepository, OrderRepository pOrderRepository,
                                   OrderItemsService pOrderItemsService, ProductHistoryService pProductHistoryService,
                                   ProductVariantService pProductVariantService, ProductDetailTempRepository pProductVariantTempRepository,
                                   UserSession pUserSession, SystemLogService pSystemLogService, StorageService pStorageService) {
        super(TicketExport.class, TicketExportDTO.class, pTicketExportRepository);
        this.mvOrderRepository = pOrderRepository;
        this.mvOrderItemsService = pOrderItemsService;
        this.mvTicketExportRepository = pTicketExportRepository;
        this.mvProductHistoryService = pProductHistoryService;
        this.mvProductVariantService = pProductVariantService;
        this.mvProductVariantTempRepository = pProductVariantTempRepository;
        this.mvUserSession = pUserSession;
        this.systemLogService = pSystemLogService;
        this.mvStorageService = pStorageService;
    }

    @Override
    public List<TicketExportDTO>find(BaseParameter pParam) {
        return super.find(pParam);
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
    public TicketExportDTO findById(Long entityId, boolean pThrowException) {
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

        return TicketExportDTO.toDto(ticketExportOpt.get());
    }

    @Override
    public TicketExportDTO save(TicketExportDTO pDto) {
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

        TicketExport lvTx = new TicketExport();
        lvTx.setTitle(pDto.getTitle());
        lvTx.setStorage(mvStorageService.findEntById(pDto.getStorage().getId(), true));
        lvTx.setExporter(mvUserSession.getUserPrincipal().getUsername());
        lvTx.setExportTime(LocalDateTime.now());
        lvTx.setStatus(TicketExportStatus.DRAFT.name());
        lvTx.setNote(pDto.getNote());

        return TicketExportDTO.toDto(mvEntityRepository.save(lvTx));
    }

    @Transactional
    @Override
    public TicketExport save(OrderDTO orderDTO) {
        if (!ObjectUtils.isNotEmpty(orderDTO) || mvOrderRepository.findById(orderDTO.getId()).isEmpty()) {
            throw new BadRequestException("Đơn hàng không tồn tại");
        }

        TicketExportDTO lvTx = new TicketExportDTO();
        lvTx.setTitle("Xuất hàng cho đơn " + orderDTO.getCode());
        lvTx.setExporter(mvUserSession.getUserPrincipal().getUsername());
        lvTx.setExportTime(LocalDateTime.now());
        lvTx.setStatus(TicketExportStatus.DRAFT.name());

        TicketExportDTO ticketExportSaved = this.save(lvTx);
        mvOrderRepository.updateTicketExportInfo(orderDTO.getId(), ticketExportSaved.getId());
        //return ticketExportSaved;
        return new TicketExport();
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

        TicketExportDTO lvTx = new TicketExportDTO();
        lvTx.setTitle(title);
        lvTx.setStorage(new StorageDTO(storageId));
        lvTx.setExporter(mvUserSession.getUserPrincipal().getUsername());
        lvTx.setExportTime(LocalDateTime.now());
        lvTx.setStatus(TicketExportStatus.DRAFT.name());
        lvTx.setNote(order != null ? "Phiếu xuất hàng cho đơn " + order.getCode() : "");
        TicketExportDTO ticketExportSaved = this.save(lvTx);

        if (order != null) {
            mvOrderRepository.updateTicketExportInfo(order.getId(), ticketExportSaved.getId());
            List<OrderDetail> orderDetails = mvOrderItemsService.findByOrderId(order.getId());
            for (OrderDetail item : orderDetails) {
                mvProductVariantTempRepository.save(ProductVariantExim.builder()
                        .ticketExport(new TicketExport(ticketExportSaved.getId()))
                        .productVariant(item.getProductDetail())
                        .sellPrice(item.getPrice())
                        .quantity(item.getQuantity())
                        .note(item.getNote())
                        .action(TicketExportAction.RELEASE_TO_CUSTOMER.name())
                        .build());
            }
        }
        //return ticketExportSaved;
        return null;
    }

    @Transactional
    @Override
    public TicketExportDTO update(TicketExportDTO pTicket, Long ticketExportId) {
        pTicket.setId(ticketExportId);
        TicketExport ticketExport = super.findById(ticketExportId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found!"));

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
                mvProductVariantService.updateStockQuantity(lvProductVariant.getId(), soldQtyInOrder, "D");
                //Save log
                int storageQty = lvProductVariant.getStorageQty();
                int soldQty = lvProductVariant.getSoldQty();
                ProductHistoryDTO productHistory = ProductHistoryDTO.builder()
                    .product(new ProductDTO(lvProductVariant.getProduct().getId()))
                    .productDetail(new ProductVariantDTO(lvProductVariant.getId()))
                    .title("Cập nhật số lượng cho [" + lvProductVariant.getVariantName() + "] - " + pTicket.getTitle())
                    .field("Storage Qty | Sold Qty")
                    .oldValue(storageQty + " | " + soldQty)
                    .newValue((storageQty - soldQtyInOrder) +  " | " + (soldQty + soldQtyInOrder))
                    .build();
                mvProductHistoryService.save(productHistory);
            }
        }
        return TicketExportDTO.toDto(ticketExportUpdated);
    }

    @Override
    public String delete(Long ticketExportId) {
        TicketExport ticketExportToDelete = super.findById(ticketExportId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found!"));
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