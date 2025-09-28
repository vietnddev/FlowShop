package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.system.dto.LanguageDTO;
import com.flowiee.pms.modules.system.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys/language")
@RequiredArgsConstructor
public class LanguageController extends BaseController {
    private final LanguageService languageService;

    @GetMapping
    @PreAuthorize("@vldModuleSystem.readConfig(true)")
    public AppResponse<List<LanguageDTO>> findConfigs(@RequestParam(value = Constants.PAGE_SIZE, defaultValue = Constants.DEFAULT_PSIZE) int pageSize,
                                                      @RequestParam(value = Constants.PAGE_NUM, defaultValue = Constants.DEFAULT_PNUM) int pageNum,
                                                      @RequestParam(value = "locale", required = false) String locale) {
        return AppResponse.paged(languageService.findAll(pageNum - 1, pageSize, locale));
    }

    @Operation(summary = "Update config")
    @PutMapping("/update/{id}")
    @PreAuthorize("@vldModuleSystem.updateConfig(true)")
    public AppResponse<LanguageDTO> updateConfig(@RequestBody LanguageDTO pMessage, @PathVariable("id") Long pMessageId) {
        return AppResponse.success(languageService.update(pMessage, pMessageId));
    }
}