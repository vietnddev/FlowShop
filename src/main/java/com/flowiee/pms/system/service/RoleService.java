package com.flowiee.pms.system.service;

import com.flowiee.pms.system.entity.Account;
import com.flowiee.pms.system.entity.AccountRole;
import com.flowiee.pms.shared.enums.ACTION;
import com.flowiee.pms.system.model.ActionModel;
import com.flowiee.pms.system.model.RoleModel;

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

    boolean deleteAllRole(Long groupId, Long accountId);

    List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Long groupId);

    List<AccountRole> findByAction(ACTION action);

    boolean checkTempRights(String pAccountId, String pRight);
}