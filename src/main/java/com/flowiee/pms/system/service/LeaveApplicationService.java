package com.flowiee.pms.system.service;

import com.flowiee.pms.system.entity.LeaveApplication;
import com.flowiee.pms.system.model.LeaveApplicationReq;

public interface LeaveApplicationService {
    LeaveApplication getApplicationById(Long leaveApplicationId);

    LeaveApplication submitLeaveRequest(LeaveApplicationReq leaveRequest);

    LeaveApplication approveLeaveRequest(Long requestId, String managerComment);

    LeaveApplication rejectLeaveRequest(Long requestId, String managerComment);
}