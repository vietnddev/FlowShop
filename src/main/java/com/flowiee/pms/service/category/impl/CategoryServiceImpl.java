package com.flowiee.pms.service.category.impl;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.exception.*;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.common.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.repository.category.CategoryHistoryRepository;
import com.flowiee.pms.repository.category.CategoryRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.category.CategoryHistoryService;
import com.flowiee.pms.service.category.CategoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryServiceImpl extends BaseService implements CategoryService {
    CategoryRepository mvCategoryRepository;
    CategoryHistoryService mvCategoryHistoryService;
    CategoryHistoryRepository mvCategoryHistoryRepository;
    OrderRepository mvOrderRepository;

    @Override
    public List<Category> findAll() {
        return mvCategoryRepository.findAll();
    }

    @Override
    public Category findById(Long entityId, boolean pThrowException) {
        Optional<Category> entityOptional = mvCategoryRepository.findById(entityId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"category"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public Category save(Category entity) {
        if (entity == null) {
            throw new BadRequestException();
        }
        Category categorySaved = mvCategoryRepository.save(entity);
        systemLogService.writeLogCreate(MODULE.CATEGORY, ACTION.CTG_I, MasterObject.Category, "Thêm mới danh mục " + categorySaved.getType(), categorySaved.getName());
        return categorySaved;
    }

    @Transactional
    @Override
    public Category update(Category pCategory, Long categoryId) {
        Category categoryOpt = this.findById(categoryId, true);

        Category categoryBefore = ObjectUtils.clone(categoryOpt);

        categoryOpt.setName(pCategory.getName());
        categoryOpt.setNote(pCategory.getNote());
        if (pCategory.getSort() != null) categoryOpt.setSort(pCategory.getSort());

        Category categorySaved = mvCategoryRepository.save(categoryOpt);

        String logTitle = "Cập nhật thông tin danh mục " + categorySaved.getType() + ": " + categorySaved.getName();
        ChangeLog changeLog = new ChangeLog(categoryBefore, categorySaved);
        mvCategoryHistoryService.save(changeLog.getLogChanges(), logTitle, categoryId);
        systemLogService.writeLogUpdate(MODULE.CATEGORY, ACTION.CTG_U, MasterObject.Category, logTitle, changeLog.getOldValues(), changeLog.getNewValues());
        logger.info("Update Category success! {}", categorySaved);

        return categorySaved;
    }

    @Transactional
    @Override
    public String delete(Long categoryId) {
        Category categoryToDelete = this.findById(categoryId, true);

        if (categoryInUse(categoryId)) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }
        mvCategoryHistoryRepository.deleteAllByCategory(categoryId);
        mvCategoryRepository.deleteById(categoryId);
        systemLogService.writeLogDelete(MODULE.CATEGORY, ACTION.CTG_D, MasterObject.Category, "Xóa danh mục " + categoryToDelete.getType(), categoryToDelete.getName());
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
    public List<Category> findUnits() {
        return findSubCategory(CATEGORY.UNIT, null, null, -1, -1).getContent();
    }

    @Override
    public List<Category> findColors() {
        return findSubCategory(CATEGORY.COLOR, null, null, -1, -1).getContent();
    }

    @Override
    public List<Category> findSizes() {
        return findSubCategory(CATEGORY.SIZE, null, null, -1, -1).getContent();
    }

    @Override
    public List<Category> findSalesChannels() {
        return findSubCategory(CATEGORY.SALES_CHANNEL, null, null, -1, -1).getContent();
    }

    @Override
    public List<Category> findPaymentMethods() {
        return findSubCategory(CATEGORY.PAYMENT_METHOD, null, null, -1, -1).getContent();
    }

    @Override
    public List<Category> findOrderStatus(Long ignoreId) {
        return findSubCategory(CATEGORY.ORDER_STATUS, null, ignoreId != null ? List.of(ignoreId) : null, -1, -1).getContent();
    }

    @Override
    public List<Category> findLedgerGroupObjects() {
        return findSubCategory(CATEGORY.GROUP_OBJECT, null, null, -1, -1).getContent();
    }

    @Override
    public List<Category> findLedgerReceiptTypes() {
        return findSubCategory(CATEGORY.RECEIPT_TYPE, null, null, -1, -1).getContent();
    }

    @Override
    public List<Category> findLedgerPaymentTypes() {
        return findSubCategory(CATEGORY.PAYMENT_TYPE, null, null, -1, -1).getContent();
    }

    @Override
    public boolean categoryInUse(Long categoryId) {
        Category lvCategoryMdl = this.findById(categoryId, true);
        CATEGORY lvCategoryType = CATEGORY.valueOf(lvCategoryMdl.getType().toUpperCase());

        switch (lvCategoryType) {
            case UNIT:
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListUnit())) {
                    return true;
                }
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListProductByUnit())) {
                    return true;
                }
                break;
            case FABRIC_TYPE:
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListFabricType()))
                    return true;
                break;
            case PAYMENT_METHOD:
//                if (ObjectUtils.isNotEmpty(category.get().getListTrangThaiDonHang())) {
//                    return true;
//                }
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListPaymentMethod())) {
                    return true;
                }
                break;
            case SALES_CHANNEL:
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListKenhBanHang())) {
                    return true;
                }
                break;
            case SIZE:
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListLoaiKichCo())) {
                    return true;
                }
                break;
            case COLOR:
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListLoaiMauSac())) {
                    return true;
                }
                break;
            case PRODUCT_TYPE:
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListProductByProductType())) {
                    return true;
                }
                break;
            case ORDER_STATUS:
//                if (ObjectUtils.isNotEmpty(category.get().getListTrangThaiDonHang())) {
//                    return true;
//                }
                break;
            case ORDER_CANCEL_REASON:
                List<Order> orderList = mvOrderRepository.findByCancellationReason(categoryId);
                if (orderList != null && !orderList.isEmpty()) {
                    return true;
                }
                break;
            case GROUP_CUSTOMER:
                if (ObjectUtils.isNotEmpty(lvCategoryMdl.getListCustomerByGroupCustomer())) {
                    return true;
                }
                break;
            default:
                //throw new IllegalStateException("Unexpected value: " + category.get().getType());
                logger.info("Unexpected value: " + lvCategoryMdl.getType());
        }
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
}