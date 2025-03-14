package com.flowiee.pms.service.product.impl;

import com.flowiee.pms.model.Filter;
import com.flowiee.pms.base.service.BaseFService;
import com.flowiee.pms.common.converter.ProductConvert;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.Product;
import com.flowiee.pms.entity.product.ProductDescription;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.sales.OrderDetail;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.DataInUseException;
import com.flowiee.pms.model.ProductHeld;
import com.flowiee.pms.model.ProductSummaryInfoModel;
import com.flowiee.pms.model.ProductVariantParameter;
import com.flowiee.pms.model.dto.ProductDTO;
import com.flowiee.pms.repository.product.ProductDescriptionRepository;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.repository.product.ProductRepository;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.repository.system.FileStorageRepository;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.service.product.ProductHistoryService;
import com.flowiee.pms.service.product.ProductService;
import com.flowiee.pms.service.product.ProductVariantService;
import com.flowiee.pms.service.system.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductFServiceImpl extends BaseFService<Product, ProductDTO, ProductRepository> implements ProductService {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final ProductDescriptionRepository mvProductDescriptionRepository;
    private final ProductVariantService        mvProductVariantService;
    private final ProductHistoryService        mvProductHistoryService;
    private final OrderRepository              mvOrderRepository;
    private final CategoryService              mvCategoryService;
    private final FileStorageRepository        mvFileStorageRepository;
    private final ProductDetailRepository      mvProductDetailRepository;
    private final ModelMapper                  mvModelMapper;
    private final SystemLogService             mvSystemLogService;

    @Override
    public Page<ProductDTO> findAll(PID pPID, int pageSize, int pageNum, String pTxtSearch, Long pBrand, Long pProductType, Long pColor, Long pSize, Long pUnit, String pGender, Boolean pIsSaleOff, Boolean pIsHotTrend, String pStatus) {
        return null;
    }

    @Override
    public Page<ProductDTO> search(List<Filter> filters) {
        Pageable lvPageable = Pageable.unpaged();

        Specification<Product> specification = Specification.where(buildSpecification(filters));
        Page<Product> productPage = entityRepository.findAll(specification, lvPageable);

        return mapEntityPageToDtoPage(productPage, ProductDTO.class);
    }

    private void assignActiveImages(List<ProductDTO> pProductDTOs) {
        List<Long> lvProductIds = pProductDTOs.stream().map(ProductDTO::getId).toList();

        List<FileStorage> lvImageList = new ArrayList<>();
        int batchSize = 1000; // Số lượng tối đa trong một truy vấn
        for (int i = 0; i < lvProductIds.size(); i += batchSize) {
            List<Long> batch = lvProductIds.subList(i, Math.min(i + batchSize, lvProductIds.size()));
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
            List<Long> batch = lvProductIds.subList(i, Math.min(i + batchSize, lvProductIds.size()));
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
        for (Object[] objects : entityRepository.findIdAndName()) {
            products.add(new Product(Integer.parseInt(String.valueOf(objects[0])), String.valueOf(objects[1])));
        }
        return products;
    }

    @Override
    public ProductDTO saveProduct(ProductDTO dto) {
        Product productToSave = mvModelMapper.map(dto, Product.class);

        Category lvProductType = mvCategoryService.findById(productToSave.getProductType().getId(), false);
        if (lvProductType == null)
            throw new BadRequestException("Product type invalid!");

        Category lvBrand = mvCategoryService.findById(productToSave.getBrand().getId(), false);
        if (lvBrand == null)
            throw new BadRequestException("Brand invalid!");

        Category lvUnit = mvCategoryService.findById(productToSave.getUnit().getId(), false);
        if (lvUnit == null)
            throw new BadRequestException("Unit invalid!");

        if (CoreUtils.isNullStr(productToSave.getProductName()))
            throw new BadRequestException("Product name is null!");

        try {
            //productToSave.setCreatedBy(CommonUtils.getUserPrincipal().getId());
            //productToSave.setStatus(ProductStatus.ACT);
            Product productSaved = entityRepository.save(productToSave);

            if (ObjectUtils.isNotEmpty(dto.getDescription())) {
                mvProductDescriptionRepository.save(ProductDescription.builder()
                        .productId(productSaved.getId())
                        .description(dto.getDescription()).build());
            }

            mvSystemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_PRD_C, MasterObject.Product, "Thêm mới sản phẩm", dto.getProductName());
            LOG.info("Insert product success! {}", dto);

            return ProductConvert.convertToDTO(productSaved);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "product"), ex);
        }
    }

    @Transactional
    @Override
    public ProductDTO updateProduct(ProductDTO dto, Long pId) {
        Product product = super.findById(pId, true);

        Long lvProductTypeId = dto.getProductTypeId();
        Long lvBrandId = dto.getBrandId();
        Long lvUnitId = dto.getUnitId();

        //BaseService.VldModel vldModel = vldCategory(lvProductTypeId, lvBrandId, lvUnitId);

        Category lvProductType = mvCategoryService.findById(lvProductTypeId, false);
        if (lvProductType == null)
            throw new BadRequestException("Product type invalid!");

        Category lvBrand = mvCategoryService.findById(lvBrandId, false);
        if (lvBrand == null)
            throw new BadRequestException("Brand invalid!");

        Category lvUnit = mvCategoryService.findById(lvUnitId, false);
        if (lvUnit == null)
            throw new BadRequestException("Unit invalid!");

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(product));

        product.setProductName(dto.getProductName());
        product.setProductType(lvProductType);
        product.setBrand(lvBrand);
        product.setUnit(lvUnit);
        //lvProduct.setStatus(productDTO.getStatus());

        ProductDescription productDescription = mvProductDescriptionRepository.findByProductId(product.getId());
        if (productDescription != null) {
            productDescription.setDescription(dto.getDescription());
        } else {
            productDescription = ProductDescription.builder()
                    .productId(product.getId())
                    .description(dto.getDescription()).build();
        }
        ProductDescription productDescriptionUpdated = mvProductDescriptionRepository.save(productDescription);

        //lvProduct.setProductDescription(productDescriptionUpdated);
        Product productUpdated = entityRepository.save(product);

        changeLog.setNewObject(productUpdated);
        changeLog.doAudit();

        String logTitle = "Cập nhật sản phẩm: " + productUpdated.getProductName();

        mvProductHistoryService.save(changeLog.getLogChanges(), logTitle, productUpdated.getId(), null, null);
        mvSystemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_PRD_U, MasterObject.Product, logTitle, changeLog);
        LOG.info("Update product success! productId={}", pId);

        return mvModelMapper.map(productUpdated, ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long pId) {
        Product product = super.findById(pId, true);
        if (productInUse(pId)) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        entityRepository.deleteById(pId);
        mvSystemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_PRD_D, MasterObject.Product, "Xóa sản phẩm", product.getProductName());
        LOG.info("Delete product success! productId={}", pId);

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public boolean productInUse(Long productId) {
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
        return null;
    }
}