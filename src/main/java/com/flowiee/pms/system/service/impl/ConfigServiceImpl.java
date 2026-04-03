package com.flowiee.pms.system.service.impl;

import com.flowiee.pms.product.enums.ProductStatus;
import com.flowiee.pms.shared.base.StartUp;
import com.flowiee.pms.shared.base.FlwSys;
import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.system.entity.Category;
import com.flowiee.pms.system.entity.SystemConfig;
import com.flowiee.pms.system.entity.SystemLog;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.system.enums.CATEGORY;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.system.enums.LogType;
import com.flowiee.pms.system.model.ShopInfo;
import com.flowiee.pms.system.dto.SystemConfigDTO;
import com.flowiee.pms.system.repository.CategoryRepository;
import com.flowiee.pms.shared.util.ChangeLog;
import com.flowiee.pms.shared.util.CommonUtils;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.system.repository.ConfigRepository;

import com.flowiee.pms.system.service.CategoryService;
import com.flowiee.pms.system.service.ConfigService;
import com.flowiee.pms.system.service.LanguageService;
import com.flowiee.pms.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private final CategoryService mvCategoryService;
    private final CategoryRepository mvCategoryRepository;
    private final LanguageService mvLanguageService;
    private final ConfigRepository mvSysConfigRepository;
    private final SystemLogService mvSystemLogService;
    private final ModelMapper mvModelMapper;

    private boolean mvAppRefreshing = false;

    @Override
    public List<SystemConfigDTO> getAll() {
        return mvSysConfigRepository.findAll().stream()
                .map(entity -> mvModelMapper.map(entity, SystemConfigDTO.class))
                .toList();
    }

    @Override
    public SystemConfigDTO update(SystemConfigDTO pSystemConfig, Long id) {
        Optional<SystemConfig> lvConfigOpt = mvSysConfigRepository.findById(id);
        if (lvConfigOpt.isEmpty()) {
            throw new BadRequestException();
        }

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvConfigOpt.get()));

        lvConfigOpt.get().setValue(pSystemConfig.getValue());
        SystemConfig lvConfigUpdated = mvSysConfigRepository.save(lvConfigOpt.get());

        changeLog.setNewObject(lvConfigUpdated);
        changeLog.doAudit();

        mvSystemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_CNF_U, MasterObject.SystemConfig, "Cập nhật cấu hình hệ thống", changeLog);
        log.info("Update config success! {}", lvConfigUpdated.getName());

        return mvModelMapper.map(lvConfigUpdated, SystemConfigDTO.class);
    }

    @Transactional
    @Override
    public String refreshApp() {
        if (mvAppRefreshing) {
            throw new AppException(ErrorCode.SYSTEM_BUSY, new Object[]{}, null, getClass(), null);
        }
        mvAppRefreshing = true;

        try {
            ShopInfo lvShopInfo = CommonUtils.mvShopInfo != null ? CommonUtils.mvShopInfo : new ShopInfo();
            //Reload system configs
            List<SystemConfig> systemConfigList = mvSysConfigRepository.findAll();
            FlwSys.getSystemConfigs().clear();
            for (SystemConfig systemConfig : systemConfigList) {
                ConfigCode lvConfigCode = ConfigCode.get(systemConfig.getCode());
                String lvConfigValue = systemConfig.getValue();

                if (lvConfigCode == null) continue;

                if (ConfigCode.resourceUploadPath.equals(lvConfigCode)) StartUp.mvResourceUploadPath = lvConfigValue;
                if (ConfigCode.shopName.equals(lvConfigCode))           lvShopInfo.setName(lvConfigValue);
                if (ConfigCode.shopPhoneNumber.equals(lvConfigCode))    lvShopInfo.setPhoneNumber(lvConfigValue);
                if (ConfigCode.shopEmail.equals(lvConfigCode))          lvShopInfo.setEmail(lvConfigValue);
                if (ConfigCode.shopAddress.equals(lvConfigCode))        lvShopInfo.setAddress(lvConfigValue);
                if (ConfigCode.shopLogoUrl.equals(lvConfigCode))        lvShopInfo.setLogoUrl(lvConfigValue);

                FlwSys.getSystemConfigs().put(lvConfigCode, systemConfig);
            }
            CommonUtils.mvShopInfo = lvShopInfo;

            reloadCategoryLabel();

            //Root category's label
            List<Category> rootCategories = mvCategoryService.findRootCategory();
            for (Category c : rootCategories) {
                try {
                    if (c.getType() != null && !c.getType().trim().isEmpty()) {
                        CATEGORY.valueOf(c.getType()).setLabel(c.getName());
                    }
                } catch (IllegalArgumentException e) {
                    log.warn(e.getMessage());
                }
            }

            //Reload message
            mvLanguageService.reloadMessage("vi");
            mvLanguageService.reloadMessage("en");

            if (StartUp.START_APP_TIME != null) {
                mvSystemLogService.writeLog(MODULE.SYSTEM, ACTION.SYS_REFRESH_APP, MasterObject.Master, LogType.U, "Refresh application", SystemLog.EMPTY, SystemLog.EMPTY);
            }

            int i = 1;
            return new StringBuilder()
                    .append("Completed the following tasks: ")
                    .append("\n " + i++ + ". ").append("Reload message vi & en")
                    .append("\n " + i++ + ". ").append("Reload system configs")
                    .append("\n " + i++ + ". ").append("Reload categories label")
                    .toString();
        } catch (RuntimeException ex) {
            throw new AppException("An error occurred while refreshing app configuration", ex);
        } finally {
            mvAppRefreshing = false;
            log.info("Configurations have been refreshed successfully.");
        }
    }

    private void reloadCategoryLabel() {
        List<String> lvCategoryTypeList = new ArrayList<>();
        lvCategoryTypeList.add(CATEGORY.PRODUCT_STATUS.getName());

        List<Category> lvCategoryList = mvCategoryRepository.findSubCategory(lvCategoryTypeList);
        if (lvCategoryList == null) {
            return;
        }

        for (Category lvCategory : lvCategoryList) {
            String lvLabel = CoreUtils.trim(lvCategory.getName());
            String lvCode = CoreUtils.trim(lvCategory.getCode());
            CATEGORY lvCategoryType = CATEGORY.valueOf(CoreUtils.trim(lvCategory.getType()));
            switch (lvCategoryType) {
                case PRODUCT_STATUS:
                    for (ProductStatus lvPS : ProductStatus.values()) {
                        if (lvPS.name().equals(lvCode))
                            lvPS.setLabel(lvLabel);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}