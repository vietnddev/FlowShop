package com.flowiee.pms.service.system;

import com.flowiee.pms.entity.system.AccountRole;
import com.flowiee.pms.model.role.ActionModel;
import com.flowiee.pms.model.role.RoleModel;

import java.util.List;

public interface RoleService {
    List<RoleModel> findAllRoleByAccountId(Integer accountId);

    List<ActionModel> findAllAction();

    AccountRole findById(Integer id);

    List<AccountRole> findByAccountId(Integer accountId);

    List<AccountRole> findByGroupId(Integer accountId);

    String updatePermission(String moduleKey, String actionKey, Integer accountId);

    boolean isAuthorized(int accountId, String module, String action);

    String deleteAllRole(Integer accountId);
}