package com.flowiee.pms.product.service.impl;

import com.flowiee.pms.inventory.dto.StorageDTO;
import com.flowiee.pms.inventory.dto.TransactionGoodsDTO;
import com.flowiee.pms.inventory.entity.Storage;
import com.flowiee.pms.inventory.enums.TransactionGoodsType;
import com.flowiee.pms.inventory.service.StorageService;
import com.flowiee.pms.inventory.service.TransactionGoodsService;
import com.flowiee.pms.product.dto.ProductPriceDTO;
import com.flowiee.pms.product.dto.ProductVariantDTO;
import com.flowiee.pms.product.dto.ProductVariantTempDTO;
import com.flowiee.pms.product.entity.Product;
import com.flowiee.pms.product.entity.ProductDetail;
import com.flowiee.pms.product.entity.ProductPrice;
import com.flowiee.pms.product.entity.ProductVariantExim;
import com.flowiee.pms.product.enums.ProductStatus;
import com.flowiee.pms.product.service.*;
import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.exception.DataExistsException;
import com.flowiee.pms.shared.exception.DataInUseException;
import com.flowiee.pms.shared.jpa.JpaHints;
import com.flowiee.pms.shared.request.BaseParameter;
import com.flowiee.pms.shared.util.*;
import com.flowiee.pms.inventory.enums.TransactionGoodsStatus;
import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.system.enums.CATEGORY;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.system.service.CategoryService;
import com.flowiee.pms.system.service.SendOperatorNotificationService;
import com.flowiee.pms.system.service.SystemLogService;
import com.flowiee.pms.cart.entity.Items;
import com.flowiee.pms.cart.entity.OrderCart;
import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.product.model.ProductVariantSearchRequest;
import com.flowiee.pms.cart.repository.OrderCartRepository;
import com.flowiee.pms.shared.base.GenerateBarcodeService;
import com.flowiee.pms.cart.service.CartService;
import com.flowiee.pms.product.repository.ProductDetailRepository;
import com.flowiee.pms.product.repository.ProductDetailTempRepository;
import com.flowiee.pms.product.mapper.ProductVariantConvert;
import com.google.zxing.WriterException;
import javax.persistence.EntityGraph;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductVariantServiceImpl extends BaseService<ProductDetail, ProductVariantDTO, ProductDetailRepository> implements ProductVariantService {
    private final SendOperatorNotificationService mvSendOperatorNotificationService;
    private final ProductGenerateQRCodeService mvProductGenerateQRCodeService;
    private final ProductDetailTempRepository mvProductVariantTempRepository;
    private final TransactionGoodsService mvTransactionGoodsService;
    private final GenerateBarcodeService mvGenerateBarcodeService;
    private final ProductHistoryService mvProductHistoryService;
    private final ProductPriceService mvProductPriceService;
    private final ProductImageService mvProductImageService;
    private final ProductService mvProductService;
    private final OrderCartRepository mvCartRepository;
    private final SystemLogService mvSystemLogService;
    private final CategoryService mvCategoryService;
    private final StorageService mvStorageService;
    private final CartService mvCartService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final ProductService productService;

    public ProductVariantServiceImpl(ProductDetailRepository pEntityRepository, ProductDetailTempRepository pProductVariantTempRepository,
                                     ProductGenerateQRCodeService pProductGenerateQRCodeService, ProductHistoryService pProductHistoryService,
                                     TransactionGoodsService pTransactionGoodsService, CategoryService pCategoryService,
                                     StorageService pStorageService, OrderCartRepository pCartRepository,
                                     GenerateBarcodeService pGenerateBarcodeService, @Lazy ProductPriceService pProductPriceService,
                                     SystemLogService pSystemLogService, SendOperatorNotificationService pSendOperatorNotificationService,
                                     @Lazy ProductService pProductService, @Lazy CartService pCartService, @Lazy ProductImageService pProductImageService, ProductService productService) {
        super(ProductDetail.class, ProductVariantDTO.class, pEntityRepository);
        this.mvSendOperatorNotificationService = pSendOperatorNotificationService;
        this.mvProductVariantTempRepository = pProductVariantTempRepository;
        this.mvProductGenerateQRCodeService = pProductGenerateQRCodeService;
        this.mvTransactionGoodsService = pTransactionGoodsService;
        this.mvGenerateBarcodeService = pGenerateBarcodeService;
        this.mvProductHistoryService = pProductHistoryService;
        this.mvProductImageService = pProductImageService;
        this.mvProductPriceService = pProductPriceService;
        this.mvProductService = pProductService;
        this.mvSystemLogService = pSystemLogService;
        this.mvCategoryService = pCategoryService;
        this.mvStorageService = pStorageService;
        this.mvCartRepository = pCartRepository;
        this.mvCartService = pCartService;
        this.productService = productService;
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

        OrderCart currentCart = Boolean.TRUE.equals(pRequest.getCheckInAnyCart()) ? getCurrentCart() : null;
        List<Items> lvCartItems = currentCart != null ? mvCartService.getItems(currentCart.getId(), lvProductVariantIds) : List.of();
        List<Long> lvCartItemIds = lvCartItems.isEmpty() ? List.of() : lvCartItems.stream().map(a -> a.getProductDetail().getId()).toList();

        for (ProductVariantDTO lvDto : lvProductVariantDTOs) {
            ProductPriceDTO lvPrice = mvProductPriceService.getPrice(lvDto.getId());
            lvDto.setPrice(lvPrice);

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
        List<OrderCart> cartList = mvCartRepository.findByAccountId(SecurityUtils.getCurrentUser().getId());
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

        lvDto.setPrice(mvProductPriceService.getPrice(pProductVariantId));

        return lvDto;
    }

    @Override
    public ProductDetail findEntById(Long pId, boolean throwException) {
        return super.findEntById(pId, throwException);
    }

    @Override
    public List<ProductVariantDTO> save(Long pProductId, List<ProductVariantDTO> pVariantDTOs) {
        List<ProductVariantDTO> lvProductVariantSavedList = new ArrayList<>();
        if (pProductId == null || pProductId <= 0 || CollectionUtils.isEmpty(pVariantDTOs)) {
            return lvProductVariantSavedList;
        }

        Product lvProduct = productService.findEntById(pProductId, true);
        for (ProductVariantDTO lvVariant : pVariantDTOs) {
            ProductPriceDTO lvPrice = lvVariant.getPrice();

            lvVariant.setProductId(lvProduct.getId());
            lvVariant.setColorId(lvVariant.getColor().getId());
            lvVariant.setSizeId(lvVariant.getSize().getId());
            lvVariant.setFabricTypeId(lvVariant.getFabricType().getId());
            lvVariant.setVariantName(lvProduct.getProductName());
            lvVariant.setDefectiveQty(0);
            lvVariant.setNote("");
            lvVariant.setPrice(ProductPriceDTO.builder()
                    .retailPrice(lvPrice.getRetailPrice())
                    .wholesalePrice(lvPrice.getWholesalePrice())
                    .costPrice(lvPrice.getCostPrice())
                    .build());
            lvVariant.setStatus(ProductStatus.INA);

            lvProductVariantSavedList.add(this.save(lvVariant));
        }

        return lvProductVariantSavedList;
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
        Product lvProduct = mvProductService.findEntById(pDto.getProductId(), true);

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

        TransactionGoodsDTO lvTransactionGoodsDto = mvTransactionGoodsService.createExportTransaction(TransactionGoodsDTO.builder()
                .title("Initialize storage")
                .transactionType(TransactionGoodsType.EXPORT)
                .warehouse(new StorageDTO(lvStorage.getId()))
                .transactionStatus(TransactionGoodsStatus.APPROVED)
                .description(initMessage)
                .build());

        mvProductVariantTempRepository.save(ProductVariantExim.builder()
                //.ticketImport(new TicketImport(lvTransactionGoodsDto.getId()))
                .productVariant(pProductVariantSaved)
                .quantity(pProductVariantSaved.getStorageQty())
                .note(initMessage)
                .build());
    }

    private void initSoldQuantity(ProductDetail pProductVariantSaved, ProductVariantDTO pDto) {
        Storage lvStorage = mvStorageService.findEntById(pDto.getStorageIdInitStorageQty(), true);
        String initMessage = "Initialize storage quantity when create new products";

        TransactionGoodsDTO lvTransactionGoodsDto = mvTransactionGoodsService.createExportTransaction(TransactionGoodsDTO.builder()
                .title("Initialize storage")
                .transactionType(TransactionGoodsType.EXPORT)
                .warehouse(new StorageDTO(lvStorage.getId()))
                .transactionStatus(TransactionGoodsStatus.APPROVED)
                .description(initMessage)
                .build());

        mvProductVariantTempRepository.save(ProductVariantExim.builder()
                //.ticketExport(new TicketExport(lvTransactionGoodsDto.getId()))
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

    @Transactional
    @Override
    public String delete(Long productVariantId) {
        ProductDetail productDetailToDelete = super.findEntById(productVariantId, true);

        //Validate here...

        try {
            //Begin 2026/03/28 Replace hard delete by soft delete
            //mvEntityRepository.deleteById(productVariantId);
            mvEntityRepository.softDelete(productVariantId, LocalDateTime.now(), String.valueOf(SecurityUtils.getCurrentUser().getId()));
            //End 2026/03/28
        } catch (DataIntegrityViolationException ex) {
            throw new DataInUseException("Data in use!", ex);
        } catch (DataAccessException ex) {
            throw new AppException("Database error", ex);
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
    public List<ProductVariantTempDTO> findStorageHistoryByProductId(Long productId) {
        List<ProductVariantExim> storageHistory = mvProductVariantTempRepository.findByProductId(productId);
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
                changeQty = "+/- " + tempDTO.getQuantity();
            }
//            if (tempDTO.getTicketImport() != null) {
//                staff = tempDTO.getTicketImport().getImporter();
//                for (TicketImportAction importAction : TicketImportAction.values()) {
//                    if (importAction.name().equals(tempDTO.getAction()) ) {
//                        actionLabel = importAction.getLabel();
//                        break;
//                    }
//                }
//            }
//            if (tempDTO.getTicketExport() != null) {
//                staff = tempDTO.getTicketExport().getExporter();
//                for (TicketExportAction exportAction : TicketExportAction.values()) {
//                    if (exportAction.name().equals(tempDTO.getAction()) ) {
//                        actionLabel = exportAction.getLabel();
//                        break;
//                    }
//                }
//            }
            tempDTO.setStaff(staff);
            tempDTO.setAction(actionLabel);
            tempDTO.setChangeQty(changeQty);
            tempDTO.setStorageQty(tempDTO.getStorageQty());
            tempDTO.setBranchName(branchName);
        }
        return storageHistoryDTOs;
    }

    @Override
    public List<ProductVariantTempDTO> findStorageHistoryByVariantId(Long productVariantId) {
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
                changeQty = "+/- " + tempDTO.getQuantity();
            }
//            if (tempDTO.getTicketImport() != null) {
//                staff = tempDTO.getTicketImport().getImporter();
//                for (TicketImportAction importAction : TicketImportAction.values()) {
//                    if (importAction.name().equals(tempDTO.getAction()) ) {
//                        actionLabel = importAction.getLabel();
//                        break;
//                    }
//                }
//            }
//            if (tempDTO.getTicketExport() != null) {
//                staff = tempDTO.getTicketExport().getExporter();
//                for (TicketExportAction exportAction : TicketExportAction.values()) {
//                    if (exportAction.name().equals(tempDTO.getAction()) ) {
//                        actionLabel = exportAction.getLabel();
//                        break;
//                    }
//                }
//            }
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
    public void updateStatus(Long pProductVariantId, ProductStatus pStatus) {
        ProductDetail lvProductVariant = super.findEntById(pProductVariantId, true);
        lvProductVariant.setStatus(pStatus);
        mvEntityRepository.save(lvProductVariant);
    }

    @Override
    public List<ProductVariantDTO> getProductsOutOfStock() {
        List<ProductDetail> productVariants = mvEntityRepository.findProductsOutOfStock();
        return ProductVariantConvert.entitiesToDTOs(productVariants);
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