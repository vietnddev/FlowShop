package com.flowiee.pms.modules.staff.service;

import com.flowiee.pms.modules.staff.entity.LeaveApplication;
import com.flowiee.pms.modules.staff.model.LeaveApplicationReq;

public interface LeaveApplicationService {
    LeaveApplication getApplicationById(Long leaveApplicationId);

    LeaveApplication submitLeaveRequest(LeaveApplicationReq leaveRequest);

    LeaveApplication approveLeaveRequest(Long requestId, String managerComment);

    LeaveApplication rejectLeaveRequest(Long requestId, String managerComment);
}