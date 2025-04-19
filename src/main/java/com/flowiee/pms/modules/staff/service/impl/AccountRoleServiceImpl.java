package com.flowiee.pms.modules.staff.service.impl;

import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.utils.SysConfigUtils;
import com.flowiee.pms.modules.staff.model.ActionModel;
import com.flowiee.pms.modules.system.model.ModuleModel;
import com.flowiee.pms.modules.staff.model.RoleModel;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.entity.AccountRole;
import com.flowiee.pms.modules.system.entity.SystemLog;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.staff.dto.GroupAccountDTO;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.modules.staff.repository.AccountRoleRepository;
import com.flowiee.pms.modules.staff.service.RoleService;

import com.flowiee.pms.modules.staff.service.GroupAccountService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl implements RoleService {
    private static Map<String, List<String>> mvRightsAreGrantedInRuntime = new HashMap<>();

    private final AccountRepository     mvAccountRepository;
    private final GroupAccountService mvGroupAccountService;
    private final AccountRoleRepository mvAccountRoleRepository;
    private final SystemLogService systemLogService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<RoleModel> findAllRoleByAccountId(Long accountId) {
        Optional<Account> account = mvAccountRepository.findById(accountId);
        if (account.isEmpty()) {
            return List.of();
        }
        List<RoleModel> listReturn = new ArrayList<>();
        for (ACTION act : ACTION.values()) {
            listReturn.add(initRoleModel(null, accountId, act.getModuleKey(), act.getModuleLabel(), act.name(), act.getActionLabel()));
        }
        return listReturn;
    }

    @Override
    public List<RoleModel> findAllRoleByGroupId(Long groupId) {
        GroupAccountDTO lvGroupAcc = mvGroupAccountService.findById(groupId, false);
        if (lvGroupAcc == null) {
            return List.of();
        }
        List<RoleModel> listReturn = new ArrayList<>();
        for (ACTION act : ACTION.values()) {
            listReturn.add(initRoleModel(groupId, null, act.getModuleKey(), act.getModuleLabel(), act.name(), act.getActionLabel()));
        }
        return listReturn;
    }

    @Override
    public List<ActionModel> findAllAction() {
        List<ActionModel> listAction = new ArrayList<>();
        for (ACTION sysAction : ACTION.values()) {
            listAction.add(ActionModel.builder()
                    .actionKey(sysAction.getActionKey())
                    .actionLabel(sysAction.getActionLabel())
                    .moduleKey(sysAction.getModuleKey())
                    .build());
        }
        return listAction;
    }

    @Override
    public AccountRole findById(Long id, boolean pThrowException) {
        Optional<AccountRole> entityOptional = mvAccountRoleRepository.findById(id);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"role"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public List<AccountRole> findByAccountId(Long accountId) {
        return mvAccountRoleRepository.findByAccountId(accountId);
    }

    @Override
    public List<AccountRole> findByGroupId(Long accountId) {
        return mvAccountRoleRepository.findByGroupId(accountId);
    }

    private List<String> getListRights1(List<ActionModel> pActionModelList) {
        return pActionModelList.stream()
                .map(ActionModel::getActionKey)
                .collect(Collectors.toList());
    }

    private List<String> getListRights2(List<AccountRole> pRightsModelList) {
        return pRightsModelList.stream()
                .map(AccountRole::getAction)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public String updatePermission(Account pAccount, List<ActionModel> pActionModelList) {
        long lvAccountId = pAccount.getId();
        String lvUsername = pAccount.getUsername();

        List<String> lvRightsAddedList = new ArrayList<>();
        List<String> lvRightsRemovedList = new ArrayList<>();
        List<AccountRole> lvCurrentRightsList = mvAccountRoleRepository.findByAccountId(lvAccountId);

        if (lvCurrentRightsList.isEmpty()) {
            lvRightsAddedList.clear();
            lvRightsAddedList.addAll(getListRights1(pActionModelList));
        } else {
            List<String> lvCurrentRightsStringList = getListRights2(lvCurrentRightsList);
            lvRightsAddedList.addAll(
                    pActionModelList.stream()
                            .map(ActionModel::getActionKey)
                            .filter(lvRequestRightsKey -> !lvCurrentRightsStringList.contains(lvRequestRightsKey))
                            .toList()
            );
        }

        if (pActionModelList.isEmpty()) {
            lvRightsRemovedList.clear();
            lvRightsRemovedList.addAll(getListRights2(lvCurrentRightsList));
        } else {
            List<String> lvRequestRightsStringList = getListRights1(pActionModelList);
            lvRightsRemovedList.addAll(
                    lvCurrentRightsList.stream()
                            .map(AccountRole::getAction)
                            .filter(lvCurrentRightsKey -> !lvRequestRightsStringList.contains(lvCurrentRightsKey))
                            .toList());
        }

        for (String lvRightsRemoved : lvRightsRemovedList)
            mvAccountRoleRepository.deleteByActionAndAccountId(lvRightsRemoved, lvAccountId);

        for (String lvRightsAdded : lvRightsAddedList)
            mvAccountRoleRepository.save(AccountRole.builder()
                    .module(ACTION.get(lvRightsAdded).getModuleKey())
                    .action(lvRightsAdded)
                    .accountId(lvAccountId)
                    .build());

        if (SysConfigUtils.isYesOption(ConfigCode.forceApplyAccountRightsNoNeedReLogin))
            addTempRights(lvUsername, lvRightsAddedList);

        String lvLogTitle = "Cập nhật quyền của tài khoản " + lvUsername;
        String lvLogContent = SystemLog.EMPTY;
        if (!lvRightsAddedList.isEmpty()) {
            lvLogContent += "Cấp quyền: (" + lvRightsAddedList.size() + ") " + lvRightsAddedList.toString().replaceAll("[\\[\\]]", "").trim() + "\n</br>";
        }
        if (!lvRightsRemovedList.isEmpty()) {
            lvLogContent += "Thu quyền: (" + lvRightsRemovedList.size() + ") " + lvRightsRemovedList.toString().replaceAll("[\\[\\]]", "").trim();
        }
        systemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_ROLE_U, MasterObject.AccountRole, lvLogTitle, lvLogContent);

        return MessageCode.UPDATE_SUCCESS.getDescription();
    }

    @Override
    public boolean isAuthorized(long accountId, String module, String action) {
        return mvAccountRoleRepository.isAuthorized(null, accountId, module, action) != null;
    }

    @Override
    public String deleteAllRole(Long groupId, Long accountId) {
        if (groupId == null && accountId == null) {
            throw new IllegalArgumentException("groupId and accountId cannot be null");
        }
        mvAccountRoleRepository.deleteByAccountId(accountId);
        mvAccountRoleRepository.deleteByGroupAccountId(groupId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Long groupId) {
        this.deleteAllRole(groupId, null);
        List<RoleModel> list = new ArrayList<>();
        if (ObjectUtils.isEmpty(rights)) {
            return list;
        }
        for (RoleModel role : rights) {
            if (ObjectUtils.isEmpty(role)) {
                return list;
            }
            if (role.getIsAuthor() == null || !role.getIsAuthor()) {
                continue;
            }
            mvAccountRoleRepository.save(AccountRole.builder()
                    .module(role.getModule().getModuleKey())
                    .action(role.getAction().getActionKey())
                    .groupId(groupId)
                    .build());
            list.add(role);
        }
        return list;
    }

    @Override
    public List<AccountRole> findByAction(ACTION action) {
        return mvAccountRoleRepository.findByModuleAndAction(action.getModuleKey(), action.getActionKey());
    }

    private RoleModel initRoleModel(Long pGroupId, Long pAccountId, String pModuleKey, String pModuleLabel, String pActionKey, String pActionLabel) {
        AccountRole lvAccountRole = null;
        try {
            lvAccountRole = mvAccountRoleRepository.isAuthorized(pGroupId, pAccountId, pModuleKey, pActionKey);
        } catch (RuntimeException ex) {
            System.out.println("pGroupId " + pGroupId);
            System.out.println("pAccountId " + pAccountId);
            System.out.println("pModuleKey " + pModuleKey);
            System.out.println("pActionKey " + pActionKey);
            ex.printStackTrace();
        }

        return RoleModel.builder()
                .module(ModuleModel.builder().moduleKey(pModuleKey).moduleLabel(pModuleLabel).build())
                .action(ActionModel.builder().moduleKey(pModuleKey).actionKey(pActionKey).actionLabel(pActionLabel).build())
                .isAuthor(lvAccountRole != null)
                .groupId(pGroupId)
                .accountId(pAccountId)
                .build();
    }

    @Override
    public boolean checkTempRights(String pAccountId, String pRight) {
        if (pAccountId == null || pRight == null) {
            return false;
        }
        List<String> lvRights = mvRightsAreGrantedInRuntime.get(pAccountId);
        if (CollectionUtils.isEmpty(lvRights)) {
            return false;
        }
        for (String lvRight : lvRights) {
            if (lvRight.equals(pRight)) {
                return true;
            }
        }
        return false;
    }

    public void addTempRights(String pAccountId, List<String> pRights) {
        mvRightsAreGrantedInRuntime.remove(pAccountId);
        mvRightsAreGrantedInRuntime.put(pAccountId, pRights);
    }
}