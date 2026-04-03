package com.flowiee.pms.shared.exception;

import com.flowiee.pms.shared.base.FlwSys;
import com.flowiee.pms.shared.base.BaseException;
import com.flowiee.pms.shared.base.BaseController;
import com.flowiee.pms.shared.util.SysConfigUtils;
import com.flowiee.pms.system.entity.SystemConfig;
import com.flowiee.pms.shared.response.AppResponse;
import com.flowiee.pms.system.service.MailMediaService;
import com.flowiee.pms.shared.util.CoreUtils;
import com.flowiee.pms.system.enums.ConfigCode;
import com.flowiee.pms.shared.enums.Pages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends BaseController {
    private final MailMediaService mailMediaService;

    private Map<Class, Boolean> mvExceptionNotifyEmail = new HashMap<>();

    {
        mvExceptionNotifyEmail.put(AppException.class, true);
    }

    private void notifyEmail(BaseException pEx) {
        if (mvExceptionNotifyEmail.get(pEx.getClass()) == null) {
            return;
        }

        if (!SysConfigUtils.isYesOption(ConfigCode.sendNotifyAdminExceptionRuntime)) {
            return;
        }

        SystemConfig lvRecipientConfig = FlwSys.getSystemConfigs().get(ConfigCode.adminEmailRecipientExceptionNotification);
        String lvRecipients = lvRecipientConfig.getValue();
        String lvMessage = CoreUtils.isNullStr(pEx.getFullStackTrace())
                ? pEx.getMessage() : pEx.getFullStackTrace();

        mailMediaService.send(lvRecipients, "[Flowiee] Thông báo hệ thống!", lvMessage);
    }

    @ExceptionHandler
    public ModelAndView exceptionHandler(AuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        return new ModelAndView(Pages.SYS_LOGIN.getTemplate());
    }

    @ExceptionHandler
    public Object exceptionHandler(ResourceNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        if (ex.isRedirectView()) {
            ErrorModel error = new ErrorModel(HttpStatus.NOT_FOUND.value(), ex.getMessage());
            ModelAndView modelAndView = new ModelAndView(ex.getView() != null ? ex.getView() : Pages.SYS_ERROR.getTemplate());
            modelAndView.addObject("error", error);
            return baseView(modelAndView);
        } else {
            return ResponseEntity.badRequest().body(AppResponse.fail(HttpStatus.BAD_REQUEST, ex.getMessage()));
        }
    }

    @ExceptionHandler
    public ResponseEntity<AppResponse<Object>> exceptionHandler(BadRequestException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(AppResponse.fail(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<AppResponse<?>> exceptionHandler(DataExistsException ex) {
        log.error(ex.getMessage(), ex);
        notifyEmail(ex);
        return ResponseEntity.badRequest().body(AppResponse.fail(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler
    public ModelAndView exceptionHandler(ForbiddenException ex) {
        log.error(ex.getMessage(), ex);
        ErrorModel error = new ErrorModel(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        ModelAndView modelAndView = new ModelAndView(Pages.SYS_ERROR.getTemplate());
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }

    @ExceptionHandler
    public ResponseEntity<AppResponse<Object>> exceptionHandler(DataInUseException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(AppResponse.fail(HttpStatus.LOCKED, ex.getMessage()));
    }

    @ExceptionHandler
    public Object exceptionHandler(AccountLockedException ex) {
        log.error(ex.getMessage(), ex);
        if (ex.isRedirectView()) {
            ErrorModel error = new ErrorModel(HttpStatus.LOCKED.value(), ex.getMessage());
            ModelAndView modelAndView = new ModelAndView(ex.getView() != null ? ex.getView() : Pages.SYS_ERROR.getTemplate());
            modelAndView.addObject("error", error);
            return baseView(modelAndView);
        } else {
            return ResponseEntity.badRequest().body(AppResponse.fail(HttpStatus.LOCKED, ex.getMessage()));
        }
    }

    @ExceptionHandler
    public ResponseEntity<AppResponse<Object>> exceptionHandler(AppException ex) {
        log.error(ex.getMessage(), ex);
        notifyEmail(ex);
        return ResponseEntity.internalServerError ().body(AppResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<AppResponse<?>> exceptionHandler(RuntimeException ex) {
        log.error(ex.getMessage(), ex);

        BaseException lvBaseException = new BaseException();
        lvBaseException.setMessage(ex.getMessage());
        notifyEmail(lvBaseException);

        return ResponseEntity.internalServerError().body(AppResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<AppResponse<?>> exceptionHandler(Exception ex) {
        log.error(ex.getMessage(), ex);

        BaseException lvBaseException = new BaseException();
        lvBaseException.setMessage(ex.getMessage());
        notifyEmail(lvBaseException);

        return ResponseEntity.internalServerError().body(AppResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}