package com.flowiee.pms.modules.leave.service;

import com.flowiee.pms.modules.leave.entity.LeaveApplication;
import com.flowiee.pms.modules.leave.model.LeaveApplicationReq;

public interface LeaveApplicationService {
    LeaveApplication getApplicationById(Long leaveApplicationId);

    LeaveApplication submitLeaveRequest(LeaveApplicationReq leaveRequest);

    LeaveApplication approveLeaveRequest(Long requestId, String managerComment);

    LeaveApplication rejectLeaveRequest(Long requestId, String managerComment);
}