package com.flowiee.pms.modules.leave.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.leave.model.LeaveApplicationReq;
import com.flowiee.pms.modules.leave.service.LeaveApplicationService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.enumeration.Pages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sys/leave-application")
@RequiredArgsConstructor
public class LeaveApplicationControllerView extends BaseController {
    private final LeaveApplicationService leaveApplicationService;

    @GetMapping
    @PreAuthorize("@vldModuleSystem.readLeaveRequests(true)")
    public ModelAndView getRequests() {
        return baseView(new ModelAndView(Pages.SLS_LOYALTY_PROGRAM.getTemplate()));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("@vldModuleSystem.readLeaveRequests(true)")
    public ModelAndView getDetailApplication(@PathVariable("id") Long groupId) {
        ModelAndView modelAndView = new ModelAndView(Pages.SLS_LOYALTY_PROGRAM_DETAIL.getTemplate());
        modelAndView.addObject("leaveApplication", leaveApplicationService.getApplicationById(groupId));
        return baseView(modelAndView);
    }

    @PostMapping("/submit")
    public ModelAndView submitLeaveRequest(@ModelAttribute("leaveApplicationReq") LeaveApplicationReq leaveApplicationReq) {
        if (!CoreUtils.isNullStr(leaveApplicationReq.getReason())) {
            throw new BadRequestException("The reason request can not be empty!");
        }
        leaveApplicationService.submitLeaveRequest(leaveApplicationReq);
        return new ModelAndView("redirect:/sys/leave-application");
    }

    @PostMapping("/leave-application/{id}/approve")
    public ModelAndView approveLeaveRequest(@PathVariable("id") Long id,
                                            @ModelAttribute("leaveApplicationReq") LeaveApplicationReq leaveApplicationReq,
                                            HttpServletRequest request) {
        leaveApplicationService.approveLeaveRequest(id, leaveApplicationReq.getManagerComment());
        return refreshPage(request);
    }

    @PostMapping("/leave-requests/{id}/reject")
    public ModelAndView rejectLeaveRequest(@PathVariable("id") Long id,
                                           @ModelAttribute("leaveApplicationReq") LeaveApplicationReq leaveApplicationReq,
                                           HttpServletRequest request) {
        if (!CoreUtils.isNullStr(leaveApplicationReq.getManagerComment())) {
            throw new BadRequestException("The reason reject can not be empty!");
        }
        leaveApplicationService.rejectLeaveRequest(id, leaveApplicationReq.getManagerComment());
        return refreshPage(request);
    }
}