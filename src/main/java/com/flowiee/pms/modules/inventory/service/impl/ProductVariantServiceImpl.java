package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.constants.JpaHints;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.inventory.dto.*;
import com.flowiee.pms.modules.inventory.service.*;
import com.flowiee.pms.modules.inventory.service.ProductGenerateQRCodeService;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.system.service.CategoryService;
import com.flowiee.pms.modules.system.service.SendOperatorNotificationService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import com.flowiee.pms.modules.inventory.entity.ProductVariantExim;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.modules.inventory.entity.TicketImport;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.inventory.model.ProductVariantSearchRequest;
import com.flowiee.pms.modules.inventory.repository.ProductPriceRepository;
import com.flowiee.pms.modules.sales.repository.OrderCartRepository;
import com.flowiee.pms.common.base.service.GenerateBarcodeService;
import com.flowiee.pms.modules.sales.service.CartService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.inventory.repository.ProductDetailTempRepository;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.inventory.util.ProductVariantConvert;
import com.google.zxing.WriterException;
import javax.persistence.EntityGraph;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductVariantServiceImpl extends BaseService<ProductDetail, ProductVariantDTO, ProductDetailRepository> implements ProductVariantService {
    private final SendOperatorNotificationService mvSendOperatorNotificationService;
    private final ProductDetailTempRepository mvProductVariantTempRepository;
    private final ProductPriceRepository mvProductPriceRepository;
    private final ProductGenerateQRCodeService mvProductGenerateQRCodeService;
    private final ProductHistoryService mvProductHistoryService;
    private final TicketImportService mvTicketImportService;
    private final TicketExportService mvTicketExportService;
    private final CategoryService mvCategoryService;
    private final StorageService mvStorageService;
    private final OrderCartRepository mvCartRepository;
    private final GenerateBarcodeService mvGenerateBarcodeService;
    private final ProductPriceService mvProductPriceService;
    private final SystemLogService mvSystemLogService;
    private final ProductInfoService mvProductInfoService;
    private final CartService mvCartService;
    private final ProductImageService mvProductImageService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public ProductVariantServiceImpl(ProductDetailRepository pEntityRepository, ProductDetailTempRepository pProductVariantTempRepository,
                                     ProductPriceRepository pProductPriceRepository, ProductGenerateQRCodeService pProductGenerateQRCodeService,
                                     ProductHistoryService pProductHistoryService, @Lazy TicketImportService pTicketImportService,
                                     @Lazy TicketExportService pTicketExportService, CategoryService pCategoryService,
                                     StorageService pStorageService, OrderCartRepository pCartRepository,
                                     GenerateBarcodeService pGenerateBarcodeService, ProductPriceService pProductPriceService,
                                     SystemLogService pSystemLogService, SendOperatorNotificationService pSendOperatorNotificationService,
                                     @Lazy ProductInfoService pProductInfoService, @Lazy CartService pCartService, @Lazy ProductImageService pProductImageService) {
        super(ProductDetail.class, ProductVariantDTO.class, pEntityRepository);
        this.mvSendOperatorNotificationService = pSendOperatorNotificationService;
        this.mvProductVariantTempRepository = pProductVariantTempRepository;
        this.mvProductPriceRepository = pProductPriceRepository;
        this.mvProductGenerateQRCodeService = pProductGenerateQRCodeService;
        this.mvProductHistoryService = pProductHistoryService;
        this.mvTicketImportService = pTicketImportService;
        this.mvTicketExportService = pTicketExportService;
        this.mvStorageService = pStorageService;
        this.mvCartRepository = pCartRepository;
        this.mvGenerateBarcodeService = pGenerateBarcodeService;
        this.mvProductPriceService = pProductPriceService;
        this.mvSystemLogService = pSystemLogService;
        this.mvProductInfoService = pProductInfoService;
        this.mvCategoryService = pCategoryService;
        this.mvCartService = pCartService;
        this.mvProductImageService = pProductImageService;
    }

    @Override
    public List<ProductVariantDTO>find(BaseParameter pParam) {
        return this.findAll(ProductVariantSearchRequest.builder().checkInAnyCart(false).build()).getContent();
    }

    @Override
    public Page<ProductVariantDTO> findAll(ProductVariantSearchRequest pRequest) {
        Pageable lvPageable = getPageable(pRequest.getPageNum(), pRequest.getPageSize(), Sort.by("variantName").ascending());

        QueryBuilder<ProductDetail> lvQueryBuilder = buildSearchQuery(pRequest);
        EntityGraph<ProductDetail> lvEntityGraph = buildEntityGraph();

        List<ProductDetail> lvProductVariants = executeQuery(lvQueryBuilder, lvEntityGraph, lvPageable);
        long lvTotalRecords = lvQueryBuilder.buildCount();

        List<ProductVariantDTO> lvProductVariantDTOs = super.convertDTOs(lvProductVariants);
        List<Long> lvProductVariantIds = lvProductVariants.stream().map(ProductDetail::getId).toList();

        Map<Long, FileStorage> lvImageActiveList = mvProductImageService.getImageActiveOfProductVariants(lvProductVariantIds);

        OrderCart currentCart = pRequest.getCheckInAnyCart() ? getCurrentCart() : null;
        List<Items> lvCartItems = currentCart != null ? mvCartService.getItems(currentCart.getId(), lvProductVariantIds) : List.of();
        List<Long> lvCartItemIds = lvCartItems.isEmpty() ? List.of() : lvCartItems.stream().map(a -> a.getProductDetail().getId()).toList();

        for (ProductVariantDTO lvDto : lvProductVariantDTOs) {
            mvProductPriceService.assignPriceInfo(lvDto, mvProductPriceRepository.findPresentPrices(lvDto.getId()));
            String lvImageUrl = FileUtils.getImageUrl(lvImageActiveList.get(lvDto.getId()), true);
            lvDto.setImageSrc(lvImageUrl != null ? lvImageUrl : EndPoint.URL_MEDIA_DEFAULT_PRODUCT.getValue());
            lvDto.setCurrentInCart(lvCartItemIds.contains(lvDto.getId()));
        }

        return new PageImpl<>(lvProductVariantDTOs, lvPageable, lvTotalRecords);
    }

    private QueryBuilder<ProductDetail> buildSearchQuery(ProductVariantSearchRequest pRequest) {
        QueryBuilder<ProductDetail> lvQueryBuilder = createQueryBuilder(ProductDetail.class)
                .addLike("variantCode", pRequest.getTxtSearch())
                .addLike("variantName", pRequest.getTxtSearch())
                .addEqual("product.id", pRequest.getProductId())
                .addEqual("product.brand.id", pRequest.getBrandId())
                .addEqual("color.id", pRequest.getColorId())
                .addEqual("size.id", pRequest.getSizeId())
                .addEqual("fabricType.id", pRequest.getFabricTypeId());
        if (pRequest.getAvailableForSales() != null) {
            lvQueryBuilder.addPredicate(cb -> {
                //storageQty - defectiveQty
                Expression<Integer> availableQty = cb.diff(
                        lvQueryBuilder.getRoot().get("storageQty"),
                        lvQueryBuilder.getRoot().get("defectiveQty")
                );
                return Boolean.TRUE.equals(pRequest.getAvailableForSales())
                        ? cb.greaterThan(availableQty, 0)
                        : cb.lessThan(availableQty, 1);
            });
        }
        return lvQueryBuilder;
    }

    private EntityGraph<ProductDetail> buildEntityGraph() {
        EntityGraph<ProductDetail> lvEntityGraph = mvEntityManager.createEntityGraph(ProductDetail.class);
        lvEntityGraph.addSubgraph("product").addAttributeNodes("productType", "unit", "brand");
        lvEntityGraph.addSubgraph("color");
        lvEntityGraph.addSubgraph("size");
        lvEntityGraph.addSubgraph("fabricType");
        lvEntityGraph.addSubgraph("priceList");
        return lvEntityGraph;
    }

    private List<ProductDetail> executeQuery(QueryBuilder<ProductDetail> pQueryBuilder, EntityGraph<ProductDetail> pEntityGraph, Pageable pPageable) {
        return pQueryBuilder.build(pPageable)
                .setHint(JpaHints.FETCH_GRAPH, pEntityGraph)
                .getResultList();
    }

    private OrderCart getCurrentCart() {
        List<OrderCart> cartList = mvCartRepository.findByAccountId(getUserPrincipal().getId());
        if (ObjectUtils.isNotEmpty(cartList)) {
            return cartList.get(0);
        }
        return null;
    }

    @Override
    public ProductVariantDTO findById(Long pProductVariantId, boolean pThrowException) {
        ProductDetail lvEntity = super.findEntById(pProductVariantId, pThrowException);
        ProductVariantDTO lvDto = super.convertDTO(lvEntity);

        List<ProductPrice> lvProductPrice = new ArrayList<>();
        if (lvEntity.getPriceList() != null) {
            for (ProductPrice price : lvEntity.getPriceList()) {
                if (ProductPrice.STATE_ACTIVE.equals(price.getState()))
                    lvProductPrice.add(price);
            }
        }
        mvProductPriceService.assignPriceInfo(lvDto, lvProductPrice);

        return lvDto;
    }

    @Override
    public ProductDetail findEntById(Long pId, boolean throwException) {
        return super.findEntById(pId, throwException);
    }

    @Transactional
    @Override
    public ProductVariantDTO save(ProductVariantDTO pDto) {
        try {
            ProductDetail lvVariantEntity = buildEntity(pDto);
            ProductDetail lvProductVariantSaved = mvEntityRepository.save(lvVariantEntity);

            mvProductPriceService.save(lvProductVariantSaved, pDto.getPrice());

            //Make it async in next version
            try {
                mvProductGenerateQRCodeService.generateProductVariantQRCode(lvProductVariantSaved.getId());
            } catch (IOException | WriterException e ) {
                LOG.error(String.format("Can't generate QR Code for Product %s", lvProductVariantSaved.getVariantCode()), e);
            }

            //Make it async in next version
            try {
                mvGenerateBarcodeService.generateBarcode(lvProductVariantSaved.getId());
            } catch (IOException | WriterException e ) {
                LOG.error(String.format("Can't generate Barcode for Product %s", lvProductVariantSaved.getVariantCode()), e);
            }

            if (pDto.getStorageIdInitStorageQty() != null) {
                if (lvProductVariantSaved.getStorageQty() > 0) {
                    initInventoryQuantity(lvProductVariantSaved, pDto);
                }

                if (lvProductVariantSaved.getSoldQty() > 0) {
                    initSoldQuantity(lvProductVariantSaved, pDto);
                }
            }

            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, "Thêm mới biến thể sản phẩm", lvProductVariantSaved.toStringInsert());
            LOG.info("Insert productVariant success! {}", lvProductVariantSaved);

            return ProductVariantConvert.toDto(lvProductVariantSaved);
        } catch (RuntimeException ex) {
            throw new AppException(ex.getMessage(), ex);
        }
    }

    private ProductDetail buildEntity(ProductVariantDTO pDto) {
        Product lvProduct = mvProductInfoService.findEntById(pDto.getProductId(), true);

        Map<CATEGORY, Category> lvCategoryMap = mvCategoryService.findByIdsAsMap(Set.of(pDto.getColorId(),
                pDto.getSizeId(), pDto.getFabricTypeId()));
        Category lvColor = lvCategoryMap.get(CATEGORY.COLOR);
        Category lvSize = lvCategoryMap.get(CATEGORY.SIZE);
        Category lvFabricType = lvCategoryMap.get(CATEGORY.FABRIC_TYPE);

        if (checkVariantExisted(lvProduct.getId(), lvColor.getId(), lvSize.getId(), lvFabricType.getId())) {
            throw new DataExistsException("This product variant already exists!");
        }

        return ProductDetail.builder()
                .product(lvProduct)
                .color(lvColor)
                .size(lvSize)
                .fabricType(lvFabricType)
                .variantCode(genProductCode(pDto.getVariantCode()))
                .variantName(pDto.getVariantName())
                .soldQty(CoreUtils.coalesce(pDto.getSoldQty()))
                .storageQty(CoreUtils.coalesce(pDto.getStorageQty()))
                .defectiveQty(CoreUtils.coalesce(pDto.getDefectiveQty()))
                .weight(pDto.getWeight())
                .note(pDto.getNote())
                .status(ProductStatus.ACT)
                .sku(generateSKUCode())
                .build();
    }

    private void initInventoryQuantity(ProductDetail pProductVariantSaved, ProductVariantDTO pDto) {
        Storage lvStorage = mvStorageService.findEntById(pDto.getStorageIdInitStorageQty(), true);
        String initMessage = "Initialize storage quantity when create new products";

        TicketImport ticketImportSaved = mvTicketImportService.save(TicketImport.builder()
                .title("Initialize storage")
                .importer(getUserPrincipal().getUsername())
                .importTime(LocalDateTime.now())
                .note(initMessage)
                .status(TicketImportStatus.COMPLETED.name())
                .storage(lvStorage)
                .build());

        mvProductVariantTempRepository.save(ProductVariantExim.builder()
                .ticketImport(ticketImportSaved)
                .productVariant(pProductVariantSaved)
                .quantity(pProductVariantSaved.getStorageQty())
                .note(initMessage)
                .build());
    }

    private void initSoldQuantity(ProductDetail pProductVariantSaved, ProductVariantDTO pDto) {
        Storage lvStorage = mvStorageService.findEntById(pDto.getStorageIdInitStorageQty(), true);
        String initMessage = "Initialize storage quantity when create new products";

        TicketExportDTO lvTicketExport = new TicketExportDTO();
        lvTicketExport.setTitle("Initialize storage");
        lvTicketExport.setExporter(getUserPrincipal().getUsername());
        lvTicketExport.setExportTime(LocalDateTime.now());
        lvTicketExport.setNote(initMessage);
        lvTicketExport.setStatus(TicketExportStatus.COMPLETED.name());
        lvTicketExport.setStorage(new StorageDTO(lvStorage.getId()));
        TicketExportDTO ticketExportSaved = mvTicketExportService.save(lvTicketExport);

        mvProductVariantTempRepository.save(ProductVariantExim.builder()
                .ticketExport(new TicketExport(ticketExportSaved.getId()))
                .productVariant(pProductVariantSaved)
                .quantity(pProductVariantSaved.getStorageQty())
                .note(initMessage)
                .build());
    }

    @Transactional
    @Override
    public ProductVariantDTO update(ProductVariantDTO pDto, Long productVariantId) {
        ProductDetail lvVariant = super.findEntById(productVariantId, true);
        try {
            ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvVariant));

            lvVariant.setVariantName(pDto.getVariantName());
            lvVariant.setDefectiveQty(pDto.getDefectiveQty());
            lvVariant.setWeight(pDto.getWeight());
            lvVariant.setNote(pDto.getNote());
            ProductDetail lvProductVariantUpdated = mvEntityRepository.save(lvVariant);

            changeLog.setNewObject(lvProductVariantUpdated);
            changeLog.doAudit();

            mvProductPriceService.updatePrice(lvProductVariantUpdated, pDto.getPrice());

            //Log
            String logTitle = "Cập nhật thông tin sản phẩm: " + lvProductVariantUpdated.getVariantName();
            mvProductHistoryService.save(changeLog.getLogChanges(), logTitle, lvProductVariantUpdated.getProduct().getId(), lvProductVariantUpdated.getId(), null);
            mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, logTitle, changeLog.getOldValues(), changeLog.getNewValues());
            LOG.info("Update productVariant success! {}", lvProductVariantUpdated);

            return ProductVariantConvert.toDto(lvProductVariantUpdated);
        } catch (Exception e) {
            throw new AppException("Update productVariant fail! " + pDto.toString(), e);
        }
    }

    @Override
    public String delete(Long productVariantId) {
        ProductDetail productDetailToDelete = super.findEntById(productVariantId, true);
        try {
            mvEntityRepository.deleteById(productVariantId);
        } catch (ConstraintViolationException ex) {
            throw new DataInUseException("Không thể xóa sản phẩm đã được sử dụng!", ex);
        }
        mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, "Xóa biến thể sản phẩm", productDetailToDelete.getVariantName());
        LOG.info("Delete productVariant success! {}", productDetailToDelete);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public boolean checkVariantExisted(long productId, long colorId, long sizeId, long fabricTypeId) {
        ProductDetail productDetail = mvEntityRepository.findByColorAndSize(productId, colorId, sizeId, fabricTypeId);
        return ObjectUtils.isNotEmpty(productDetail);
    }

    @Override
    public List<ProductVariantTempDTO> findStorageHistory(Long productVariantId) {
        List<ProductVariantExim> storageHistory = mvProductVariantTempRepository.findByProductVariantId(productVariantId);
        List<ProductVariantTempDTO> storageHistoryDTOs = ProductVariantTempDTO.convertToDTOs(storageHistory);
        if (ObjectUtils.isEmpty(storageHistoryDTOs)) {
            return List.of();
        }
        for (ProductVariantTempDTO tempDTO : storageHistoryDTOs) {
            String staff = "";
            String actionLabel = "";
            String changeQty = "";
            String branchName = "";
            if (tempDTO.getQuantity() > 0) {
                changeQty = "+ " + tempDTO.getQuantity();
            }
            if (tempDTO.getTicketImport() != null) {
                staff = tempDTO.getTicketImport().getImporter();
                for (TicketImportAction importAction : TicketImportAction.values()) {
                    if (importAction.name().equals(tempDTO.getAction()) ) {
                        actionLabel = importAction.getLabel();
                        break;
                    }
                }
            }
            if (tempDTO.getTicketExport() != null) {
                staff = tempDTO.getTicketExport().getExporter();
                for (TicketExportAction exportAction : TicketExportAction.values()) {
                    if (exportAction.name().equals(tempDTO.getAction()) ) {
                        actionLabel = exportAction.getLabel();
                        break;
                    }
                }
            }
            tempDTO.setStaff(staff);
            tempDTO.setAction(actionLabel);
            tempDTO.setChangeQty(changeQty);
            tempDTO.setStorageQty(tempDTO.getStorageQty());
            tempDTO.setBranchName(branchName);
        }
        return storageHistoryDTOs;
    }

    @Override
    public void updateLowStockThreshold(Long pProductId, int pThreshold) {
        ProductDetail lvProduct = super.findEntById(pProductId, true);
        lvProduct.setLowStockThreshold(pThreshold);
        mvEntityRepository.save(lvProduct);
    }

    @Override
    public void updateStockQuantity(Long pProductVariantId, Integer pQuantity, String pUpdateType) {
        ProductDetail lvProductDetail = super.findEntById(pProductVariantId, true);
        Integer lvLowStockThreshold = lvProductDetail.getLowStockThreshold();
        int lvCurrentQuantity = lvProductDetail.getStorageQty();
        try {
            if ("I".equals(pUpdateType)) {
                mvEntityRepository.updateQuantityIncrease(pQuantity, pProductVariantId);
            } else if ("D".equals(pUpdateType)) {
                if (lvCurrentQuantity < pQuantity)
                    throw new BadRequestException("Hàng tồn kho không đủ số lượng!");

                lvProductDetail.setStorageQty(lvCurrentQuantity - pQuantity);
                lvProductDetail.setSoldQty(lvProductDetail.getSoldQty() + pQuantity);
                ProductDetail productDetailUpdated = mvEntityRepository.save(lvProductDetail);

                if (lvLowStockThreshold != null && productDetailUpdated.getStorageQty() <= lvLowStockThreshold) {
                    if (SysConfigUtils.isYesOption(ConfigCode.lowStockAlert)) {
                        mvSendOperatorNotificationService.notifyWarningLowStock(productDetailUpdated);
                    }
                }

                //Hết hàng
                if (productDetailUpdated.getAvailableSalesQty() == 0) {
                    productDetailUpdated.setStatus(ProductStatus.OOS);
                    productDetailUpdated.setOutOfStockDate(LocalDateTime.now());
                    mvEntityRepository.save(productDetailUpdated);
                }
            }
            mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, "Cập nhật số lượng sản phẩm", "productVariantId = " + pProductVariantId);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "product quantity"), ex);
        }
    }

    @Override
    public void updateDefectiveQuantity(Long pProductVariantId, Integer pQuantity, String pUpdateType) {

    }

    @Override
    public Page<ProductVariantDTO> getProductsOutOfStock(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize);
        Page<ProductDetail> productVariants = mvEntityRepository.findProductsOutOfStock(pageable);
        List<ProductVariantDTO> productVariantDTOs = ProductVariantConvert.entitiesToDTOs(productVariants.getContent());
        for (ProductVariantDTO dto : productVariantDTOs) {
            //assignPriceInfo(dto, mvProductPriceService.findPresentPrice(dto.getId()));
        }
        return new PageImpl<>(productVariantDTOs, pageable, productVariantDTOs.size());
    }

    private String genProductCode(String defaultCode) {
        if (CoreUtils.isNullStr(defaultCode))
        {
            return CommonUtils.now("yyyyMMddHHmmssSSS");
        }
        return defaultCode;
    }

    private void vldPrice(BigDecimal lvRetailPrice, BigDecimal lvRetailPriceDiscount,
                          BigDecimal lvWholesalePrice, BigDecimal lvWholesalePriceDiscount,
                          BigDecimal lvPurchasePrice, BigDecimal lvCostPrice)
    {
        if (lvRetailPrice                   == null || lvRetailPrice.doubleValue()            < 0
                || lvRetailPriceDiscount    == null || lvRetailPriceDiscount.doubleValue()    < 0
                || lvWholesalePrice         == null || lvWholesalePrice.doubleValue()         < 0
                || lvWholesalePriceDiscount == null || lvWholesalePriceDiscount.doubleValue() < 0
                || lvPurchasePrice          == null || lvPurchasePrice.doubleValue()          < 0
                || lvCostPrice              == null || lvCostPrice.doubleValue()              < 0)
        {
            throw new BadRequestException("Price must greater than zero!");
        }

        if (!SysConfigUtils.isYesOption(ConfigCode.allowSellPriceLessThanCostPrice))
        {
            double sellingPrice = Math.min(lvRetailPriceDiscount.doubleValue(), lvWholesalePriceDiscount.doubleValue());
            double costPrice = Math.min(lvPurchasePrice.doubleValue(), lvCostPrice.doubleValue());
            if (sellingPrice < costPrice)
            {
                throw new BadRequestException("Selling price must greater than cost price!");
            }
        }
    }

    private String generateSKUCode() {
        //Do something pattern
        return UUID.randomUUID().toString();
    }
}