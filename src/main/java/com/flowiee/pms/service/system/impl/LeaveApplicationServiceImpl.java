package com.flowiee.pms.service.system.impl;


import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.LeaveApplication;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.payload.LeaveApplicationReq;
import com.flowiee.pms.repository.system.LeaveApplicationRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.category.CategoryService;
import com.flowiee.pms.service.system.AccountService;
import com.flowiee.pms.service.system.LeaveApplicationService;
import com.flowiee.pms.service.system.MailMediaService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.LeaveStatus;
import com.flowiee.pms.common.enumeration.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeaveApplicationServiceImpl extends BaseService implements LeaveApplicationService {
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final MailMediaService mailMediaService;
    private final CategoryService categoryService;
    private final AccountService employeeService;

    @Override
    public LeaveApplication getApplicationById(Long leaveApplicationId) {
        return leaveApplicationRepository.findById(leaveApplicationId)
                .orElseThrow(() -> new EntityNotFoundException(new Object[] {"leave application"}, null, null));
    }

    @Transactional
    @Override
    public LeaveApplication submitLeaveRequest(LeaveApplicationReq pLeaveRequest) {
        Account employee = employeeService.findById(pLeaveRequest.getEmployee(), true);
        if (employee.getRemainingLeaveDays() < pLeaveRequest.getTotalLeaveDays()) {
            throw new AppException(ErrorCode.InsufficientLeaveBalance, new Object[]{}, null, getClass(), null);
        }

        StringBuilder replacementEmployee = new StringBuilder();
        for (int i = 0; i < pLeaveRequest.getReplacementEmployees().length; i++) {
            Long replacementId = pLeaveRequest.getReplacementEmployees()[i];
            Account repEmployee = employeeService.findById(replacementId, true);
            if (i < pLeaveRequest.getReplacementEmployees().length - 1) {
                replacementEmployee.append(repEmployee.getUsername()).append(",");
            } else {
                replacementEmployee.append(repEmployee.getUsername());
            }
        }

        Category leaveType = categoryService.findById(pLeaveRequest.getLeaveType(), true);
        LeaveApplication leaveApplicationSubmitted = leaveApplicationRepository.save(LeaveApplication.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(pLeaveRequest.getStartDate())
                .endDate(pLeaveRequest.getEndDate())
                .reason(pLeaveRequest.getReason())
                .status(LeaveStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .replacementEmployee(replacementEmployee.toString())
                .build());

        Account lineManager = employeeService.findById(employee.getLineManagerId(), false);
        if (lineManager == null) {
            throw new AppException(ErrorCode.NoApproverDefined, new Object[]{}, null, getClass(), null);
        }
        sendEmail(leaveApplicationSubmitted.getStatus(), employee, lineManager, leaveApplicationSubmitted);

        return leaveApplicationSubmitted;
    }

    @Override
    public LeaveApplication approveLeaveRequest(Long leaveRequestId, String managerComment) {
        LeaveApplication leaveRequest = getApplicationById(leaveRequestId);
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setResponseDate(LocalDateTime.now());
        leaveRequest.setManagerComment(managerComment);
        LeaveApplication leaveApplicationApproved = leaveApplicationRepository.save(leaveRequest);

        Account lvEmployee = leaveRequest.getEmployee();
        Account lvManager = employeeService.findById(lvEmployee.getLineManagerId(), true);
        sendEmail(leaveApplicationApproved.getStatus(), leaveRequest.getEmployee(), lvManager, leaveApplicationApproved);

        return leaveApplicationApproved;
    }

    @Override
    public LeaveApplication rejectLeaveRequest(Long requestId, String managerComment) {
        LeaveApplication leaveRequest = getApplicationById(requestId);
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setResponseDate(LocalDateTime.now());
        leaveRequest.setManagerComment(managerComment);
        LeaveApplication leaveApplicationRejected = leaveApplicationRepository.save(leaveRequest);

        Account lvEmployee = leaveRequest.getEmployee();
        Account lvManager = employeeService.findById(lvEmployee.getLineManagerId(), true);
        sendEmail(leaveApplicationRejected.getStatus(), lvEmployee, lvManager, leaveApplicationRejected);

        return leaveApplicationRejected;
    }

    private void sendEmail(LeaveStatus leaveStatus, Account employee, Account lineManager, LeaveApplication leaveApplication) {
        NotificationType notificationType = NotificationType.LeaveApplicationAlert;

        Map<String, Object> lvParameter = new HashMap<>();
        lvParameter.put("managerName", lineManager.getFullName());
        lvParameter.put("employeeName", employee.getFullName());
        lvParameter.put("startTime", leaveApplication.getStartDate());
        lvParameter.put("endTime", leaveApplication.getEndDate());
        lvParameter.put("totalLeaveDays", leaveApplication.getTotalLeaveDays());
        lvParameter.put("leaveType", leaveApplication.getLeaveType().getName());
        lvParameter.put("reason", leaveApplication.getReason());

        switch (leaveStatus) {
            case PENDING:
                lvParameter.put(notificationType.name(), lineManager.getEmail());
                lvParameter.put("leaveStatus", leaveApplication.getStatus().getDescription());
                mailMediaService.send(notificationType, lvParameter);
                break;
            case APPROVED:
                lvParameter.put(notificationType.name(), employee.getEmail());
                lvParameter.put("leaveStatus", LeaveStatus.APPROVED.getDescription());
                mailMediaService.send(notificationType, lvParameter);
                break;
            case REJECTED:
                lvParameter.put(notificationType.name(), employee.getEmail());
                lvParameter.put("leaveStatus", LeaveStatus.REJECTED.getDescription());
                mailMediaService.send(notificationType, lvParameter);
        }
    }
}