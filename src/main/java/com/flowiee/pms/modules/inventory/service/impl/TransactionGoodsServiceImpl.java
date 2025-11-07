package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.inventory.dto.*;
import com.flowiee.pms.modules.inventory.entity.*;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsStatus;
import com.flowiee.pms.modules.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsItemReq;
import com.flowiee.pms.modules.inventory.model.TransactionGoodsReq;
import com.flowiee.pms.modules.inventory.repository.TransactionGoodsItemRepository;
import com.flowiee.pms.modules.inventory.service.StorageService;
import com.flowiee.pms.modules.inventory.service.TransactionGoodsService;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.inventory.repository.TransactionGoodsRepository;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class TransactionGoodsServiceImpl extends BaseService<TransactionGoods, TransactionGoodsDTO, TransactionGoodsRepository> implements TransactionGoodsService {
    private final TransactionGoodsItemRepository transactionGoodsItemRepository;
    private final ProductDetailRepository productVariantRepository;
    private final FileStorageRepository fileStorageRepository;
    private final SystemLogService systemLogService;
    private final OrderRepository orderRepository;
    private final StorageService storageService;

    private static final String PREFIX_TRANSACTION_IMPORT_CODE = "I";
    private static final String PREFIX_TRANSACTION_EXPORT_CODE = "E";

    public TransactionGoodsServiceImpl(TransactionGoodsRepository pTransactionGoodsRepository,
                                       TransactionGoodsItemRepository pTransactionGoodsItemRepository,
                                       ProductDetailRepository pProductVariantRepository,
                                       FileStorageRepository pFileStorageRepository,
                                       SystemLogService pSystemLogService,
                                       OrderRepository pOrderRepository,
                                       StorageService pStorageService) {
        super(TransactionGoods.class, TransactionGoodsDTO.class, pTransactionGoodsRepository);
        this.transactionGoodsItemRepository = pTransactionGoodsItemRepository;
        this.productVariantRepository = pProductVariantRepository;
        this.fileStorageRepository = pFileStorageRepository;
        this.systemLogService = pSystemLogService;
        this.orderRepository = pOrderRepository;
        this.storageService = pStorageService;
    }

    @Override
    public Page<TransactionGoodsDTO> getTransactionGoods(TransactionGoodsType pTranType, TransactionGoodsReq pRequest) {
        Pageable lvPageable = getPageable(pRequest.getPageNum(), pRequest.getPageSize(), Sort.by("id").descending());
        Long lvWarehouseId = pRequest.getWarehouse() != null ? pRequest.getWarehouse().getId() : null;
        Page<TransactionGoods> transactionGoodsPage = mvEntityRepository.findAll(pTranType, lvWarehouseId, lvPageable);
        List<TransactionGoods> transactionGoodsList = transactionGoodsPage.getContent();
        return new PageImpl<>(TransactionGoodsDTO.toDTOs(transactionGoodsList), lvPageable, transactionGoodsPage.getTotalElements());
    }

    @Override
    public TransactionGoods findEntById(Long pId, boolean pThrowException) {
        return super.findEntById(pId, pThrowException);
    }

    @Override
    public TransactionGoodsDTO findDtoById(Long pId, boolean throwException) {
        return TransactionGoodsDTO.toDto(super.findEntById(pId, throwException));
    }

    @Override
    public TransactionGoodsDTO createImportTransaction(TransactionGoodsDTO pTransactionGoodsDto) throws Exception {
        try {
            Storage lvWarehouse = storageService.findEntById(pTransactionGoodsDto.getWarehouse().getId(), true);
            TransactionGoods lvRecordedTransactionGoods = mvEntityRepository.save(TransactionGoods.builder()
                    .code(generateTransactionGoodsCode(TransactionGoodsType.IMPORT))
                    .title("Draft")
                    .warehouse(lvWarehouse)
                    .transactionType(TransactionGoodsType.IMPORT)
                    .transactionStatus(TransactionGoodsStatus.DRAFT)
                    .build());
            return TransactionGoodsDTO.toDto(lvRecordedTransactionGoods);
        } catch (Exception e) {
            log.error("Create transaction goods got [{}]", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    @Override
    public TransactionGoodsDTO updateImportTransaction(TransactionGoodsDTO pTransactionGoodsDto, Long pTranId) {
        TransactionGoods lvTransactionGoods = super.findEntById(pTranId, true);

        if (TransactionGoodsStatus.APPROVED.equals(lvTransactionGoods.getTransactionStatus())) {
            throw new AppException("Do not allow updating this with status " + lvTransactionGoods.getTransactionStatus());
        }

        lvTransactionGoods.setTitle(pTransactionGoodsDto.getTitle());
        lvTransactionGoods.setRequestNote(pTransactionGoodsDto.getRequestNote());
        lvTransactionGoods.setDescription(pTransactionGoodsDto.getDescription());
        lvTransactionGoods.setTransactionStatus(pTransactionGoodsDto.getTransactionStatus());

        TransactionGoods lvUpdatedTransaction = mvEntityRepository.save(lvTransactionGoods);
        if (TransactionGoodsStatus.APPROVED.equals(lvUpdatedTransaction.getTransactionStatus())) {
            for (TransactionGoodsItem lvItem : lvUpdatedTransaction.getItems()) {
                ProductDetail lvProductVariant = lvItem.getProductVariant();
                if (lvProductVariant != null) {
                    Long lvProductVariantId = lvProductVariant.getId();
                    int lvQuantity = lvItem.getQuantity();
                    productVariantRepository.updateQuantityIncrease(lvQuantity, lvProductVariantId);
                }
            }
        }

        return TransactionGoodsDTO.toDto(lvUpdatedTransaction);
    }

    @Transactional
    @Override
    public String deleteImportTransaction(Long pTranId) {
        TransactionGoods lvTransactionGoods = super.findEntById(pTranId, true);
        if (!TransactionGoodsStatus.DRAFT.equals(lvTransactionGoods.getTransactionStatus())) {
            throw new BadRequestException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        //Validate more
        //...

        mvEntityRepository.deleteById(lvTransactionGoods.getId());

        systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_TICKET_IM, MasterObject.TicketImport, "Xóa phiếu nhập hàng", lvTransactionGoods.getTitle());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public void restockReturnedItems(Long pStorageId, String pOrderCode) {
        Storage lvStorage = storageService.findEntById(pStorageId, true);
        Order lvOrder = orderRepository.findByOrderCode(pOrderCode);
        if (lvOrder == null) {
            return;
        }

        for (TransactionGoods lvTransactionGoods : lvOrder.getTransactionGoodsList()) {
            for (TransactionGoodsItem lvItem : lvTransactionGoods.getItems()) {
                long lvProductVariantId = lvItem.getProductVariant().getId();
                int lvStockQuantity = lvItem.getQuantity();
                productVariantRepository.updateQuantityIncrease(lvStockQuantity, lvProductVariantId);
            }
        }
    }

    @Override
    public TransactionGoodsDTO createExportTransaction(TransactionGoodsDTO pTransactionGoodsDto) {
        Storage lvWarehouse = storageService.findEntById(pTransactionGoodsDto.getWarehouse().getId(), true);
        TransactionGoods lvRecordedTransactionGoods = mvEntityRepository.save(TransactionGoods.builder()
                .code(generateTransactionGoodsCode(TransactionGoodsType.EXPORT))
                .title("Draft")
                .warehouse(lvWarehouse)
                .transactionType(TransactionGoodsType.EXPORT)
                .transactionStatus(TransactionGoodsStatus.DRAFT)
                .build());
        return TransactionGoodsDTO.toDto(lvRecordedTransactionGoods);
    }

    @Transactional
    @Override
    public TransactionGoodsDTO updateExportTransaction(TransactionGoodsDTO pTransactionGoodsDto, Long pTranId) {
        TransactionGoods lvTransactionGoods = super.findEntById(pTranId, true);

        if (TransactionGoodsStatus.APPROVED.equals(lvTransactionGoods.getTransactionStatus())) {
            throw new AppException("Do not allow updating this with status " + lvTransactionGoods.getTransactionStatus());
        }

        lvTransactionGoods.setTitle(pTransactionGoodsDto.getTitle());
        lvTransactionGoods.setRequestNote(pTransactionGoodsDto.getRequestNote());
        lvTransactionGoods.setDescription(pTransactionGoodsDto.getDescription());
        lvTransactionGoods.setTransactionStatus(pTransactionGoodsDto.getTransactionStatus());

        TransactionGoods lvUpdatedTransaction = mvEntityRepository.save(lvTransactionGoods);
        if (TransactionGoodsStatus.APPROVED.equals(lvUpdatedTransaction.getTransactionStatus())) {
            for (TransactionGoodsItem lvItem : lvUpdatedTransaction.getItems()) {
                ProductDetail lvProductVariant = lvItem.getProductVariant();
                if (lvProductVariant != null) {
                    Long lvProductVariantId = lvProductVariant.getId();
                    int lvQuantity = lvItem.getQuantity();
                    productVariantRepository.updateQuantityDecrease(lvQuantity, lvProductVariantId);
                }
            }
        }

        return TransactionGoodsDTO.toDto(lvUpdatedTransaction);
    }

    @Transactional
    @Override
    public String deleteExportTransaction(Long pTranId) {
        TransactionGoods lvTransactionGoods = super.findEntById(pTranId, true);
        if (!TransactionGoodsStatus.DRAFT.equals(lvTransactionGoods.getTransactionStatus())) {
            throw new BadRequestException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        mvEntityRepository.deleteById(lvTransactionGoods.getId());

        systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_TICKET_EX, MasterObject.TicketExport, "Xóa phiếu xuất hàng", lvTransactionGoods.getTitle());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public String generateItemsByOrderCode(Long pTranId, String pOrderCode) {
        TransactionGoods lvTransactionGoods = super.findEntById(pTranId, true);
        Order lvOrder = orderRepository.findByOrderCode(pOrderCode);

        if (lvOrder == null) {
            throw new BadRequestException("Invalid order!");
        }

        List<TransactionGoodsItem> lvItems = lvOrder.getListOrderDetail().stream()
                .map(lvItem -> TransactionGoodsItem.builder()
                        .transactionGoods(lvTransactionGoods)
                        .productVariant(lvItem.getProductDetail())
                        .quantity(lvItem.getQuantity())
                        .build()).toList();
        transactionGoodsItemRepository.saveAll(lvItems);

        lvTransactionGoods.setOrder(lvOrder);
        mvEntityRepository.save(lvTransactionGoods);

        return "Generated successfully";
    }

    @Override
    public List<TransactionGoodsItemDTO> getItems(Long pTranId) {
        return TransactionGoodsItemDTO.toDTOs(transactionGoodsItemRepository.findByTranId(pTranId));
    }

    @Override
    public List<TransactionGoodsItem> addItems(Long pTranId, TransactionGoodsReq pReq) {
        TransactionGoods lvTransactionGoods = super.findEntById(pTranId, true);

        List<TransactionGoodsItemReq> lvItems = pReq.getReqItems();
        if (ObjectUtils.isEmpty(lvItems)) {
            throw new BadRequestException("Please choose at least one product!");
        }

        List<TransactionGoodsItem> lvSavedItems = new ArrayList<>();
        for (TransactionGoodsItemReq lvItem : lvItems) {
            ProductDetail lvProductVariant = productVariantRepository.findById(lvItem.getProductVariantId())
                    .orElseThrow(() -> new BadRequestException(String.format("ItemId %s is invalid!", lvItem.getProductVariantId())));
            int lvItemQty = CoreUtils.coalesce(lvItem.getQuantity(), 1);

            TransactionGoodsItem lvTransactionGoodsItem = transactionGoodsItemRepository.findByTranIdAndProductVariantId(lvTransactionGoods.getId(), lvProductVariant.getId());
            if (lvTransactionGoodsItem != null) {
                lvTransactionGoodsItem.setQuantity(lvTransactionGoodsItem.getQuantity() + lvItemQty);
                transactionGoodsItemRepository.save(lvTransactionGoodsItem);
            } else {
                lvSavedItems.add(transactionGoodsItemRepository.save(TransactionGoodsItem.builder()
                        .transactionGoods(lvTransactionGoods)
                        .productVariant(lvProductVariant)
                        .quantity(lvItemQty)
                        .build()));
            }
        }

        return lvSavedItems;
    }

    @Override
    public List<FileStorage> getUploadedImages(Long pTranId) {
        return fileStorageRepository.findByTransactionGoodsId(pTranId);
    }

    private String generateTransactionGoodsCode(TransactionGoodsType pType) {
        if (CoreUtils.isNullStr(pType)) {
            throw new IllegalArgumentException();
        }

        TransactionGoods lvLatestTrans = mvEntityRepository.findTopByTransactionTypeOrderByIdDesc(pType);
        String lvLatestCode = lvLatestTrans == null ? "" : CoreUtils.trim(lvLatestTrans.getCode());
        String lvPrefixCode = pType.equals(TransactionGoodsType.IMPORT)
                ? PREFIX_TRANSACTION_IMPORT_CODE
                : PREFIX_TRANSACTION_EXPORT_CODE;
        if (CoreUtils.isNullStr(lvLatestCode)) {
            return lvPrefixCode + "00001";
        }

        int lvCurrentIndex = Integer.parseInt(lvLatestCode.substring(lvPrefixCode.length()));
        return lvPrefixCode + (lvCurrentIndex + 1);
    }

    void checkWarehouseCapacity() {
//        @Override
//        public TicketImport save(TicketImport entity) {
//            if (entity == null) {
//                throw new BadRequestException();
//            }
//            TicketImport ticketImportSaved = mvTicketImportRepository.save(entity);
//            Storage storage = ticketImportSaved.getStorage();
//            if (storage != null) {
//                if (storage.getHoldableQty() != null && storage.getHoldWarningPercent() != null) {
//                    int productQty = 0;
//                    int materialQty = 0;
//                    if (ObjectUtils.isNotEmpty(ticketImportSaved.getListProductVariantTemps())) {
//                        productQty = ticketImportSaved.getListProductVariantTemps().size();
//                    }
//                    if (ObjectUtils.isNotEmpty(ticketImportSaved.getListMaterialTemps())) {
//                        materialQty = ticketImportSaved.getListMaterialTemps().size();
//                    }
//                    int totalGoodsImport = productQty + materialQty;
//                    int totalGoodsHolding = 0;
//                    if ((totalGoodsImport + totalGoodsHolding) / storage.getHoldableQty() * 100 >= storage.getHoldWarningPercent()) {
//                        List<AccountRole> listOfStorageManagersRight = mvRoleService.findByAction(ACTION.STG_STORAGE);
//                        if (ObjectUtils.isNotEmpty(listOfStorageManagersRight)){
//                            Set<Account> stgManagersReceiveNtfs = new HashSet<>();
//                            for (AccountRole storageManagerRight : listOfStorageManagersRight) {
//                                GroupAccountDTO groupAccount = mvGroupAccountService.findById(storageManagerRight.getGroupId(), false);
//                                if (groupAccount != null) {
//                                    //stgManagersReceiveNtfs.addAll(groupAccount.getListAccount());
//                                }
//                                Optional<Account> account = mvAccountRepository.findById(storageManagerRight.getAccountId());
//                                if (account.isPresent()) {
//                                    stgManagersReceiveNtfs.add(account.get());
//                                }
//                            }
//                            for (Account a : stgManagersReceiveNtfs) {
//                                Notification lvNotify = Notification.builder()
//                                        .send(0l)
//                                        .receive(a.getId())
//                                        .type("WARNING")
//                                        .title("Sức chứa của kho " + storage.getName() + " đã chạm mốc cảnh báo!")
//                                        .content("Số lượng hàng hóa hiện tại " + totalGoodsHolding + ", Số lượng nhập thêm: " + totalGoodsImport + ", Số lượng sau khi nhập: " + totalGoodsImport + totalGoodsHolding + "/" + storage.getHoldableQty())
//                                        .readed(false)
//                                        .importId(ticketImportSaved.getId())
//                                        .build();
//                                mvNotificationService.save(mvModelMapper.map(lvNotify, NotificationDTO.class));
//                            }
//                        }
//                    }
//                }
//            }
//            return ticketImportSaved;
//        }
    }
}