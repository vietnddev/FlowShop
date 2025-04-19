package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.inventory.dto.StorageDTO;
import com.flowiee.pms.modules.inventory.service.StorageService;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/storage")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StorageControllerView extends BaseController {
    StorageService mvStorageService;

    @GetMapping
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public ModelAndView findAll() {
        return baseView(new ModelAndView(Pages.STG_STORAGE.getTemplate()));
    }

    @GetMapping(value = "/{storageId}")
    @PreAuthorize("@vldModuleStorage.readStorage(true)")
    public ModelAndView findDetail(@PathVariable("storageId") Long storageId) {
        StorageDTO storage = mvStorageService.findById(storageId, true);

        ModelAndView modelAndView = new ModelAndView(Pages.STG_STORAGE_DETAIL.getTemplate());
        modelAndView.addObject("storageId", storage.getId());
        modelAndView.addObject("storage", storage);
        return baseView(modelAndView);
    }
}