package com.flowiee.pms.modules.sales.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.modules.sales.entity.LoyaltyProgram;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.sales.service.LoyaltyProgramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/loyalty-programs")
@Tag(name = "Order API", description = "Quản lý đơn hàng")
@RequiredArgsConstructor
public class LoyaltyProgramController extends BaseController {
    private final LoyaltyProgramService loyaltyProgramService;

    @GetMapping
    @PreAuthorize("@vldModuleSales.readOrder(true)")
    public AppResponse<List<LoyaltyProgram>> getAllPrograms() {
        return AppResponse.success(loyaltyProgramService.find());
    }

    @GetMapping("/active")
    @PreAuthorize("@vldModuleSales.readOrder(true)")
    public AppResponse<List<LoyaltyProgram>> getActivePrograms() {
        return AppResponse.success(loyaltyProgramService.getActivePrograms());
    }

//    @GetMapping("/{id}")
//    @PreAuthorize("@vldModuleSales.readOrder(true)")
//    public AppResponse<LoyaltyProgram> getProgramById(@PathVariable Long id) {
//        return AppResponse.success(loyaltyProgramService.findById(id, true));
//    }
//
//    @PostMapping
//    @PreAuthorize("@vldModuleSales.readOrder(true)")
//    public AppResponse<LoyaltyProgram> createProgram(@RequestBody LoyaltyProgram program) {
//        return AppResponse.success(loyaltyProgramService.save(program));
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("@vldModuleSales.readOrder(true)")
//    public AppResponse<LoyaltyProgram> updateProgram(@PathVariable Long id, @RequestBody LoyaltyProgram program) {
//        return AppResponse.success(loyaltyProgramService.update(program, id));
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("@vldModuleSales.readOrder(true)")
//    public AppResponse<String> deleteProgram(@PathVariable Long id) {
//        return AppResponse.success(loyaltyProgramService.delete(id));
//    }
}