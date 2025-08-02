package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.sales.dto.OrderDetailDTO;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.system.service.CategoryHistoryService;
import com.flowiee.pms.modules.system.service.CategoryService;
import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.system.dto.CategoryDTO;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.system.repository.CategoryRepository;

import com.flowiee.pms.modules.system.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends BaseService<Category, CategoryDTO, CategoryRepository> implements CategoryService {
    private final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository mvCategoryRepository;
    private final CategoryHistoryService mvCategoryHistoryService;
    private final SystemLogService mvSystemLogService;
    private final ModelMapper mvModelMapper;

    public CategoryServiceImpl(CategoryRepository pEntityRepository, CategoryRepository mvCategoryRepository, CategoryHistoryService mvCategoryHistoryService, SystemLogService mvSystemLogService, ModelMapper mvModelMapper) {
        super(Category.class, CategoryDTO.class, pEntityRepository);
        this.mvCategoryRepository = mvCategoryRepository;
        this.mvCategoryHistoryService = mvCategoryHistoryService;
        this.mvSystemLogService = mvSystemLogService;
        this.mvModelMapper = mvModelMapper;
    }

    @Override
    public Category findEntById(Long entityId, boolean pThrowException) {
        return super.findEntById(entityId, pThrowException);
    }

    @Override
    public CategoryDTO findById(Long entityId, boolean pThrowException) {
        return super.findDtoById(entityId, pThrowException);
    }

    @Transactional
    @Override
    public CategoryDTO save(CategoryDTO pDto) {
        if (pDto == null) {
            throw new BadRequestException();
        }
        CategoryDTO categorySaved = super.save(pDto);
        mvSystemLogService.writeLogCreate(MODULE.CATEGORY, ACTION.CTG_I, MasterObject.Category, "Thêm mới danh mục " + categorySaved.getType(), categorySaved.getName());
        return categorySaved;
    }

    @Transactional
    @Override
    public CategoryDTO update(CategoryDTO pCategory, Long categoryId) {
        Category lvCurrentCategory = super.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(new Object[]{"category"}, null, null));

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvCurrentCategory));

        lvCurrentCategory.setName(pCategory.getName());
        lvCurrentCategory.setNote(pCategory.getNote());
        if (pCategory.getSort() != null)
            lvCurrentCategory.setSort(pCategory.getSort());

        Category categorySaved = mvEntityRepository.save(lvCurrentCategory);

        changeLog.setNewObject(categorySaved);
        changeLog.doAudit();

        String logTitle = "Cập nhật thông tin danh mục " + categorySaved.getType() + ": " + categorySaved.getName();
        mvCategoryHistoryService.save(changeLog.getLogChanges(), logTitle, categoryId);

        mvSystemLogService.writeLogUpdate(MODULE.CATEGORY, ACTION.CTG_U, MasterObject.Category, logTitle, changeLog.getOldValues(), changeLog.getNewValues());
        LOG.info("Update Category success! {}", categorySaved);

        return mvModelMapper.map(categorySaved, CategoryDTO.class);
    }

    @Transactional
    @Override
    public String delete(Long categoryId) {
        Category lvCurrentCategory = this.findEntById(categoryId, true);

        if (categoryInUse(categoryId)) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }

        mvEntityRepository.deleteById(categoryId);

        mvSystemLogService.writeLogDelete(MODULE.CATEGORY, ACTION.CTG_D, MasterObject.Category, "Xóa danh mục " + lvCurrentCategory.getType(), lvCurrentCategory.getName());

        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<Category> findRootCategory() {
        try {
            List<Category> roots = mvCategoryRepository.findRootCategory();
            List<Object[]> recordsOfEachType = mvCategoryRepository.totalRecordsOfEachType();
            for (Category c : roots) {
                for (Object[] o : recordsOfEachType) {
                    if (c.getType().equals(o[0])) {
                        c.setTotalSubRecords(Integer.parseInt(String.valueOf(o[1])));
                        break;
                    }
                }
            }
            return roots;
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "category"), ex);
        }
    }

    @Override
    public Page<Category> findSubCategory(CATEGORY categoryType, Long parentId, List<Long> ignoreIds, int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());
        Page<Category> categoryPage = mvCategoryRepository.findSubCategory(categoryType.name(), parentId, ignoreIds, pageable);
        for (Category c : categoryPage.getContent()) {
            String statusName = CategoryStatus.I.getLabel();
            if (c.getStatus()) {
                statusName = CategoryStatus.A.getLabel();
            }
            boolean categoryInUse = categoryInUse(c.getId());
            c.setInUse(categoryInUse ? "Đang được sử dụng" : "Chưa sử dụng");
            c.setStatusName(statusName);
        }
        return new PageImpl<>(categoryPage.getContent(), pageable, categoryPage.getTotalElements());
    }

    @Override
    public List<Category> findByIds(List<Long> pIds) {
        return super.findByIds(pIds);
    }

    @Override
    public List<Category> findOrderStatus(Long ignoreId) {
        return findSubCategory(CATEGORY.ORDER_STATUS, null, ignoreId != null ? List.of(ignoreId) : null, -1, -1).getContent();
    }

    @Override
    public List<Category> findByType(CATEGORY pType) {
        return findSubCategory(pType, null, null, -1, -1).getContent();
    }

    @Override
    public boolean categoryInUse(Long categoryId) {
        Category lvCategoryMdl = super.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("category with id " + categoryId));
        CATEGORY lvCategoryType = CATEGORY.valueOf(lvCategoryMdl.getType().toUpperCase());

//        switch (lvCategoryType) {
//            case UNIT:
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListUnit())) {
//                    return true;
//                }
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListProductByUnit())) {
//                    return true;
//                }
//                break;
//            case FABRIC_TYPE:
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListFabricType()))
//                    return true;
//                break;
//            case PAYMENT_METHOD:
////                if (ObjectUtils.isNotEmpty(category.get().getListTrangThaiDonHang())) {
////                    return true;
////                }
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListPaymentMethod())) {
//                    return true;
//                }
//                break;
//            case SALES_CHANNEL:
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListKenhBanHang())) {
//                    return true;
//                }
//                break;
//            case SIZE:
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListLoaiKichCo())) {
//                    return true;
//                }
//                break;
//            case COLOR:
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListLoaiMauSac())) {
//                    return true;
//                }
//                break;
//            case PRODUCT_TYPE:
//                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListProductByProductType())) {
//                    return true;
//                }
//                break;
//            case ORDER_STATUS:
////                if (ObjectUtils.isNotEmpty(category.get().getListTrangThaiDonHang())) {
////                    return true;
////                }
//                break;
//            case ORDER_CANCEL_REASON:
//                List<Order> orderList = mvOrderRepository.findByCancellationReason(categoryId);
//                if (orderList != null && !orderList.isEmpty()) {
//                    return true;
//                }
//                break;
//            case GROUP_CUSTOMER:
////                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListCustomerByGroupCustomer())) {
////                    return true;
////                }
//                break;
//            default:
//                //throw new IllegalStateException("Unexpected value: " + category.get().getType());
//                LOG.info("Unexpected value: " + lvCategoryMdl.getType());
//        }
        return false;
    }

    @Override
    public Map<CATEGORY, List<Category>> findByType(List<CATEGORY> pCategoryTypeList) {
        Map<CATEGORY, List<Category>> categoryMap = new HashMap<>();
        for (CATEGORY type : pCategoryTypeList) {
            categoryMap.put(type, new ArrayList<>());
        }
        mvCategoryRepository.findSubCategory(pCategoryTypeList.stream().map(CATEGORY::name).toList())
                .forEach(c -> categoryMap.get(CATEGORY.valueOf(c.getType())).add(c));
        return categoryMap;
    }

    @Override
    public Map<CATEGORY, Category> findByIdsAsMap(Set<Long> ids) {
        return super.findByIds(ids.stream().toList()).stream()
                .collect(Collectors.toMap(c -> CATEGORY.valueOf(c.getType()), Function.identity()));
    }
}