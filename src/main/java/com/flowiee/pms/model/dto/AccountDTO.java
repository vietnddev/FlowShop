package com.flowiee.pms.model.dto;

import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.Branch;
import com.flowiee.pms.entity.system.GroupAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class AccountDTO implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private Boolean sex;
    private String phoneNumber;
    private String email;
    private String address;
    private String avatar;
    private String remark;
    private String role;
    private Boolean isPartTimeStaff;
    private Integer remainingLeaveDays;
    private Long lineManagerId;
    private GroupAccount groupAccount;
    private Long groupAccountId;
    private String groupAccountName;
    private Branch branch;
    private Long branchId;
    private String branchName;
    private String resetTokens;
    private LocalDateTime resetTokenDate;
    private LocalDate passwordExpireDate;
    private Integer failLogonCount;
    private String status;

    public static AccountDTO toDTO(Account pAccount) {
        AccountDTO dto = new AccountDTO();
        if (pAccount == null) {
            return dto;
        }
        dto.setId(pAccount.getId());
        dto.setUsername(pAccount.getUsername());
        dto.setPassword(pAccount.getPassword());
        dto.setFullName(pAccount.getFullName());
        dto.setSex(pAccount.isSex());
        dto.setPhoneNumber(pAccount.getPhoneNumber());
        dto.setEmail(pAccount.getEmail());
        dto.setAddress(pAccount.getAddress());
        dto.setAvatar("");
        dto.setRemark(pAccount.getRemark());
        dto.setRole(pAccount.getRole());
        dto.setIsPartTimeStaff(pAccount.getIsPartTimeStaff());
        dto.setRemainingLeaveDays(pAccount.getRemainingLeaveDays());
        dto.setLineManagerId(pAccount.getLineManagerId());

        GroupAccount lvGroupAccount = pAccount.getGroupAccount();
        dto.setGroupAccount(lvGroupAccount);
        //dto.setGroupAccountId(lvGroupAccount != null ? lvGroupAccount.getId() : null);
        //dto.setGroupAccountName(lvGroupAccount != null ? lvGroupAccount.getGroupName() : null);

        Branch lvBranch = pAccount.getBranch();
        dto.setBranch(lvBranch);
        //dto.setBranchId(lvBranch != null ? lvBranch.getId() : null);
        //dto.setBranchName(lvBranch != null ? lvBranch.getBranchName() : null);

        dto.setResetTokens(pAccount.getResetTokens());
        dto.setResetTokenDate(pAccount.getResetTokenDate());
        dto.setPasswordExpireDate(pAccount.getPasswordExpireDate());
        dto.setFailLogonCount(pAccount.getFailLogonCount());
        dto.setStatus(pAccount.getStatus());

        return dto;
    }
}