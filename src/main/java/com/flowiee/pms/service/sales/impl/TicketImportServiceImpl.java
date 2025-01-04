package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.product.ProductVariantExim;
import com.flowiee.pms.entity.product.Material;
import com.flowiee.pms.entity.product.MaterialTemp;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.TicketExport;
import com.flowiee.pms.entity.sales.TicketImport;
import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.AccountRole;
import com.flowiee.pms.entity.system.GroupAccount;
import com.flowiee.pms.entity.system.Notification;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.exception.ResourceNotFoundException;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.repository.storage.StorageRepository;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.model.dto.TicketImportDTO;
import com.flowiee.pms.repository.sales.MaterialTempRepository;
import com.flowiee.pms.repository.product.ProductDetailTempRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.product.MaterialService;
import com.flowiee.pms.service.product.ProductQuantityService;
import com.flowiee.pms.service.system.AccountService;
import com.flowiee.pms.service.system.GroupAccountService;
import com.flowiee.pms.service.system.NotificationService;
import com.flowiee.pms.service.system.RoleService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.repository.sales.TicketImportRepository;
import com.flowiee.pms.service.sales.TicketImportService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TicketImportServiceImpl extends BaseService implements TicketImportService {
    RoleService                 mvRoleService;
    AccountService              mvAccountService;
    MaterialService             mvMaterialService;
    OrderRepository             mvOrderRepository;
    StorageRepository           mvStorageRepository;
    NotificationService         mvNotificationService;
    GroupAccountService         mvGroupAccountService;
    ProductQuantityService      mvProductQuantityService;
    MaterialTempRepository      mvMaterialTempRepository;
    TicketImportRepository      mvTicketImportRepository;
    ProductDetailRepository     mvProductVariantRepository;
    ProductDetailTempRepository mvProductVariantTempRepository;

    @Override
    public List<TicketImport> findAll() {
        return this.findAll(-1, -1, null, null, null,null, null, null).getContent();
    }

    @Override
    public Page<TicketImport> findAll(int pageSize, int pageNum, String pText, Long pSupplierId, Long pPaymentMethod, String pPayStatus, String pImportStatus, Long pStorageId) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<TicketImport> ticketImportPage = mvTicketImportRepository.findAll(pText, pSupplierId, pPaymentMethod, pPayStatus, pImportStatus, pStorageId, pageable);
        for (TicketImport ticketImport : ticketImportPage.getContent()) {
            BigDecimal[] totalValueAndItems = getTotalValueAndItems(ticketImport.getListProductVariantTemps(), ticketImport.getListMaterialTemps());
            BigDecimal lvTotalValue = totalValueAndItems[0];
            int lvTotalItems = totalValueAndItems[1].intValue();
            ticketImport.setTotalValue(lvTotalValue);
            ticketImport.setTotalItems(lvTotalItems);
        }
        return ticketImportPage;
    }

    @Override
    public TicketImport findById(Long entityId, boolean pThrowException) {
        Optional<TicketImport> ticketImport = mvTicketImportRepository.findById(entityId);
        if (ticketImport.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"ticket import"}, null, null);
        }

        BigDecimal[] totalValueAndItems = getTotalValueAndItems(ticketImport.get().getListProductVariantTemps(), ticketImport.get().getListMaterialTemps());
        BigDecimal lvTotalValue = totalValueAndItems[0];
        int lvTotalItems = totalValueAndItems[1].intValue();
        ticketImport.get().setTotalValue(lvTotalValue);
        ticketImport.get().setTotalItems(lvTotalItems);

        return ticketImport.orElse(null);
    }

    @Override
    public TicketImport save(TicketImport entity) {
        if (entity == null) {
            throw new BadRequestException();
        }
        TicketImport ticketImportSaved = mvTicketImportRepository.save(entity);
        Storage storage = ticketImportSaved.getStorage();
        if (storage != null) {
            if (storage.getHoldableQty() != null && storage.getHoldWarningPercent() != null) {
                int productQty = 0;
                int materialQty = 0;
                if (ObjectUtils.isNotEmpty(ticketImportSaved.getListProductVariantTemps())) {
                    productQty = ticketImportSaved.getListProductVariantTemps().size();
                }
                if (ObjectUtils.isNotEmpty(ticketImportSaved.getListMaterialTemps())) {
                    materialQty = ticketImportSaved.getListMaterialTemps().size();
                }
                int totalGoodsImport = productQty + materialQty;
                int totalGoodsHolding = 0;
                if ((totalGoodsImport + totalGoodsHolding) / storage.getHoldableQty() * 100 >= storage.getHoldWarningPercent()) {
                    List<AccountRole> listOfStorageManagersRight = mvRoleService.findByAction(ACTION.STG_STORAGE);
                    if (ObjectUtils.isNotEmpty(listOfStorageManagersRight)){
                        Set<Account> stgManagersReceiveNtfs = new HashSet<>();
                        for (AccountRole storageManagerRight : listOfStorageManagersRight) {
                            GroupAccount groupAccount = mvGroupAccountService.findById(storageManagerRight.getGroupId(), false);
                            if (groupAccount != null) {
                                stgManagersReceiveNtfs.addAll(groupAccount.getListAccount());
                            }
                            Account account = mvAccountService.findById(storageManagerRight.getAccountId(), false);
                            if (account != null) {
                                stgManagersReceiveNtfs.add(account);
                            }
                        }
                        for (Account a : stgManagersReceiveNtfs) {
                            mvNotificationService.save(Notification.builder()
                                .send(0l)
                                .receive(a.getId())
                                .type("WARNING")
                                .title("Sức chứa của kho " + storage.getName() + " đã chạm mốc cảnh báo!")
                                .content("Số lượng hàng hóa hiện tại " + totalGoodsHolding + ", Số lượng nhập thêm: " + totalGoodsImport + ", Số lượng sau khi nhập: " + totalGoodsImport + totalGoodsHolding + "/" + storage.getHoldableQty())
                                .readed(false)
                                .importId(ticketImportSaved.getId())
                                .build());
                        }
                    }
                }
            }
        }
        return ticketImportSaved;
    }

    @Override
    public TicketImport update(TicketImport pTicketImport, Long entityId) {
        TicketImport ticketImport = this.findById(entityId, true);

        if (ticketImport.isCompletedStatus() || ticketImport.isCancelStatus()) {
            throw new BadRequestException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        ticketImport.setTitle(pTicketImport.getTitle());
        ticketImport.setSupplier(pTicketImport.getSupplier());
        ticketImport.setImportTime(pTicketImport.getImportTime());
        ticketImport.setNote(pTicketImport.getNote());
        ticketImport.setStatus(pTicketImport.getStatus());

        TicketImport ticketImportUpdated = mvTicketImportRepository.save(ticketImport);
        if (ticketImportUpdated.isCompletedStatus()) {
            if (ObjectUtils.isNotEmpty(ticketImportUpdated.getListProductVariantTemps())) {
                for (ProductVariantExim p : ticketImportUpdated.getListProductVariantTemps()) {
                    long lvProductVariantId = p.getProductVariant().getId();
                    mvProductQuantityService.updateProductVariantQuantityIncrease(p.getQuantity(), lvProductVariantId);
                    mvProductVariantTempRepository.updateStorageQuantity(lvProductVariantId, p.getQuantity());
                }
            }
            if (ObjectUtils.isNotEmpty(ticketImportUpdated.getListMaterialTemps())) {
                for (MaterialTemp m : ticketImportUpdated.getListMaterialTemps()) {
                    long lvMaterialId = m.getMaterial().getId();
                    mvMaterialService.updateQuantity(m.getQuantity(), lvMaterialId, "I");
                    mvMaterialTempRepository.updateStorageQuantity(lvMaterialId, m.getQuantity());
                }
            }
        }
        return ticketImportUpdated;
    }

    @Override
    public String delete(Long entityId) {
        TicketImport ticketImport = this.findById(entityId, true);

        mvTicketImportRepository.deleteById(entityId);

        systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_TICKET_IM, MasterObject.TicketImport, "Xóa phiếu nhập hàng", ticketImport.getTitle());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public TicketImport findDraftImportPresent(Long createdBy) {
        return mvTicketImportRepository.findDraftGoodsImportPresent(TicketImportStatus.DRAFT.name(), createdBy);
    }

    @Override
    public TicketImport createDraftTicketImport(TicketImportDTO pTicketImport) {
        TicketImport ticketImport = TicketImport.builder()
            .title(pTicketImport.getTitle())
            .status(TicketImportStatus.DRAFT.name())
            .importer(CommonUtils.getUserPrincipal().getUsername())
            .importTime(LocalDateTime.now())
            .storage(new Storage(pTicketImport.getStorageId()))
            .build();
        ticketImport.setCreatedBy(CommonUtils.getUserPrincipal().getId());
        return this.save(ticketImport);
    }

    @Override
    public TicketImport updateStatus(Long entityId, String status) {
        if (entityId == null || entityId <= 0) {
            throw new BadRequestException();
        }
        TicketImport ticketImport = this.findById(entityId, true);
        ticketImport.setStatus(status);

        return mvTicketImportRepository.save(ticketImport);
    }

    @Override
    public List<ProductVariantExim> addProductToTicket(Long ticketImportId, List<Long> productVariantIds) {
        if (this.findById(ticketImportId, true) == null) {
            throw new ResourceNotFoundException("Ticket import goods not found!");
        }
        List<ProductVariantExim> listAdded = new ArrayList<>();
        for (Long productVariantId : productVariantIds) {
            Optional<ProductDetail> productDetailOpt = mvProductVariantRepository.findById(productVariantId);
            if (productDetailOpt.isEmpty()) {
                logger.error(String.format("Product variant with id %s not found in database!", productVariantId));
                continue;
            }
            ProductDetail lvProductDetail = productDetailOpt.get();
            ProductVariantExim temp = mvProductVariantTempRepository.findProductVariantInGoodsImport(ticketImportId, lvProductDetail.getId());
            int defaultQuantity = 1;
            if (temp != null) {
                mvProductVariantTempRepository.updateQuantityIncrease(temp.getId(), defaultQuantity);
            } else {
                ProductVariantExim productVariantEximAdded = mvProductVariantTempRepository.save(ProductVariantExim.builder()
                        .ticketImport(new TicketImport(ticketImportId))
                        .productVariant(lvProductDetail)
                        .quantity(defaultQuantity)
                        .storageQty(lvProductDetail.getStorageQty())
                        .build());
                listAdded.add(productVariantEximAdded);
            }
        }
        return listAdded;
    }

    @Override
    public List<MaterialTemp> addMaterialToTicket(Long ticketImportId, List<Long> materialIds) {
        List<MaterialTemp> listAdded = new ArrayList<>();
        for (Long materialId : materialIds) {
            Material material = mvMaterialService.findById(materialId, false);
            if (material == null) {
                continue;
            }
            MaterialTemp temp = mvMaterialTempRepository.findMaterialInGoodsImport(ticketImportId, material.getId());
            int defaultQuantity = 1;
            if (temp != null) {
                mvMaterialTempRepository.updateQuantityIncrease(temp.getId(), defaultQuantity);
            } else {
                MaterialTemp materialTempAdded = mvMaterialTempRepository.save(MaterialTemp.builder()
                        .ticketImport(new TicketImport(ticketImportId))
                        .material(material)
                        .quantity(defaultQuantity)
                        .storageQty(material.getQuantity())
                        .build());
                listAdded.add(materialTempAdded);
            }
        }
        return listAdded;
    }

    @Override
    public void restockReturnedItems(Long pStorageId, String pOrderCode) {
        Optional<Storage> lvStorageOpt = mvStorageRepository.findById(pStorageId);
        if (lvStorageOpt.isEmpty()) return;
        Order lvOrder = mvOrderRepository.findByOrderCode(pOrderCode);
        if (lvOrder == null) return;

        TicketExport lvTicketExport = lvOrder.getTicketExport();
        for (ProductVariantExim productVariantTmp : lvTicketExport.getListProductVariantTemp()) {
            long lvProductVariantId = productVariantTmp.getProductVariant().getId();
            int lvStockQuantity = productVariantTmp.getQuantity();
            mvProductQuantityService.updateProductVariantQuantityIncrease(lvStockQuantity, lvProductVariantId);
        }
    }

    private BigDecimal[] getTotalValueAndItems(List<ProductVariantExim> pProductVariantEximList, List<MaterialTemp> pMaterialTempList) {
        BigDecimal totalValue = BigDecimal.ZERO;
        int totalItems = 0;
        if (pProductVariantEximList != null) {
            for (ProductVariantExim p : pProductVariantEximList) {
                if (p.getPurchasePrice() != null) {
                    totalValue = totalValue.add(p.getPurchasePrice().multiply(new BigDecimal(p.getQuantity())));
                }
                totalItems = totalItems + p.getQuantity();
            }
        }
        if (pMaterialTempList != null) {
            for (MaterialTemp m : pMaterialTempList) {
                if (m.getPurchasePrice() != null) {
                    totalValue = totalValue.add(m.getPurchasePrice().multiply(new BigDecimal(m.getQuantity())));
                }
                totalItems += m.getQuantity();
            }
        }
        return new BigDecimal[] {totalValue, BigDecimal.valueOf(totalItems)};
    }
}