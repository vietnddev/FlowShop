package com.flowiee.pms.service.system;

import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.AccountRole;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.model.role.ActionModel;
import com.flowiee.pms.model.role.RoleModel;

import java.util.List;

public interface RoleService {
    List<RoleModel> findAllRoleByAccountId(Long accountId);

    List<RoleModel> findAllRoleByGroupId(Long groupId);

    List<ActionModel> findAllAction();

    AccountRole findById(Long id, boolean pThrowException);

    List<AccountRole> findByAccountId(Long accountId);

    List<AccountRole> findByGroupId(Long accountId);

    String updatePermission(Account accountId, List<ActionModel> actionModelList);

    boolean isAuthorized(long accountId, String module, String action);

    String deleteAllRole(Long groupId, Long accountId);

    List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Long groupId);

    List<AccountRole> findByAction(ACTION action);

    boolean checkTempRights(String pAccountId, String pRight);
}