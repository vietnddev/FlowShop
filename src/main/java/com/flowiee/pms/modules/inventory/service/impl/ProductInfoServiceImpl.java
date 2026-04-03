package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.common.constants.JpaHints;
import com.flowiee.pms.common.model.BaseParameter;
import com.flowiee.pms.modules.inventory.dto.ProductAttributeDTO;
import com.flowiee.pms.modules.inventory.dto.ProductPriceDTO;
import com.flowiee.pms.modules.inventory.dto.ProductVariantDTO;
import com.flowiee.pms.modules.inventory.model.ProductSearchRequest;
import com.flowiee.pms.modules.inventory.model.ProductSummaryModel;
import com.flowiee.pms.modules.inventory.repository.ProductPriceRepository;
import com.flowiee.pms.modules.inventory.service.*;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import com.flowiee.pms.modules.system.service.SystemLogService;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductDescription;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.inventory.model.ProductVariantSearchRequest;
import com.flowiee.pms.modules.inventory.model.ProductSummaryInfoModel;
import com.flowiee.pms.modules.inventory.repository.ProductDescriptionRepository;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.modules.system.service.CategoryService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import com.flowiee.pms.modules.inventory.repository.ProductRepository;
import com.flowiee.pms.modules.inventory.util.ProductConvert;
import javax.persistence.EntityGraph;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductInfoServiceImpl extends BaseService<Product, ProductDTO, ProductRepository> implements ProductInfoService {
    private final ProductDescriptionRepository mvProductDescriptionRepository;
    private final ProductAttributeService mvProductAttributeService;
    private final ProductDetailRepository mvProductDetailRepository;
    private final ProductPriceRepository mvProductPriceRepository;
    private final FileStorageRepository mvFileStorageRepository;
    private final ProductVariantService mvProductVariantService;
    private final ProductHistoryService mvProductHistoryService;
    private final ProductPriceService mvProductPriceService;
    private final SystemLogService mvSystemLogService;
    private final OrderRepository mvOrderRepository;
    private final CategoryService mvCategoryService;

    public ProductInfoServiceImpl(ProductRepository pProductRepository, ProductDescriptionRepository pProductDescriptionRepository, ProductAttributeService pProductAttributeService, ProductDetailRepository pProductDetailRepository, ProductPriceRepository pProductPriceRepository, @Lazy ProductVariantService pProductVariantService, ProductHistoryService pProductHistoryService, FileStorageRepository pFileStorageRepository, ProductPriceService pProductPriceService, SystemLogService pSystemLogService, OrderRepository pOrderRepository, CategoryService pCategoryService) {
        super(Product.class, ProductDTO.class, pProductRepository);
        this.mvProductDescriptionRepository = pProductDescriptionRepository;
        this.mvProductAttributeService = pProductAttributeService;
        this.mvProductDetailRepository = pProductDetailRepository;
        this.mvProductPriceRepository = pProductPriceRepository;
        this.mvProductVariantService = pProductVariantService;
        this.mvProductHistoryService = pProductHistoryService;
        this.mvFileStorageRepository = pFileStorageRepository;
        this.mvProductPriceService = pProductPriceService;
        this.mvSystemLogService = pSystemLogService;
        this.mvOrderRepository = pOrderRepository;
        this.mvCategoryService = pCategoryService;
    }

    @Override
    public List<ProductDTO>find(BaseParameter pParam) {
        return this.findAll(ProductSearchRequest.builder().build(), false).getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductDTO> findAll(ProductSearchRequest pRequest, boolean pFullInformation) {
        Pageable lvPageable = getPageable(pRequest.getPageNum(), pRequest.getPageSize(), Sort.by("createdAt").descending());

        QueryBuilder<Product> lvQueryBuilder = buildSearchQuery(pRequest);
        EntityGraph<Product> lvEntityGraph = buildEntityGraph();

        List<Product> lvResultList = executeQuery(lvQueryBuilder, lvEntityGraph, lvPageable);
        Long lvTotalRecords = lvQueryBuilder.buildCount();

        //Convert to DTO
        List<ProductDTO> lvResultListDto = super.convertDTOs(lvResultList);

        if (pFullInformation) {// Default is true for list of products page
            setImages(lvResultListDto);
            //setVariants(lvResultListDto);
        }

        if (!lvResultListDto.isEmpty()) {
            List<Long> lvProductIds = lvResultListDto.stream()
                    .map(ProductDTO::getId)
                    .toList();
            Map<Long, ProductSummaryModel> lvProductSummariesMap = loadProductSummariesBatch(lvProductIds);

            for (ProductDTO lvDto : lvResultListDto) {
                ProductSummaryModel prdSummaryMdl = lvProductSummariesMap.get(lvDto.getId());
                if (prdSummaryMdl != null) {
                    lvDto.setStockQty(prdSummaryMdl.getStockQty());
                    lvDto.setSoldQty(prdSummaryMdl.getSoldQty());
                    lvDto.setSoldQty(prdSummaryMdl.getSoldQty());
                    lvDto.setDefectiveQty(prdSummaryMdl.getDefectiveQty());
                    lvDto.setReservedQty(prdSummaryMdl.getReservedQty());
                    lvDto.setAvailableQty(lvDto.getStockQty() - lvDto.getReservedQty() - lvDto.getDefectiveQty());
                    if (prdSummaryMdl.isActive()) {
                        lvDto.setStatusCode(ProductStatus.ACT.name());
                        lvDto.setStatusName(ProductStatus.ACT.getLabel());
                    }
                }
            }
        }

        return new PageImpl<>(lvResultListDto, lvPageable, lvTotalRecords);
    }

    private QueryBuilder<Product> buildSearchQuery(ProductSearchRequest pRequest) {
        return createQueryBuilder(Product.class)
                .addLike("productName", pRequest.getTxtSearch())
                .addEqual("brand.id", pRequest.getBrandId())
                .addEqual("productType.id", pRequest.getProductTypeId())
                .addOrder("createdAt", false);
    }

    private EntityGraph<Product> buildEntityGraph() {
        EntityGraph<Product> lvEntityGraph = mvEntityManager.createEntityGraph(Product.class);
        lvEntityGraph.addSubgraph("brand");
        lvEntityGraph.addSubgraph("productType");
        lvEntityGraph.addSubgraph("unit");
        //lvEntityGraph.addSubgraph("productVariantList");
        return lvEntityGraph;
    }

    private List<Product> executeQuery(QueryBuilder<Product> pQueryBuilder, EntityGraph<Product> pEntityGraph, Pageable pPageable) {
        return pQueryBuilder.build(pPageable)
                .setHint(JpaHints.FETCH_GRAPH, pEntityGraph)
                .getResultList();
    }

    private Map<Long, ProductSummaryModel> loadProductSummariesBatch(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return Collections.emptyMap();
        }

        // Split batch để tránh query quá lớn
        List<ProductSummaryModel> allSummaries = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i += 500) {
            List<Long> batch = productIds.subList(i, Math.min(i + 500, productIds.size()));
            allSummaries.addAll(mvEntityRepository.getSummariesQty(batch));
        }

        return allSummaries.stream()
                .collect(Collectors.toMap(ProductSummaryModel::getProductId, s -> s));
    }

    private void setImages(List<ProductDTO> pProducts) {
        if (CollectionUtils.isEmpty(pProducts)) {
            return;
        }

        List<Long> lvProductIds = pProducts.stream().map(ProductDTO::getId).toList();

        List<FileStorage> lvImageList = new ArrayList<>();
        int lvBatchSizeImage = 200; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < lvProductIds.size(); i += lvBatchSizeImage) {
            List<Long> batch = new ArrayList<>(lvProductIds.subList(i, Math.min(i + lvBatchSizeImage, lvProductIds.size())));
            lvImageList.addAll(mvFileStorageRepository.findProductImageActive(batch));
        }

        //Map<productId, imageUrl>
        Map<Long, String> imageMap = lvImageList.stream()
                .filter(img -> img.getProduct() != null)
                .collect(Collectors.toMap(
                        img -> img.getProduct().getId(),
                        img -> FileUtils.getImageUrl(img, true),
                        (existing, replacement) -> existing // Tránh lỗi nếu có trùng key
                ));

        pProducts.forEach(dto -> dto.setImageActive(imageMap.get(dto.getId())));
    }

    private void setVariants(List<ProductDTO> pProducts) {
        if (CollectionUtils.isEmpty(pProducts)) {
            return;
        }

        List<Long> lvProductIds = pProducts.stream().map(ProductDTO::getId).toList();

        List<ProductSummaryInfoModel> lvVariantInfoList = new ArrayList<>();
        int lvBatchSizeVariant = 1000;
        for (int i = 0; i < lvProductIds.size(); i += lvBatchSizeVariant) {
            List<Long> batch = new ArrayList<>(lvProductIds.subList(i, Math.min(i + lvBatchSizeVariant, lvProductIds.size())));
            lvVariantInfoList.addAll(mvProductDetailRepository.findProductVariantInfo(batch));
        }

        Map<Long, List<ProductSummaryInfoModel>> lvProductVariantMap = lvVariantInfoList.stream()
                .collect(Collectors.groupingBy(ProductSummaryInfoModel::getProductId));

        for (ProductDTO lvProduct : pProducts) {
            //List<ProductSummaryInfoModel> lvVariantInfoList = mvProductDetailRepository.findProductVariantInfo(product.getId());
            List<ProductSummaryInfoModel> lvVariants = lvProductVariantMap.getOrDefault(lvProduct.getId(), Collections.emptyList());

            int lvReservedQty = 0;
            int lvStorageQuantity = 0;
            int lvSoldQuantity = 0;
            ProductStatus lvProductStatus = ProductStatus.INA;
            List<ProductVariantDTO> lvVariantList = new ArrayList<>();
            List<ProductAttributeDTO> lvAttributeList = mvProductAttributeService.findAll(-1, -1, lvProduct.getId()).getContent();

            for (ProductSummaryInfoModel v : lvVariants) {
                lvStorageQuantity += v.getQuantity();
                lvSoldQuantity += v.getSoldQty();

                ProductVariantDTO lvVariantDto = new ProductVariantDTO();
                lvVariantDto.setId(v.getId());
                lvVariantDto.setVariantCode(v.getVariantCode());
                lvVariantDto.setVariantName(v.getVariantName());
                lvVariantDto.setFabricType(new CategoryDTO(v.getFabricTypeId(), v.getFabricTypeName()));
                lvVariantDto.setColor(new CategoryDTO(v.getColorId(), v.getColorName()));
                lvVariantDto.setSize(new CategoryDTO(v.getSizeId(), v.getSizeName()));
                lvVariantDto.setStorageQty((int) (long) v.getQuantity());
                lvVariantDto.setSoldQty((int) (long) v.getSoldQty());
                lvVariantDto.setStatus(v.getStatus());
                lvVariantDto.setReservedQty(getReservedQuantityByVariantId(v.getId()));
                lvReservedQty += lvVariantDto.getReservedQty();
                lvVariantDto.setAvailableQty(lvVariantDto.getStorageQty() -  lvVariantDto.getReservedQty() - lvVariantDto.getDefectiveQty());

                mvProductPriceService.assignPriceInfo(lvVariantDto, mvProductPriceRepository.findPresentPrices(lvVariantDto.getId()));
                if (ProductStatus.ACT.equals(lvVariantDto.getStatus())) {
                    lvProductStatus = ProductStatus.ACT;
                }

                lvVariantList.add(lvVariantDto);
            }

            lvProduct.setStockQty(lvStorageQuantity);
            lvProduct.setSoldQty(lvSoldQuantity);
            lvProduct.setReservedQty(lvReservedQty);
            lvProduct.setVariants(lvVariantList);
            lvProduct.setAttributes(lvAttributeList);
            lvProduct.setStatusCode(lvProductStatus.name());
            lvProduct.setStatusName(lvProductStatus.getLabel());
        }
    }

    @Override
    public List<Product> findProductsIdAndProductName() {
        List<Product> products = new ArrayList<>();
        for (Object[] objects : mvEntityRepository.findIdAndName()) {
            products.add(new Product(Integer.parseInt(String.valueOf(objects[0])), String.valueOf(objects[1])));
        }
        return products;
    }

    @Override
    public Product findEntById(Long entityId, boolean throwException) {
        return super.findEntById(entityId, throwException);
    }

    @Override
    public ProductDTO findById(Long id, boolean pThrowException) {
        ProductDTO lvDto = super.findDtoById(id, pThrowException);
        if (lvDto != null) {
            setImages(List.of(lvDto));
            setVariants(List.of(lvDto));
        }

        return lvDto;
    }

    @Transactional
    @Override
    public ProductDTO save(ProductDTO pProduct) {
        try {
            Map<CATEGORY, Category> lvCategoryMap = mvCategoryService.findByIdsAsMap(Set.of(
                    pProduct.getBrand().getId(), pProduct.getProductType().getId(), pProduct.getUnit().getId()));
            Category lvBrand = lvCategoryMap.get(CATEGORY.BRAND);
            Category lvProductType = lvCategoryMap.get(CATEGORY.PRODUCT_TYPE);
            Category lvUnit = lvCategoryMap.get(CATEGORY.UNIT);

            Product lvProduct = new Product();
            lvProduct.setProductName(pProduct.getProductName());
            lvProduct.setBrand(lvBrand);
            lvProduct.setProductType(lvProductType);
            lvProduct.setUnit(lvUnit);
            lvProduct.setProductCategory(pProduct.getProductCategory());
            lvProduct.setInternalNotes(pProduct.getInternalNotes());
            //productToSave.setStatus(ProductStatus.ACT);

            Product lvProductSaved = mvEntityRepository.save(lvProduct);

            //Create variants
            List<ProductVariantDTO> lvVariants = pProduct.getVariants();
            if (!CollectionUtils.isEmpty(lvVariants)) {
                for (ProductVariantDTO lvVariant : lvVariants) {
                    ProductPriceDTO lvPrice = lvVariant.getPrice();

                    lvVariant.setProductId(lvProductSaved.getId());
                    lvVariant.setColorId(lvVariant.getColor().getId());
                    lvVariant.setSizeId(lvVariant.getSize().getId());
                    lvVariant.setFabricTypeId(lvVariant.getFabricType().getId());
                    lvVariant.setVariantName(lvProductSaved.getProductName());
                    lvVariant.setDefectiveQty(0);
                    lvVariant.setNote("");
                    lvVariant.setPrice(ProductPriceDTO.builder()
                            .retailPrice(lvPrice.getRetailPrice())
                            .wholesalePrice(lvPrice.getWholesalePrice())
                            .costPrice(lvPrice.getCostPrice())
                            .build());
                    lvVariant.setStatus(ProductStatus.INA);

                    mvProductVariantService.save(lvVariant);
                }
            }

            //Create attributes
            List<ProductAttributeDTO> lvAttributes = pProduct.getAttributes();
            if (!CollectionUtils.isEmpty(lvAttributes)) {
                mvProductAttributeService.saveAll(lvProductSaved.getId(), lvAttributes);
            }

            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_C, MasterObject.Product, "Thêm mới sản phẩm", lvProductSaved.getProductName());
            log.info("Insert product success! {}", lvProductSaved);
            return ProductConvert.toDto(lvProductSaved);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Transactional
    @Override
    public ProductDTO update(ProductDTO pProductDTO, Long productId) {
        if (pProductDTO == null) {
            throw new BadRequestException("Product data cannot be null");
        }
        if (productId == null || productId <= 0) {
            throw new BadRequestException("Invalid product ID");
        }

        Long lvProductTypeId = pProductDTO.getProductType().getId();
        Long lvBrandId = pProductDTO.getBrand().getId();
        Long lvUnitId = pProductDTO.getUnit().getId();

        Category lvProductType = mvCategoryService.findEntById(lvProductTypeId, true);
        Category lvBrand = mvCategoryService.findEntById(lvBrandId, true);
        Category lvUnit = mvCategoryService.findEntById(lvUnitId, true);

        Product lvProduct = super.findEntById(productId, true);
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvProduct));

        lvProduct.setProductName(pProductDTO.getProductName());
        lvProduct.setProductType(lvProductType);
        lvProduct.setBrand(lvBrand);
        lvProduct.setUnit(lvUnit);
        //lvProduct.setStatus(productDTO.getStatus());
        Product productUpdated = mvEntityRepository.save(lvProduct);

        changeLog.setNewObject(productUpdated);
        changeLog.doAudit();

        String logTitle = "Cập nhật sản phẩm: " + productUpdated.getProductName();

        mvProductHistoryService.save(changeLog.getLogChanges(), logTitle, productUpdated.getId(), null, null);
        mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.Product, logTitle, changeLog);
        log.info("Update product success! productId={}", productId);

        return ProductConvert.toDto(productUpdated);
    }

    @Transactional
    @Override
    public String delete(Long id) {
        try {
            Product productToDelete = this.findEntById(id, true);
            if (productInUse(productToDelete.getId())) {
                throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
            }
            mvEntityRepository.deleteById(productToDelete.getId());
            mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_PRD_D, MasterObject.Product, "Xóa sản phẩm", productToDelete.getProductName());
            log.info("Delete product success! productId={}", id);
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (RuntimeException ex) {
            throw new AppException("Delete product fail! productId=" + id, ex);
        }
    }

    private boolean productInUse(Long productId) throws RuntimeException {//Update later
        return !mvProductVariantService.findAll(ProductVariantSearchRequest.builder()
                .productId(productId)
                .checkInAnyCart(false)
                .build()
        ).getContent().isEmpty();
    }

    private int getReservedQuantityByVariantId(Long productVariantId) {
        return mvOrderRepository.getReservedQtyByVariantId(productVariantId, List.of(OrderStatus.PROCESSING));
    }

    @Override
    public ProductDescription findDescription(Long pProductId) {
        return mvProductDescriptionRepository.findByProductId(pProductId);
    }

    @Override
    public String updateDescription(Long pProductId, String pDescription) {
        Product lvProduct = super.findEntById(pProductId, true);

        ProductDescription productDescription = findDescription(lvProduct.getId());
        if (productDescription != null) {
            productDescription.setDescription(pDescription);
            return mvProductDescriptionRepository.save(productDescription).getDescription();
        }

        return mvProductDescriptionRepository.save(ProductDescription.builder()
                .productId(lvProduct.getId())
                .description(pDescription).build()).getDescription();
    }
}