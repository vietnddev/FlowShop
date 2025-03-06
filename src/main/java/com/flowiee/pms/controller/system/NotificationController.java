package com.flowiee.pms.controller.system;

import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.entity.system.Notification;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.model.AppResponse;
import com.flowiee.pms.service.system.NotificationService;
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

    @Operation(summary = "Find all notifications")
    @GetMapping("/all")
    public AppResponse<List<Notification>> findAll(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                   @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        try {
            return success(notificationService.findAll());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "notification"), ex);
        }
    }

    @Operation(summary = "Find all notifications")
    @GetMapping("/{accountId}")
    public AppResponse<List<Notification>> findByAccount(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                         @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                                         @RequestParam(value = "totalRecord", required = false) Integer totalRecord,
                                                         @PathVariable("accountId") long accountId) {
        try {
            return success(notificationService.findAllByReceiveId(pageSize, pageNum, totalRecord, accountId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR_OCCURRED.getDescription(), "notification"), ex);
        }
    }
}