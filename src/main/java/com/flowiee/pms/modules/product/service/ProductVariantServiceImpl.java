package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.common.constants.JpaHints;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.log.service.SystemLogService;
import com.flowiee.pms.modules.product.dto.ProductDTO;
import com.flowiee.pms.modules.product.entity.Product;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import com.flowiee.pms.modules.product.entity.ProductPrice;
import com.flowiee.pms.modules.product.entity.ProductVariantExim;
import com.flowiee.pms.modules.product.repository.ProductRepository;
import com.flowiee.pms.modules.sales.entity.Items;
import com.flowiee.pms.modules.sales.entity.OrderCart;
import com.flowiee.pms.modules.inventory.entity.TicketExport;
import com.flowiee.pms.modules.inventory.entity.TicketImport;
import com.flowiee.pms.modules.inventory.entity.Storage;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.product.model.ProductVariantParameter;
import com.flowiee.pms.modules.product.dto.ProductPriceDTO;
import com.flowiee.pms.modules.category.repository.CategoryRepository;
import com.flowiee.pms.modules.product.repository.ProductPriceRepository;
import com.flowiee.pms.modules.sales.repository.OrderCartRepository;
import com.flowiee.pms.modules.inventory.repository.StorageRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.category.service.CategoryService;
import com.flowiee.pms.common.base.service.GenerateBarcodeService;
import com.flowiee.pms.modules.sales.service.CartService;
import com.flowiee.pms.modules.inventory.service.StorageService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.modules.product.dto.ProductVariantDTO;
import com.flowiee.pms.modules.product.dto.ProductVariantTempDTO;
import com.flowiee.pms.modules.product.repository.ProductDetailRepository;
import com.flowiee.pms.modules.product.repository.ProductDetailTempRepository;
import com.flowiee.pms.modules.inventory.service.TicketExportService;
import com.flowiee.pms.modules.inventory.service.TicketImportService;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.converter.ProductVariantConvert;
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
public class ProductVariantServiceImpl extends BaseGService<ProductDetail, ProductVariantDTO, ProductDetailRepository> implements ProductVariantService {
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
        return this.findAll(-1, -1, null, null, null, null, null, null, null, null, false).getContent();
    }

    @Override
    public Page<ProductVariantDTO> findAll(ProductVariantParameter pParameter) {
        return this.findAll(pParameter.getPageSize(), pParameter.getPageNum(), pParameter.getTxtSerch(), pParameter.getProductId(),
                pParameter.getTicketImportId(), pParameter.getBrandId(), pParameter.getColorId(), pParameter.getSizeId(),
                pParameter.getProductId(), pParameter.getAvailableForSales(), pParameter.getCheckInAnyCart());
    }

    @Override
    public Page<ProductVariantDTO> findAll(int pageSize, int pageNum, String pTxtSearch, Long pProductId, Long pTicketImport, Long pBrandId, Long pColorId, Long pSizeId, Long pFabricTypeId, Boolean pAvailableForSales, boolean checkInAnyCart) {
        Pageable lvPageable = getPageable(pageNum, pageSize, Sort.by("variantName").ascending());

        QueryBuilder<ProductDetail> lvQueryBuilder = createQueryBuilder(ProductDetail.class)
                .addLike("variantCode", pTxtSearch)
                .addLike("variantName", pTxtSearch)
                .addEqual("product.id", pProductId)
                .addEqual("product.brand.id", pBrandId)
                .addEqual("color.id", pColorId)
                .addEqual("size.id", pSizeId)
                .addEqual("fabricType.id", pFabricTypeId);
        if (pAvailableForSales != null) {
            lvQueryBuilder.addPredicate(cb -> {
                //storageQty - defectiveQty
                Expression<Integer> availableQty = cb.diff(
                        lvQueryBuilder.getRoot().get("storageQty"),
                        lvQueryBuilder.getRoot().get("defectiveQty")
                );
                return Boolean.TRUE.equals(pAvailableForSales)
                        ? cb.greaterThan(availableQty, 0)
                        : cb.lessThan(availableQty, 1);
            });
        }

        EntityGraph<ProductDetail> lvEntityGraph = mvEntityManager.createEntityGraph(ProductDetail.class);
        lvEntityGraph.addSubgraph("product").addAttributeNodes("productType", "unit", "brand");
        lvEntityGraph.addSubgraph("color");
        lvEntityGraph.addSubgraph("size");
        lvEntityGraph.addSubgraph("fabricType");
        lvEntityGraph.addSubgraph("priceList");

        List<ProductDetail> lvProductVariants = lvQueryBuilder.build(lvPageable)
                .setHint(JpaHints.FETCH_GRAPH, lvEntityGraph)
                .getResultList();
        long lvTotalRecords = lvQueryBuilder.buildCount();

        //List<ProductVariantDTO> lvProductVariantDTOs = ProductVariantConvert.entitiesToDTOs(lvProductVariants);
        List<ProductVariantDTO> lvProductVariantDTOs = convertDTOs(lvProductVariants);
        List<Long> lvProductVariantIds = lvProductVariants.stream().map(ProductDetail::getId).toList();

        Map<Long, FileStorage> lvImageActiveList = mvProductImageService.getImageActiveOfProductVariants(lvProductVariantIds);

        OrderCart currentCart = checkInAnyCart ? getCurrentCart() : null;
        List<Items> lvCartItems = currentCart != null ? mvCartService.getItems(currentCart.getId(), lvProductVariantIds) : List.of();
        List<Long> lvCartItemIds = lvCartItems.isEmpty() ? List.of() : lvCartItems.stream().map(a -> a.getProductDetail().getId()).toList();

        lvProductVariantDTOs.stream()
                .peek(dto -> {
                    assignPriceInfo(dto, mvProductPriceService.findPresentPrice(dto.getId()));
                    String lvImageUrl = FileUtils.getImageUrl(lvImageActiveList.get(dto.getId()), true);
                    dto.setImageSrc(lvImageUrl != null ? lvImageUrl : EndPoint.URL_MEDIA_DEFAULT_PRODUCT.getValue());
                    dto.setCurrentInCart(lvCartItemIds.contains(dto.getId()));
                })
                .collect(Collectors.toList());

        return new PageImpl<>(lvProductVariantDTOs, lvPageable, lvTotalRecords);
    }

    private ProductVariantDTO assignPriceInfo(ProductVariantDTO dto, ProductPriceDTO productPrice) {
        if (dto != null) {
            if (productPrice != null) {
                dto.setPrice(ProductPriceDTO.builder()
                        .retailPrice(productPrice.getRetailPrice())
                        .retailPriceDiscount(productPrice.getRetailPriceDiscount())
                        .wholesalePrice(productPrice.getWholesalePrice())
                        .wholesalePriceDiscount(productPrice.getWholesalePriceDiscount())
                        .purchasePrice(productPrice.getPurchasePrice())
                        .costPrice(productPrice.getCostPrice())
                        .lastUpdatedAt(productPrice.getLastUpdatedAt())
                        .build());
            } else {
                dto.setPrice(new ProductPriceDTO());
            }
        }
        return dto;
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
        Optional<ProductDetail> productVariant = mvEntityRepository.findById(pProductVariantId);
        if (productVariant.isPresent()) {
            ProductVariantDTO dto = ProductVariantConvert.toDto(productVariant.get());
            //ProductPrice productPrice = mvProductPriceRepository.findPricePresent(null, dto.getId());
            ProductPrice lvProductPrice = new ProductPrice();
            if (productVariant.get().getPriceList() != null) {
                for (ProductPrice price : productVariant.get().getPriceList()) {
                    if (ProductPrice.STATE_ACTIVE.equals(price.getState()))
                        lvProductPrice = price;
                }
            }
            assignPriceInfo(dto, ProductPriceDTO.toDTO(lvProductPrice));
            return dto;
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"product variant"}, null, null);
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public ProductVariantDTO save(ProductVariantDTO pDto) {
        try {
            Product lvProduct = mvProductRepository.findById(pDto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product invalid!"));

            VldModel vldModel = vldCategory(pDto.getColorId(), pDto.getSizeId(), pDto.getFabricTypeId());

            ProductDetail lvEntity = ProductDetail.builder()
                    .product(new Product(lvProduct.getId()))
                    .color(vldModel.getColor())
                    .size(vldModel.getSize())
                    .fabricType(vldModel.getFabricType())
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
            ProductDetail lvProductVariantSaved = mvEntityRepository.save(lvEntity);

            ProductPriceDTO priceDTO = pDto.getPrice();
            savePrice(lvProductVariantSaved, priceDTO);

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

                TicketExport ticketExportSaved = mvTicketExportService.save(TicketExport.builder()
                        .title("Initialize storage")
                        .exporter(mvUserSession.getUserPrincipal().getUsername())
                        .exportTime(LocalDateTime.now())
                        .note(initMessage)
                        .status(TicketExportStatus.COMPLETED.name())
                        .storage(lvStorage)
                        .build());

                mvProductVariantTempRepository.save(ProductVariantExim.builder()
                        .ticketExport(ticketExportSaved)
                        .productVariant(lvProductVariantSaved)
                        .quantity(lvProductVariantSaved.getStorageQty())
                        .note(initMessage)
                        .build());
            }

            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.ProductVariant, "Thêm mới biến thể sản phẩm", lvEntity.toStringInsert());
            LOG.info("Insert productVariant success! {}", lvEntity);
            return ProductVariantConvert.toDto(lvProductVariantSaved);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product variant"), ex);
        }
    }

    @Transactional
    @Override
    public ProductVariantDTO update(ProductVariantDTO pProductDetail, Long productVariantId) {
        ProductDetail productVariant = this.findById(productVariantId).orElseThrow(() -> new BadRequestException());
        //VldModel vldModel = vldCategory(pProductDetail.getColorId(), pProductDetail.getSizeId(), pProductDetail.getFabricTypeId());
        try {
            ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(productVariant));

            ProductDetail productToUpdate = productVariant;
            productToUpdate.setVariantName(pProductDetail.getVariantName());
            productToUpdate.setDefectiveQty(pProductDetail.getDefectiveQty());
            productToUpdate.setWeight(pProductDetail.getWeight());
            productToUpdate.setNote(pProductDetail.getNote());
            ProductDetail productVariantUpdated = mvEntityRepository.save(productToUpdate);

            changeLog.setNewObject(productVariantUpdated);
            changeLog.doAudit();

            //Update state of current Price to inactive
            ProductPrice productVariantPricePresent = mvProductPriceRepository.findPricePresent(null, productVariantUpdated.getId());
            if (productVariantPricePresent != null) {
                productVariantPricePresent.setState(ProductPrice.STATE_INACTIVE);
                mvProductPriceRepository.save(productVariantPricePresent);
            }
            ProductPriceDTO price = pProductDetail.getPrice();
            savePrice(productVariantUpdated, price);

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
        ProductVariantDTO productDetailToDelete = this.findById(productVariantId, true);
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
    public boolean isProductVariantExists(long productId, long colorId, long sizeId, long fabricTypeId) {
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
            assignPriceInfo(dto, mvProductPriceService.findPresentPrice(dto.getId()));
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

    private VldModel vldCategory(Long pColorId, Long pSizeId, Long pFabricTypeId) {
        Category lvColor = mvCategoryRepository.findById(pColorId).get();
        if (lvColor == null)
            throw new BadRequestException("Color invalid!");

        Category lvSize = mvCategoryRepository.findById(pSizeId).get();
        if (lvSize == null)
            throw new BadRequestException("Size invalid!");

        Category lvFabricType = mvCategoryRepository.findById(pFabricTypeId).get();
        if (lvFabricType == null)
            throw new BadRequestException("Fabric type invalid!");

        VldModel vldModel = new VldModel();
        vldModel.setColor(lvColor);
        vldModel.setSize(lvSize);
        vldModel.setFabricType(lvFabricType);

        return vldModel;
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

    private void savePrice(ProductDetail productVariant, ProductPriceDTO pPriceDTO) {
        BigDecimal lvRetailPrice = CoreUtils.coalesce(pPriceDTO.getRetailPrice());
        BigDecimal lvRetailPriceDiscount = CoreUtils.coalesce(pPriceDTO.getRetailPriceDiscount(), pPriceDTO.getRetailPrice());
        BigDecimal lvWholesalePrice = CoreUtils.coalesce(pPriceDTO.getWholesalePrice());
        BigDecimal lvWholesalePriceDiscount = CoreUtils.coalesce(pPriceDTO.getWholesalePriceDiscount(), pPriceDTO.getWholesalePrice());
        BigDecimal lvPurchasePrice = CoreUtils.coalesce(pPriceDTO.getPurchasePrice());
        BigDecimal lvCostPrice = CoreUtils.coalesce(pPriceDTO.getCostPrice());

        vldPrice(lvRetailPrice, lvRetailPriceDiscount, lvWholesalePrice, lvWholesalePriceDiscount, lvPurchasePrice, lvCostPrice);

        mvProductPriceRepository.save(ProductPrice.builder()
                .productVariant(productVariant)
                .retailPrice(lvRetailPrice)
                .retailPriceDiscount(lvRetailPriceDiscount)
                .wholesalePrice(lvWholesalePrice)
                .wholesalePriceDiscount(lvWholesalePriceDiscount)
                .purchasePrice(lvPurchasePrice)
                .costPrice(lvCostPrice)
                .state(ProductPrice.STATE_ACTIVE)
                .build());
    }

    private String generateSKUCode() {
        //Do something pattern
        return UUID.randomUUID().toString();
    }
}