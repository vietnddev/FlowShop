package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.modules.inventory.entity.ProductCrawled;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.ForbiddenException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.system.dto.SystemConfigDTO;
import com.flowiee.pms.modules.inventory.repository.ProductCrawlerRepository;
import com.flowiee.pms.modules.inventory.service.CrawlerService;
import com.flowiee.pms.modules.system.service.ConfigService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys")
@Tag(name = "System API", description = "Quản lý hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemController extends BaseController {
    ConfigService    configService;
    CrawlerService   crawlerService;
    ControllerHelper mvCHelper;
    ProductCrawlerRepository productCrawlerRepository;

    private static boolean mvSystemCrawlingData = false;
    private static boolean mvSystemMergingData = false;

    @Operation(summary = "Find all configs")
    @GetMapping("/config/all")
    @PreAuthorize("@vldModuleSystem.readConfig(true)")
    public AppResponse<List<SystemConfigDTO>> findConfigs() {
        try {
            return mvCHelper.success(configService.find());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "configs"), ex);
        }
    }

    @Operation(summary = "Update config")
    @PutMapping("/config/update/{id}")
    @PreAuthorize("@vldModuleSystem.updateConfig(true)")
    public AppResponse<SystemConfigDTO> updateConfig(@RequestBody SystemConfigDTO config, @PathVariable("id") Long configId) {
        return mvCHelper.success(configService.update(config, configId));
    }

    @GetMapping("/refresh")
    public AppResponse<String> refreshApp() {
        return mvCHelper.success(configService.refreshApp());
    }

    @PostMapping("/crawler-data")
    @PreAuthorize("@vldModuleProduct.insertProduct(true)")
    public AppResponse<List<ProductCrawled>> crawlerData() {
        if (!mvUserSession.getUserPrincipal().isAdmin()) {
            throw new ForbiddenException("403");
        }
        if (mvSystemCrawlingData) {
            return mvCHelper.success(null, "System is crawling data, please try late!");
        }
        mvSystemCrawlingData = true;
        try {
            return mvCHelper.success(crawlerService.crawl(), "Successfully crawler data");
        } catch (AppException ex) {
            throw new AppException();
        } finally {
            mvSystemCrawlingData = false;
        }
    }

    @GetMapping("/data-temp")
    public AppResponse<List<ProductCrawled>> getDataTemp(@RequestParam("pageSize") int pageSize, @RequestParam("pageNum") int pageNum) {
        Page<ProductCrawled> productCrawledPage = productCrawlerRepository.findAll(PageRequest.of(pageNum - 1, pageSize));
        return mvCHelper.success(productCrawledPage.getContent(), pageNum, pageSize, productCrawledPage.getTotalPages(), productCrawledPage.getTotalElements());
    }

    @PostMapping("/data-temp/merge")
    public AppResponse<String> mergeDataTemp() {
        if (!mvUserSession.getUserPrincipal().isAdmin()) {
            throw new ForbiddenException("403");
        }
        if (mvSystemMergingData) {
            return mvCHelper.success(null, "System is merging data, please try late!");
        }
        mvSystemMergingData = true;
        try {
            crawlerService.merge();
            return mvCHelper.success(null, "Successfully merged data");
        } catch (AppException ex) {
            throw new AppException(ex.getDisplayMessage(), ex);
        } finally {
            mvSystemMergingData = false;
        }
    }
}