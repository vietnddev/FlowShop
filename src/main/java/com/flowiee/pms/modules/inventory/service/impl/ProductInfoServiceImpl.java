package com.flowiee.pms.modules.inventory.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.constants.JpaHints;
import com.flowiee.pms.modules.inventory.model.ProductSearchRequest;
import com.flowiee.pms.modules.inventory.service.ProductHistoryService;
import com.flowiee.pms.modules.inventory.service.ProductInfoService;
import com.flowiee.pms.modules.inventory.service.ProductVariantService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import com.flowiee.pms.modules.inventory.entity.Product;
import com.flowiee.pms.modules.inventory.entity.ProductDescription;
import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.inventory.model.ProductHeld;
import com.flowiee.pms.modules.inventory.model.ProductVariantSearchRequest;
import com.flowiee.pms.modules.inventory.model.ProductSummaryInfoModel;
import com.flowiee.pms.modules.inventory.repository.ProductDescriptionRepository;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.modules.system.service.CategoryService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.inventory.dto.ProductDTO;
import com.flowiee.pms.modules.inventory.repository.ProductRepository;
import com.flowiee.pms.modules.inventory.util.ProductConvert;
import jakarta.persistence.EntityGraph;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductInfoServiceImpl extends BaseService<Product, ProductDTO, ProductRepository> implements ProductInfoService {
    private final ProductDescriptionRepository mvProductDescriptionRepository;
    private final ProductVariantService mvProductVariantService;
    private final ProductHistoryService mvProductHistoryService;
    private final OrderRepository mvOrderRepository;
    private final CategoryService mvCategoryService;
    private final FileStorageRepository mvFileStorageRepository;
    private final ProductDetailRepository mvProductDetailRepository;
    private final SystemLogService mvSystemLogService;

    private final Logger mvLogger = LoggerFactory.getLogger(getClass());

    public ProductInfoServiceImpl(ProductRepository pProductRepository, ProductDescriptionRepository pProductDescriptionRepository,
                                  ProductVariantService pProductVariantService, ProductHistoryService pProductHistoryService,
                                  OrderRepository pOrderRepository, CategoryService pCategoryService,
                                  FileStorageRepository pFileStorageRepository, ProductDetailRepository pProductDetailRepository,
                                  SystemLogService pSystemLogService) {
        super(Product.class, ProductDTO.class, pProductRepository);
        this.mvProductDescriptionRepository = pProductDescriptionRepository;
        this.mvProductVariantService = pProductVariantService;
        this.mvProductHistoryService = pProductHistoryService;
        this.mvOrderRepository = pOrderRepository;
        this.mvCategoryService = pCategoryService;
        this.mvFileStorageRepository = pFileStorageRepository;
        this.mvProductDetailRepository = pProductDetailRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<ProductDTO> findAll() {
        return this.findAll(ProductSearchRequest.builder().build()).getContent();
    }

    @Override
    public Page<ProductDTO> findAll(ProductSearchRequest pRequest) {
        Pageable lvPageable = getPageable(pRequest.getPageNum(), pRequest.getPageSize(), Sort.by("createdAt").descending());

        QueryBuilder<Product> lvQueryBuilder = buildSearchQuery(pRequest);
        EntityGraph<Product> lvEntityGraph = buildEntityGraph();

        List<Product> lvResultList = executeQuery(lvQueryBuilder, lvEntityGraph, lvPageable);
        List<ProductDTO> lvResultListDto = super.convertDTOs(lvResultList);
        Long lvTotalRecords = lvQueryBuilder.buildCount();

        assignActiveImages(lvResultListDto);
        assignSummaryVariantInfo(lvResultListDto);

        return new PageImpl<>(lvResultListDto, lvPageable, lvTotalRecords);
    }

    private QueryBuilder<Product> buildSearchQuery(ProductSearchRequest pRequest) {
        QueryBuilder<Product> lvQueryBuilder = createQueryBuilder(Product.class)
                .addLike("productName", pRequest.getTxtSearch())
                .addEqual("brand.id", pRequest.getBrandId())
                .addEqual("productType.id", pRequest.getProductTypeId())
                .addOrder("createdAt", false);
        return lvQueryBuilder;
    }

    private EntityGraph<Product> buildEntityGraph() {
        EntityGraph<Product> lvEntityGraph = mvEntityManager.createEntityGraph(Product.class);
        lvEntityGraph.addSubgraph("brand");
        lvEntityGraph.addSubgraph("productType");
        lvEntityGraph.addSubgraph("unit");
        lvEntityGraph.addSubgraph("productVariantList");
        return lvEntityGraph;
    }

    private List<Product> executeQuery(QueryBuilder<Product> pQueryBuilder, EntityGraph<Product> pEntityGraph, Pageable pPageable) {
        return pQueryBuilder.build(pPageable)
                .setHint(JpaHints.FETCH_GRAPH, pEntityGraph)
                .getResultList();
    }

    private void assignActiveImages(List<ProductDTO> pProductDTOs) {
        List<Long> lvProductIds = pProductDTOs.stream().map(ProductDTO::getId).toList();

        List<FileStorage> lvImageList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < lvProductIds.size(); i += batchSize) {
            List<Long> batch = new ArrayList<>(lvProductIds.subList(i, Math.min(i + batchSize, lvProductIds.size())));
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

        pProductDTOs.forEach(dto -> dto.setImageActive(imageMap.get(dto.getId())));
    }

    private void assignSummaryVariantInfo(List<ProductDTO> pProducts) {
        if (CollectionUtils.isEmpty(pProducts)) {
            return;
        }

        List<Long> lvProductIds = pProducts.stream().map(ProductDTO::getId).toList();

        List<ProductSummaryInfoModel> lvVariantInfoList = new ArrayList<>();
        int batchSize = 1000;
        for (int i = 0; i < lvProductIds.size(); i += batchSize) {
            List<Long> batch = new ArrayList<>(lvProductIds.subList(i, Math.min(i + batchSize, lvProductIds.size())));
            lvVariantInfoList.addAll(mvProductDetailRepository.findProductVariantInfo(batch));
        }

        Map<Long, List<ProductSummaryInfoModel>> lvProductVariantMap = lvVariantInfoList.stream()
                .collect(Collectors.groupingBy(ProductSummaryInfoModel::getProductId));

        for (ProductDTO product : pProducts) {
            //List<ProductSummaryInfoModel> lvVariantInfoList = mvProductDetailRepository.findProductVariantInfo(product.getId());
            List<ProductSummaryInfoModel> variants = lvProductVariantMap.getOrDefault(product.getId(), Collections.emptyList());

            // Group summary information by color
            LinkedHashMap<String, String> lvVariantInfoMap = variants.stream()
                    .collect(Collectors.groupingBy(
                            ProductSummaryInfoModel::getColorName,
                            LinkedHashMap::new,
                            Collectors.mapping(
                                    dto -> dto.getSizeName() + " (" + dto.getQuantity() + ")",
                                    Collectors.joining(", ")
                            )
                    ));

            product.setProductVariantInfo(lvVariantInfoMap);
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
        return super.findDtoById(id, pThrowException);
    }

    @Override
    public ProductDTO save(ProductDTO pProduct) {
        Product lvProduct = new Product();
        lvProduct.setProductName(pProduct.getProductName());
        lvProduct.setBrand(mvCategoryService.findEntById(pProduct.getBrandId(), true));
        lvProduct.setProductType(mvCategoryService.findEntById(pProduct.getProductTypeId(), true));
        lvProduct.setUnit(mvCategoryService.findEntById(pProduct.getUnitId(), true));
        lvProduct.setProductCategory(pProduct.getProductCategory());
        lvProduct.setInternalNotes(pProduct.getInternalNotes());

        try {
            if (CoreUtils.isNullStr(lvProduct.getProductName()))
                throw new BadRequestException("Product name is not null!");

            //productToSave.setStatus(ProductStatus.ACT);
            Product lvProductSaved = mvEntityRepository.save(lvProduct);

//            ProductDescription productDescription = null;
//            if (ObjectUtils.isNotEmpty(product.getDescription())) {
//                productDescription = mvProductDescriptionRepository.save(ProductDescription.builder()
//                        .productId(lvProductSaved.getId())
//                        .description(product.getDescription()).build());
//            }

            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_C, MasterObject.Product, "Thêm mới sản phẩm", lvProductSaved.getProductName());
            mvLogger.info("Insert product success! {}", lvProductSaved);
            return ProductConvert.toDto(lvProductSaved);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Transactional
    @Override
    public ProductDTO update(ProductDTO productDTO, Long productId) {
        Long lvProductTypeId = productDTO.getProductTypeId();
        Long lvBrandId = productDTO.getBrandId();
        Long lvUnitId = productDTO.getUnitId();

        Category lvProductType = mvCategoryService.findEntById(lvProductTypeId, true);
        Category lvBrand = mvCategoryService.findEntById(lvBrandId, true);
        Category lvUnit = mvCategoryService.findEntById(lvUnitId, true);

        Product lvProduct = super.findEntById(productId, true);
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvProduct));

        lvProduct.setProductName(productDTO.getProductName());
        lvProduct.setProductType(lvProductType);
        lvProduct.setBrand(lvBrand);
        lvProduct.setUnit(lvUnit);
        //lvProduct.setStatus(productDTO.getStatus());

        ProductDescription productDescription = findDescription(lvProduct.getId());
        if (productDescription != null) {
            productDescription.setDescription(productDTO.getDescription());
        } else {
            productDescription = ProductDescription.builder()
                .productId(lvProduct.getId())
                .description(productDTO.getDescription()).build();
        }
        ProductDescription productDescriptionUpdated = mvProductDescriptionRepository.save(productDescription);

        //lvProduct.setProductDescription(productDescriptionUpdated);
        Product productUpdated = mvEntityRepository.save(lvProduct);

        changeLog.setNewObject(productUpdated);
        changeLog.doAudit();

        String logTitle = "Cập nhật sản phẩm: " + productUpdated.getProductName();

        mvProductHistoryService.save(changeLog.getLogChanges(), logTitle, productUpdated.getId(), null, null);
        mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.Product, logTitle, changeLog);
        mvLogger.info("Update product success! productId={}", productId);
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
            mvLogger.info("Delete product success! productId={}", id);
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (RuntimeException ex) {
            throw new AppException("Delete product fail! productId=" + id, ex);
        }
    }

    @Override
    public boolean productInUse(Long productId) throws RuntimeException {
        return !mvProductVariantService.findAll(ProductVariantSearchRequest.builder()
                .productId(productId)
                .checkInAnyCart(false)
                .build()
        ).getContent().isEmpty();
    }

    @Override
    public List<ProductHeld> getProductHeldInUnfulfilledOrder() {
        List<ProductHeld> productHeldList = new ArrayList<>();
        List<Order> orderPage = mvOrderRepository.findByOrderStatus(List.of(OrderStatus.PROC, OrderStatus.DLVD));
        if (orderPage.isEmpty()) {
            return productHeldList;
        }
        Map<Long, ProductHeld> productHeldMap = new HashMap<>();
        for (Order ord : orderPage) {
            for (OrderDetail ordDetail : ord.getListOrderDetail()) {
                ProductDetail lvProductVariant = ordDetail.getProductDetail();
                Long lvProductVariantId = lvProductVariant.getId();

                ProductHeld productHeldExisted = productHeldMap.get(lvProductVariantId);
                if (productHeldExisted != null) {
                    int currentQuantity = productHeldExisted.getQuantity();
                    productHeldExisted.setQuantity(currentQuantity + ordDetail.getQuantity());
                } else {
                    ProductHeld productHeld = ProductHeld.builder()
                            .productVariantId(lvProductVariantId)
                            .productName(lvProductVariant.getVariantName())
                            .orderCode(ord.getCode())
                            .quantity(ordDetail.getQuantity())
                            .orderStatus(ord.getOrderStatus())
                            .build();
                    productHeldList.add(productHeld);
                    productHeldMap.put(lvProductVariantId, productHeld);
                }
            }
        }
        return productHeldList;
    }

    @Override
    public List<ProductDTO> getDiscontinuedProducts() {
        return findAll(ProductSearchRequest.builder().status(ProductStatus.INA.name()).build()).getContent();
    }

    @Override
    public ProductDescription findDescription(Long pProductId) {
        return mvProductDescriptionRepository.findByProductId(pProductId);
    }
}