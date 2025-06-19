package com.flowiee.pms.modules.system.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.system.dto.NotificationDTO;
import com.flowiee.pms.modules.system.service.NotificationService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/notification")
@Tag(name = "Notification API", description = "Thông báo hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationController extends BaseController {
    NotificationService notificationService;
    ControllerHelper mvCHelper;

    @Operation(summary = "Find all notifications")
    @GetMapping("/all")
    public AppResponse<List<NotificationDTO>> findAll(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                   @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            return mvCHelper.success(notificationService.findAll());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "notification"), ex);
        }
    }

    @Operation(summary = "Find all notifications")
    @GetMapping("/{accountId}")
    public AppResponse<List<NotificationDTO>> findByAccount(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                            @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                            @RequestParam(value = "totalRecord", required = false) Integer totalRecord,
                                                            @PathVariable("accountId") long accountId) {
        try {
            return mvCHelper.success(notificationService.findAllByReceiveId(pageSize, pageNum, totalRecord, accountId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "notification"), ex);
        }
    }
}