package com.flowiee.pms.modules.system.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.system.entity.Branch;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BranchDTO extends BaseDTO implements Serializable {
    private String branchCode;
    private String branchName;
    private String phoneNumber;
    private String email;
    private String address;
    private String contactPoint;
    private List<Account> listAccount;

    public static BranchDTO toDTO(Branch pBranch) {
        BranchDTO dto = new BranchDTO();
        if (pBranch == null) {
            return dto;
        }
        dto.setId(pBranch.getId());
        dto.setBranchCode(pBranch.getBranchCode());
        dto.setBranchName(pBranch.getBranchName());
        dto.setPhoneNumber(pBranch.getPhoneNumber());
        dto.setEmail(pBranch.getEmail());
        dto.setAddress(pBranch.getAddress());
        dto.setContactPoint(pBranch.getContactPoint());
        dto.setListAccount(pBranch.getListAccount());

        return dto;
    }

    public static List<BranchDTO> toDTOs(List<Branch> pBranches) {
        List<BranchDTO> lvDTOs = new ArrayList<>();
        if (ObjectUtils.isEmpty(pBranches)) {
            return lvDTOs;
        }
        for (Branch lvBranch : pBranches) {
            lvDTOs.add(toDTO(lvBranch));
        }
        return lvDTOs;
    }
}