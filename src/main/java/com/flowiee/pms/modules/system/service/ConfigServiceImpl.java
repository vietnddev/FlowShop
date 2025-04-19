package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.common.base.CoreStartUp;
import com.flowiee.pms.common.base.service.BaseGService;
import com.flowiee.pms.modules.category.entity.Category;
import com.flowiee.pms.modules.system.entity.SystemConfig;
import com.flowiee.pms.modules.log.entity.SystemLog;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.system.model.ShopInfo;
import com.flowiee.pms.modules.system.dto.SystemConfigDTO;
import com.flowiee.pms.modules.category.repository.CategoryRepository;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.utils.CommonUtils;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.modules.system.repository.ConfigRepository;
import com.flowiee.pms.modules.category.service.CategoryService;

import com.flowiee.pms.modules.log.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigServiceImpl extends BaseGService<SystemConfig, SystemConfigDTO, ConfigRepository> implements ConfigService {
    private final CategoryService mvCategoryService;
    private final CategoryRepository mvCategoryRepository;
    private final LanguageService mvLanguageService;
    private final ConfigRepository mvSysConfigRepository;
    private final SystemLogService mvSystemLogService;

    public ConfigServiceImpl(ConfigRepository pSysConfigRepository, CategoryService pCategoryService, CategoryRepository pCategoryRepository, LanguageService pLanguageService, SystemLogService pSystemLogService) {
        super(SystemConfig.class, SystemConfigDTO.class, pSysConfigRepository);
        this.mvCategoryService = pCategoryService;
        this.mvCategoryRepository = pCategoryRepository;
        this.mvLanguageService = pLanguageService;
        this.mvSysConfigRepository = pSysConfigRepository;
        this.mvSystemLogService = pSystemLogService;
    }

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private boolean mvAppRefreshing = false;

    @Override
    public Optional<SystemConfig> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public List<SystemConfigDTO> findAll() {
        return super.findAll();
    }

    @Override
    public SystemConfigDTO update(SystemConfigDTO pSystemConfig, Long id) {
        Optional<SystemConfig> lvConfigOpt = this.findById(id);
        if (lvConfigOpt.isEmpty()) {
            throw new BadRequestException();
        }

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvConfigOpt.get()));

        lvConfigOpt.get().setValue(pSystemConfig.getValue());
        SystemConfig lvConfigUpdated = mvSysConfigRepository.save(lvConfigOpt.get());

        changeLog.setNewObject(lvConfigUpdated);
        changeLog.doAudit();

        mvSystemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_CNF_U, MasterObject.SystemConfig, "Cập nhật cấu hình hệ thống", changeLog);
        LOG.info("Update config success! {}", lvConfigUpdated.getName());

        return convertDTO(lvConfigUpdated);
    }

    @Transactional
    @Override
    public String refreshApp() {
        if (mvAppRefreshing) {
            throw new AppException(ErrorCode.SYSTEM_BUSY, new Object[]{}, null, getClass(), null);
        }
        mvAppRefreshing = true;
        LOG.info("Begin refresh app data");
        try {
            ShopInfo lvShopInfo = CommonUtils.mvShopInfo != null ? CommonUtils.mvShopInfo : new ShopInfo();
            //Reload system configs
            List<SystemConfig> systemConfigList = mvEntityRepository.findAll();
            CoreStartUp.getSystemConfigs().clear();
            for (SystemConfig systemConfig : systemConfigList) {
                ConfigCode lvConfigCode = ConfigCode.get(systemConfig.getCode());
                String lvConfigValue = systemConfig.getValue();

                if (lvConfigCode == null) continue;

                if (ConfigCode.resourceUploadPath.equals(lvConfigCode)) CoreStartUp.mvResourceUploadPath = lvConfigValue;
                if (ConfigCode.shopName.equals(lvConfigCode))           lvShopInfo.setName(lvConfigValue);
                if (ConfigCode.shopPhoneNumber.equals(lvConfigCode))    lvShopInfo.setPhoneNumber(lvConfigValue);
                if (ConfigCode.shopEmail.equals(lvConfigCode))          lvShopInfo.setEmail(lvConfigValue);
                if (ConfigCode.shopAddress.equals(lvConfigCode))        lvShopInfo.setAddress(lvConfigValue);
                if (ConfigCode.shopLogoUrl.equals(lvConfigCode))        lvShopInfo.setLogoUrl(lvConfigValue);

                CoreStartUp.getSystemConfigs().put(lvConfigCode, systemConfig);
            }
            CommonUtils.mvShopInfo = lvShopInfo;

            reloadCategoryLabel();

            //Root category's label
            List<Category> rootCategories = mvCategoryService.findRootCategory();
            for (Category c : rootCategories) {
                if (c.getType() != null && !c.getType().trim().isEmpty()) {
                    CATEGORY.valueOf(c.getType()).setLabel(c.getName());
                }
            }

            //Reload order status
            List<Category> lvOrderStatusList = mvCategoryService.findOrderStatus(null);
            for (Category lvCategory : lvOrderStatusList) {
                if (Category.ROOT_LEVEL.equals(lvCategory.getCode()))
                    continue;
                OrderStatus lvOrderStatus = OrderStatus.get(lvCategory.getCode());
                if (lvOrderStatus != null) {
                    lvOrderStatus.setDescription(lvCategory.getNote());
                }
            }

            //Reload message
            mvLanguageService.reloadMessage("vi");
            mvLanguageService.reloadMessage("en");

            if (CoreStartUp.START_APP_TIME != null) {
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
            LOG.info("Finish refresh app data");
        }
    }

    @Override
    public List<SystemConfig> getSystemConfigs(String[] configCodes) {
        return getSystemConfigs(List.of(configCodes));
    }

    @Override
    public SystemConfig getSystemConfig(String configCode) {
        return mvSysConfigRepository.findByCode(configCode);
    }

    @Override
    public List<SystemConfig> getSystemConfigs(List<String> configCodes) {
        return mvSysConfigRepository.findByCode(configCodes);
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
                case ORDER_STATUS:
                    for (OrderStatus lvOS : OrderStatus.values()) {
                        if (lvOS.name().equals(lvCode))
                            lvOS.setDescription(lvLabel);
                    }
                    break;
            }
        }
    }
}