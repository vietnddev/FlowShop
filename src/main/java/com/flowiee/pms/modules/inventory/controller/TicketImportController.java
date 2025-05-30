package com.flowiee.pms.modules.inventory.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.modules.inventory.entity.ProductVariantExim;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.inventory.dto.TicketImportDTO;
import com.flowiee.pms.modules.inventory.entity.MaterialTemp;
import com.flowiee.pms.modules.inventory.entity.TicketImport;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.inventory.service.TicketImportService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg/ticket-import")
@Tag(name = "Ticket import API", description = "Quản lý nhập hàng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TicketImportController extends BaseController {
    TicketImportService mvTicketImportService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Find all phiếu nhập")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<TicketImportDTO>> findAll(@RequestParam("pageSize") int pageSize,
                                                      @RequestParam("pageNum") int pageNum,
                                                      @RequestParam(value = "storageId", required = false) Long storageId) {
        try {
            Page<TicketImport> ticketImports = mvTicketImportService.findAll(pageSize, pageNum - 1, null, null, null, null, null, storageId);
            return mvCHelper.success(TicketImportDTO.fromTicketImports(ticketImports.getContent()), pageNum, pageSize, ticketImports.getTotalPages(), ticketImports.getTotalElements());
        } catch (Exception ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "ticket import"), ex);
        }
    }

    @Operation(summary = "Find detail phiếu nhập")
    @GetMapping("/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TicketImportDTO> findDetail(@PathVariable("id") Long ticketImportId) {
        TicketImport ticketImport = mvTicketImportService.findById(ticketImportId, false);
        if (ticketImport == null) {
            throw new ResourceNotFoundException("Ticket import goods not found!");
        }
        return mvCHelper.success(TicketImportDTO.fromTicketImport(ticketImport));
    }

    @Operation(summary = "Thêm mới phiếu nhập hàng")
    @PostMapping("/create-draft")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TicketImport> createDraftImport(@RequestBody TicketImportDTO ticketImport) {
        try {
            return mvCHelper.success(mvTicketImportService.createDraftTicketImport(ticketImport));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR_OCCURRED.getDescription(), "ticket import"), ex);
        }
    }

    @Operation(summary = "Thêm mới phiếu nhập hàng")
    @PostMapping("/create")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TicketImport> createTicket(@RequestParam("storageId") Long pStorageId, @RequestBody String pTitle) {
        TicketImportDTO dto = new TicketImportDTO();
        dto.setTitle(pTitle);
        dto.setStorageId(pStorageId);
        return mvCHelper.success(mvTicketImportService.createDraftTicketImport(dto));
    }

    @Operation(summary = "Cập nhật phiếu nhập hàng")
    @PutMapping("/update/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<TicketImportDTO> updateTicket(@RequestBody TicketImportDTO ticketImportDTO, @PathVariable("id") Long ticketImportId) {
        return mvCHelper.success(TicketImportDTO.fromTicketImport(mvTicketImportService.update(TicketImport.fromTicketImportDTO(ticketImportDTO), ticketImportId)));
    }

    @Operation(summary = "Xóa phiếu nhập hàng")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<String> deleteTicket(@PathVariable("id") Long ticketImportId) {
        return mvCHelper.success(mvTicketImportService.delete(ticketImportId));
    }

    @Operation(summary = "Add sản phẩm vào phiếu nhập hàng")
    @PostMapping("/{id}/add-product")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<ProductVariantExim>> addProductVariantToTicket(@PathVariable("id") Long ticketImportId,
                                                                           @RequestBody List<Long> productVariantIds) {
        return mvCHelper.success(mvTicketImportService.addProductToTicket(ticketImportId, productVariantIds));
    }

    @Operation(summary = "Add nguyên vật liệu vào phiếu nhập hàng")
    @PostMapping("/{id}/add-material")
    @PreAuthorize("@vldModuleSales.importGoods(true)")
    public AppResponse<List<MaterialTemp>> addMaterialToTicket(@PathVariable("id") Long ticketImportId,
                                                               @RequestBody List<Long> materialIds) {
        if (ticketImportId <= 0 || mvTicketImportService.findById(ticketImportId, false) == null) {
            throw new BadRequestException("Goods import to add product not found!");
        }
        try {
            return mvCHelper.success(mvTicketImportService.addMaterialToTicket(ticketImportId, materialIds));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "ticket_import"), ex);
        }
    }

//    @Operation(summary = "Nhập lại kho đơn hoàn")
//    @PostMapping("/restock-returned-items")
//    @PreAuthorize("@vldModuleSales.importGoods(true)")
//    public AppResponse<String> restockReturnedItems(@RequestParam("storageId") Long pStorageId, @RequestParam("orderCode") String pOrderCode) {
//        mvTicketImportService.restockReturnedItems(pStorageId, pOrderCode);
//        return mvCHelper.success("Nhập hoàn thành công!");
//    }
}