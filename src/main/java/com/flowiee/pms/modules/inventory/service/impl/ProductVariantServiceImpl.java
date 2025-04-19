package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.constants.JpaHints;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.inventory.dto.*;
import com.flowiee.pms.modules.inventory.enums.PriceType;
import com.flowiee.pms.modules.inventory.service.*;
import com.flowiee.pms.modules.inventory.service.ProductGenerateQRCodeService;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.system.service.SystemLogService;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.inventory.entity.ProductPrice;
import com.flowiee.pms.modules.inventory.entity.ProductVariantExim;
import com.flowiee.pms.modules.inventory.repository.ProductRepository;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.modules.inventory.entity.TicketImport;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.inventory.model.ProductVariantSearchRequest;
import com.flowiee.pms.modules.system.repository.CategoryRepository;
import com.flowiee.pms.modules.inventory.repository.ProductPriceRepository;
import com.flowiee.pms.modules.sales.repository.OrderCartRepository;
import com.flowiee.pms.modules.inventory.repository.StorageRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.system.service.CategoryService;
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
import jakarta.persistence.EntityGraph;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductVariantServiceImpl extends BaseService<ProductDetail, ProductVariantDTO, ProductDetailRepository> implements ProductVariantService {
    private final ProductDetailTempRepository mvProductVariantTempRepository;
    private final ProductPriceRepository mvProductPriceRepository;
    private final FileStorageRepository mvFileStorageRepository;
    private final ProductGenerateQRCodeService mvProductGenerateQRCodeService;
    private final ProductHistoryService mvProductHistoryService;
    private final TicketImportService mvTicketImportService;
    private final TicketExportService mvTicketExportService;
    private final CategoryService mvCategoryService;
    private final CategoryRepository mvCategoryRepository;
    private final StorageService mvStorageService;
    private final StorageRepository mvStorageRepository;
    private final OrderCartRepository mvCartRepository;
    private final GenerateBarcodeService mvGenerateBarcodeService;
    private final ProductPriceService mvProductPriceService;
    private final UserSession mvUserSession;
    private final SystemLogService mvSystemLogService;
    private final ProductRepository mvProductRepository;
    @Autowired
    @Lazy
    private CartService mvCartService;
    @Autowired
    @Lazy
    private ProductImageService mvProductImageService;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public ProductVariantServiceImpl(ProductDetailRepository pEntityRepository, ProductDetailTempRepository pProductVariantTempRepository,
                                     ProductPriceRepository pProductPriceRepository, FileStorageRepository pFileStorageRepository,
                                     ProductGenerateQRCodeService pProductGenerateQRCodeService, ProductHistoryService pProductHistoryService,
                                     TicketImportService pTicketImportService, TicketExportService pTicketExportService,
                                     CategoryService pCategoryService, CategoryRepository pCategoryRepository,
                                     StorageService pStorageService, StorageRepository pStorageRepository,
                                     OrderCartRepository pCartRepository, GenerateBarcodeService pGenerateBarcodeService,
                                     ProductPriceService pProductPriceService, UserSession pUserSession,
                                     SystemLogService pSystemLogService, ProductRepository pProductRepository) {
        super(ProductDetail.class, ProductVariantDTO.class, pEntityRepository);
        this.mvProductVariantTempRepository = pProductVariantTempRepository;
        this.mvProductPriceRepository = pProductPriceRepository;
        this.mvFileStorageRepository = pFileStorageRepository;
        this.mvProductGenerateQRCodeService = pProductGenerateQRCodeService;
        this.mvProductHistoryService = pProductHistoryService;
        this.mvTicketImportService = pTicketImportService;
        this.mvTicketExportService = pTicketExportService;
        this.mvCategoryService = pCategoryService;
        this.mvCategoryRepository = pCategoryRepository;
        this.mvStorageService = pStorageService;
        this.mvStorageRepository = pStorageRepository;
        this.mvCartRepository = pCartRepository;
        this.mvGenerateBarcodeService = pGenerateBarcodeService;
        this.mvProductPriceService = pProductPriceService;
        this.mvUserSession = pUserSession;
        this.mvSystemLogService = pSystemLogService;
        this.mvProductRepository = pProductRepository;
    }

    @Override
    public List<ProductVariantDTO> findAll() {
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

        lvProductVariantDTOs.stream()
                .peek(dto -> {
                    //assignPriceInfo(dto, mvProductPriceService.findPresentPrice(dto.getId()));
                    mvProductPriceService.assignPriceInfo(dto, mvProductPriceRepository.findPresentPrices(dto.getId()));
                    String lvImageUrl = FileUtils.getImageUrl(lvImageActiveList.get(dto.getId()), true);
                    dto.setImageSrc(lvImageUrl != null ? lvImageUrl : EndPoint.URL_MEDIA_DEFAULT_PRODUCT.getValue());
                    dto.setCurrentInCart(lvCartItemIds.contains(dto.getId()));
                })
                .collect(Collectors.toList());

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
        List<OrderCart> cartList = mvCartRepository.findByAccountId(mvUserSession.getUserPrincipal().getId());
        if (ObjectUtils.isNotEmpty(cartList)) {
            return cartList.get(0);
        }
        return null;
    }

    @Override
    public ProductVariantDTO findById(Long pProductVariantId, boolean pThrowException) {
        ProductDetail lvEntity = super.findEntById(pProductVariantId, pThrowException);
        ProductVariantDTO lvDto = super.convertDTO(lvEntity);
        //productVariant = ProductVariantConvert.toDto(productVariant.get());

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

    @Transactional
    @Override
    public ProductVariantDTO save(ProductVariantDTO pDto) {
        try {
            Product lvProduct = mvProductRepository.findById(pDto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product invalid!"));
            Category lvColor = mvCategoryRepository.findById(pDto.getColorId())
                    .orElseThrow(() -> new BadRequestException("Color invalid!"));
            Category lvSize = mvCategoryRepository.findById(pDto.getSizeId())
                    .orElseThrow(() -> new BadRequestException("Size invalid!"));
            Category lvFabricType = mvCategoryRepository.findById(pDto.getFabricTypeId())
                    .orElseThrow(() -> new BadRequestException("Fabric type invalid!"));

            if (variantExisted(lvProduct.getId(), lvColor.getId(), lvSize.getId(), lvFabricType.getId())) {
                throw new DataExistsException("This product variant already exists!");
            }

            ProductDetail lvProductVariantSaved = mvEntityRepository.save(ProductDetail.builder()
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
                    .build());

            mvProductPriceService.saveVariantPrice(lvProductVariantSaved, pDto.getPrice());

            try {
                mvProductGenerateQRCodeService.generateProductVariantQRCode(lvProductVariantSaved.getId());
            } catch (IOException | WriterException e ) {
                e.printStackTrace();
                LOG.error(String.format("Can't generate QR Code for Product %s", lvProductVariantSaved.getVariantCode()), e);
            }

            try {
                mvGenerateBarcodeService.generateBarcode(lvProductVariantSaved.getId());
            } catch (IOException | WriterException e ) {
                e.printStackTrace();
                LOG.error(String.format("Can't generate Barcode for Product %s", lvProductVariantSaved.getVariantCode()), e);
            }

            if (lvProductVariantSaved.getStorageQty() > 0 && pDto.getStorageIdInitStorageQty() != null) {
                Storage lvStorage = mvStorageRepository.findById(pDto.getStorageIdInitStorageQty())
                        .orElseThrow(() -> new EntityNotFoundException(new Object[] {"storage"}, null, null));
                String initMessage = "Initialize storage quantity when create new products";

                TicketImport ticketImportSaved = mvTicketImportService.save(TicketImport.builder()
                        .title("Initialize storage")
                        .importer(mvUserSession.getUserPrincipal().getUsername())
                        .importTime(LocalDateTime.now())
                        .note(initMessage)
                        .status(TicketImportStatus.COMPLETED.name())
                        .storage(lvStorage)
                        .build());

                mvProductVariantTempRepository.save(ProductVariantExim.builder()
                        .ticketImport(ticketImportSaved)
                        .productVariant(lvProductVariantSaved)
                        .quantity(lvProductVariantSaved.getStorageQty())
                        .note(initMessage)
                        .build());
            }

            if (lvProductVariantSaved.getSoldQty() > 0 && pDto.getStorageIdInitStorageQty() != null) {
                Storage lvStorage = mvStorageRepository.findById(pDto.getStorageIdInitStorageQty())
                        .orElseThrow(() -> new EntityNotFoundException(new Object[] {"storage"}, null, null));
                String initMessage = "Initialize storage quantity when create new products";

                TicketExportDTO lvTicketExport = new TicketExportDTO();
                lvTicketExport.setTitle("Initialize storage");
                lvTicketExport.setExporter(mvUserSession.getUserPrincipal().getUsername());
                lvTicketExport.setExportTime(LocalDateTime.now());
                lvTicketExport.setNote(initMessage);
                lvTicketExport.setStatus(TicketExportStatus.COMPLETED.name());
                lvTicketExport.setStorage(new StorageDTO(lvStorage.getId()));
                TicketExportDTO ticketExportSaved = mvTicketExportService.save(lvTicketExport);

                mvProductVariantTempRepository.save(ProductVariantExim.builder()
                        .ticketExport(new TicketExport(ticketExportSaved.getId()))
                        .productVariant(lvProductVariantSaved)
                        .quantity(lvProductVariantSaved.getStorageQty())
                        .note(initMessage)
                        .build());
            }

            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, "Thêm mới biến thể sản phẩm", lvProductVariantSaved.toStringInsert());
            LOG.info("Insert productVariant success! {}", lvProductVariantSaved);
            return ProductVariantConvert.toDto(lvProductVariantSaved);
        } catch (RuntimeException ex) {
            throw new AppException(ex.getMessage(), ex);
        }
    }

    @Transactional
    @Override
    public ProductVariantDTO update(ProductVariantDTO pProductDetail, Long productVariantId) {
        ProductDetail lvVariant = super.findEntById(productVariantId, true);
        try {
            ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvVariant));

            lvVariant.setVariantName(pProductDetail.getVariantName());
            lvVariant.setDefectiveQty(pProductDetail.getDefectiveQty());
            lvVariant.setWeight(pProductDetail.getWeight());
            lvVariant.setNote(pProductDetail.getNote());
            ProductDetail productVariantUpdated = mvEntityRepository.save(lvVariant);

            changeLog.setNewObject(productVariantUpdated);
            changeLog.doAudit();

            ProductPrice lvCurrentPrice = mvProductPriceRepository.findPricePresent(productVariantUpdated.getId());
            ProductPriceDTO lvRequestPrice = pProductDetail.getPrice();
            if (isPriceChanged(lvCurrentPrice, lvRequestPrice)) {
                lvCurrentPrice.setState(ProductPrice.STATE_INACTIVE);
                mvProductPriceRepository.save(lvCurrentPrice);
                mvProductPriceService.saveVariantPrice(productVariantUpdated, lvRequestPrice);
            }

            //Log
            String logTitle = "Cập nhật thông tin sản phẩm: " + productVariantUpdated.getVariantName();
            mvProductHistoryService.save(changeLog.getLogChanges(), logTitle, productVariantUpdated.getProduct().getId(), productVariantUpdated.getId(), null);
            mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, logTitle, changeLog.getOldValues(), changeLog.getNewValues());
            LOG.info("Update productVariant success! {}", productVariantUpdated);

            return ProductVariantConvert.toDto(productVariantUpdated);
        } catch (Exception e) {
            throw new AppException("Update productVariant fail! " + pProductDetail.toString(), e);
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
    public boolean variantExisted(long productId, long colorId, long sizeId, long fabricTypeId) {
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
        ProductDetail lvProduct = mvEntityRepository.findById(pProductId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        lvProduct.setLowStockThreshold(pThreshold);
        mvEntityRepository.save(lvProduct);
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
            return CommonUtils.now("yyyyMMddHHmmss");
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

    private boolean isPriceChanged(ProductPrice pCPrice, ProductPriceDTO pRPrice) {
        //Current price
        BigDecimal lvCRetailPrice = pCPrice.getRetailPrice();
        BigDecimal lvCRetailPriceDiscount = pCPrice.getRetailPriceDiscount();
        BigDecimal lvCWholesalePrice = pCPrice.getWholesalePrice();
        BigDecimal lvCWholesalePriceDiscount = pCPrice.getWholesalePriceDiscount();
        BigDecimal lvCPurchasePrice = pCPrice.getPurchasePrice();
        BigDecimal lvCCostPrice = pCPrice.getCostPrice();

        //Request price
        BigDecimal lvRRetailPrice = CoreUtils.coalesce(pRPrice.getRetailPrice());
        BigDecimal lvRRetailPriceDiscount = CoreUtils.coalesce(pRPrice.getRetailPriceDiscount(), pRPrice.getRetailPrice());
        BigDecimal lvRWholesalePrice = CoreUtils.coalesce(pRPrice.getWholesalePrice());
        BigDecimal lvRWholesalePriceDiscount = CoreUtils.coalesce(pRPrice.getWholesalePriceDiscount(), pRPrice.getWholesalePrice());
        BigDecimal lvRPurchasePrice = CoreUtils.coalesce(pRPrice.getPurchasePrice());
        BigDecimal lvRCostPrice = CoreUtils.coalesce(pRPrice.getCostPrice());

        boolean isChanged = false;
        if (lvCRetailPrice.compareTo(lvRRetailPrice) != 0) {
            isChanged = true;
        } else if (lvCRetailPriceDiscount.compareTo(lvRRetailPriceDiscount) != 0) {
            isChanged = true;
        } else if (lvCWholesalePrice.compareTo(lvRWholesalePrice) != 0) {
            isChanged = true;
        } else if (lvCWholesalePriceDiscount.compareTo(lvRWholesalePriceDiscount) != 0) {
            isChanged = true;
        } else if (lvCPurchasePrice.compareTo(lvRPurchasePrice) != 0) {
            isChanged = true;
        } else if (lvCCostPrice.compareTo(lvRCostPrice) != 0) {
            isChanged = true;
        }
        return isChanged;
    }

    private String generateSKUCode() {
        //Do something pattern
        return UUID.randomUUID().toString();
    }
}