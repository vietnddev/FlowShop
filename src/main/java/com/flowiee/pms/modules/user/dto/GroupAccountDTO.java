package com.flowiee.pms.modules.user.dto;

import com.flowiee.pms.modules.user.entity.Account;
import com.flowiee.pms.modules.user.entity.GroupAccount;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GroupAccountDTO implements Serializable {
    private Long id;
    private String groupCode;
    private String groupName;
    private String note;
    private List<Account> listAccount;

    public static GroupAccountDTO toDTO(GroupAccount pGroupAccount) {
        GroupAccountDTO dto = new GroupAccountDTO();
        if (pGroupAccount == null) {
            return dto;
        }
        dto.setId(pGroupAccount.getId());
        dto.setGroupCode(pGroupAccount.getGroupCode());
        dto.setGroupName(pGroupAccount.getGroupName());
        dto.setNote(pGroupAccount.getNote());
        dto.setListAccount(pGroupAccount.getListAccount());

        return dto;
    }

    public static List<GroupAccountDTO> toDTOs(List<GroupAccount> pGAccounts) {
        List<GroupAccountDTO> lvDTOs = new ArrayList<>();
        if (ObjectUtils.isEmpty(pGAccounts)) {
            return lvDTOs;
        }
        for (GroupAccount lvGAccount : pGAccounts) {
            lvDTOs.add(toDTO(lvGAccount));
        }
        return lvDTOs;
    }
}