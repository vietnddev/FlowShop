package com.flowiee.pms.modules.product.service;

import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.common.constants.JpaHints;
import com.flowiee.pms.modules.log.service.SystemLogService;
import com.flowiee.pms.modules.product.entity.Product;
import com.flowiee.pms.modules.product.entity.ProductDescription;
import com.flowiee.pms.modules.product.entity.ProductDetail;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.entity.OrderDetail;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.product.model.ProductHeld;
import com.flowiee.pms.modules.product.model.ProductVariantParameter;
import com.flowiee.pms.modules.product.model.ProductSummaryInfoModel;
import com.flowiee.pms.modules.category.repository.CategoryRepository;
import com.flowiee.pms.modules.product.repository.ProductDescriptionRepository;
import com.flowiee.pms.modules.product.repository.ProductDetailRepository;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.media.repository.FileStorageRepository;
import com.flowiee.pms.modules.category.service.CategoryService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.product.dto.ProductDTO;
import com.flowiee.pms.modules.product.repository.ProductRepository;
import com.flowiee.pms.common.converter.ProductConvert;
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
public class ProductInfoServiceImpl extends BaseGService<Product, ProductDTO, ProductRepository> implements ProductInfoService {
    private final ProductDescriptionRepository mvProductDescriptionRepository;
    private final ProductVariantService mvProductVariantService;
    private final ProductHistoryService mvProductHistoryService;
    private final OrderRepository mvOrderRepository;
    private final CategoryService mvCategoryService;
    private final CategoryRepository mvCategoryRepository;
    private final FileStorageRepository mvFileStorageRepository;
    private final ProductDetailRepository mvProductDetailRepository;
    private final SystemLogService mvSystemLogService;

    private final Logger mvLogger = LoggerFactory.getLogger(getClass());

    public ProductInfoServiceImpl(ProductRepository pProductRepository, ProductDescriptionRepository pProductDescriptionRepository,
                                  ProductVariantService pProductVariantService, ProductHistoryService pProductHistoryService,
                                  OrderRepository pOrderRepository, CategoryService pCategoryService,
                                  CategoryRepository pCategoryRepository, FileStorageRepository pFileStorageRepository,
                                  ProductDetailRepository pProductDetailRepository, SystemLogService pSystemLogService) {
        super(Product.class, ProductDTO.class, pProductRepository);
        this.mvProductDescriptionRepository = pProductDescriptionRepository;
        this.mvProductVariantService = pProductVariantService;
        this.mvProductHistoryService = pProductHistoryService;
        this.mvOrderRepository = pOrderRepository;
        this.mvCategoryService = pCategoryService;
        this.mvCategoryRepository = pCategoryRepository;
        this.mvFileStorageRepository = pFileStorageRepository;
        this.mvProductDetailRepository = pProductDetailRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<ProductDTO> findAll() {
        return this.findAll(null, -1, -1, null, null, null, null, null, null, null, null, null, null).getContent();
    }

    @Override
    public Page<ProductDTO> findAll(PID pPID , int pageSize, int pageNum, String pTxtSearch, Long pBrandId, Long pProductTypeId,
                                    Long pColorId, Long pSizeId, Long pUnitId, String pGender, Boolean pIsSaleOff, Boolean pIsHotTrend, String pStatus) {
        Pageable lvPageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());

        QueryBuilder<Product> lvQueryBuilder = createQueryBuilder(Product.class)
                .addLike("productName", pTxtSearch)
                .addEqual("brand.id", pBrandId)
                .addEqual("productType.id", pProductTypeId)
                .addOrder("createdAt", false);

        EntityGraph<Product> lvEntityGraph = lvQueryBuilder.createEntityGraph();
        lvEntityGraph.addSubgraph("brand");
        lvEntityGraph.addSubgraph("productType");
        lvEntityGraph.addSubgraph("unit");
        lvEntityGraph.addSubgraph("productVariantList");

        List<Product> lvResultList = lvQueryBuilder.build(lvPageable)
                .setHint(JpaHints.FETCH_GRAPH, lvEntityGraph).getResultList();
        Long lvTotalRecords = lvQueryBuilder.buildCount();

        List<ProductDTO> lvResultListDto = super.convertDTOs(lvResultList);

        assignActiveImages(lvResultListDto);
        assignSummaryVariantInfo(lvResultListDto);

        return new PageImpl<>(lvResultListDto, lvPageable, lvTotalRecords);
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
    public Page<ProductDTO> findClothes(int pageSize, int pageNum, String pTxtSearch, Long pBrand, Long pProductType, Long pColor, Long pSize, Long pUnit, String pGender, Boolean pIsSaleOff, Boolean pIsHotTrend, String pStatus) {
        return findAll(PID.CLOTHES, pageSize, pageNum, pTxtSearch, pBrand, pProductType, pColor, pSize, pUnit, pGender, pIsSaleOff, pIsHotTrend, pStatus);
    }

    @Override
    public Page<ProductDTO> findFruits(int pageSize, int pageNum, String pTxtSearch, String pStatus) {
        return findAll(PID.FRUIT, pageSize, pageNum, pTxtSearch, null, null, null, null, null, null, null, null, pStatus);
    }

    @Override
    public Page<ProductDTO> findSouvenirs(int pageSize, int pageNum, String pTxtSearch, Long pColor, String pStatus) {
        return findAll(PID.SOUVENIR, pageSize, pageNum, pTxtSearch, null, null, pColor, null, null, null, null, null, pStatus);
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
    public ProductDTO saveClothes(ProductDTO productDTO) {
        productDTO.setPID(PID.CLOTHES.getId());
        return save(productDTO);
    }

    @Override
    public ProductDTO saveSouvenir(ProductDTO productDTO) {
        productDTO.setPID(PID.SOUVENIR.getId());
        return save(productDTO);
    }

    @Override
    public ProductDTO saveFruit(ProductDTO productDTO) {
        productDTO.setPID(PID.FRUIT.getId());
        return save(productDTO);
    }

    @Override
    public ProductDTO findById(Long id, boolean pThrowException) {
        return super.findById(id, pThrowException);
    }

    @Override
    public ProductDTO save(ProductDTO product) {
        try {
            Product productToSave = ProductConvert.convertToEntity(product);

            vldCategory(productToSave.getProductType().getId(), productToSave.getBrand().getId(), productToSave.getUnit().getId());
            if (CoreUtils.isNullStr(productToSave.getProductName()))
                throw new BadRequestException("Product name is not null!");

            //productToSave.setCreatedBy(CommonUtils.getUserPrincipal().getId());
            //productToSave.setStatus(ProductStatus.ACT);
            Product productSaved = mvEntityRepository.save(productToSave);

            ProductDescription productDescription = null;
            if (ObjectUtils.isNotEmpty(product.getDescription())) {
                productDescription = mvProductDescriptionRepository.save(ProductDescription.builder()
                        .productId(productSaved.getId())
                        .description(product.getDescription()).build());
            }

            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_C, MasterObject.Product, "Thêm mới sản phẩm", product.getProductName());
            mvLogger.info("Insert product success! {}", product);
            return ProductConvert.toDto(productSaved);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Transactional
    @Override
    public ProductDTO update(ProductDTO productDTO, Long productId) {
        Optional<Product> productOpt = mvEntityRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new AppException(ErrorCode.ENTITY_NOT_FOUND, new Object[]{"product"}, null, getClass(), null);
        }

        Long lvProductTypeId = productDTO.getProductTypeId();
        Long lvBrandId = productDTO.getBrandId();
        Long lvUnitId = productDTO.getUnitId();

        VldModel vldModel = vldCategory(lvProductTypeId, lvBrandId, lvUnitId);
        Category lvProductType = vldModel.getProductType();
        Category lvBrand = vldModel.getBrand();
        Category lvUnit = vldModel.getUnit();

        Product lvProduct = productOpt.get();
        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(productOpt.get()));

        //product.setId(productId);
        lvProduct.setProductName(productDTO.getProductName());
        lvProduct.setProductType(lvProductType);
        lvProduct.setBrand(lvBrand);
        lvProduct.setUnit(lvUnit);
        //lvProduct.setStatus(productDTO.getStatus());

        ProductDescription productDescription = mvProductDescriptionRepository.findByProductId(lvProduct.getId());
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
            ProductDTO productToDelete = this.findById(id, true);
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
        return !mvProductVariantService.findAll(ProductVariantParameter.builder()
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
        return findAll(null, -1, -1, null, null, null, null, null, null, null, null, null, ProductStatus.INA.name()).getContent();
    }

    private VldModel vldCategory(Long pProductTypeId, Long pBrandId, Long pUnitId) {
        Category lvProductType = mvCategoryRepository.findById(pProductTypeId).get();
        if (lvProductType == null)
            throw new BadRequestException("Product type invalid!");

        Category lvBrand = mvCategoryRepository.findById(pBrandId).get();
        if (lvBrand == null)
            throw new BadRequestException("Brand invalid!");

        Category lvUnit = mvCategoryRepository.findById(pUnitId).get();
        if (lvUnit == null)
            throw new BadRequestException("Unit invalid!");

        VldModel vldModel = new VldModel();
        vldModel.setProductType(lvProductType);
        vldModel.setBrand(lvBrand);
        vldModel.setUnit(lvUnit);

        return vldModel;
    }
}