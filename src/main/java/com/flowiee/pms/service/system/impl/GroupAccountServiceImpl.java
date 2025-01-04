package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.entity.system.GroupAccount;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.common.ChangeLog;
import com.flowiee.pms.repository.system.GroupAccountRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.system.GroupAccountService;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupAccountServiceImpl extends BaseService implements GroupAccountService {
    GroupAccountRepository mvGroupAccountRepository;

    @Override
    public List<GroupAccount> findAll() {
        return this.findAll(-1, -1).getContent();
    }

    @Override
    public Page<GroupAccount> findAll(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("groupName").ascending());
        return mvGroupAccountRepository.findAll(pageable);
    }

    @Override
    public GroupAccount findById(Long groupId, boolean pThrowException) {
        Optional<GroupAccount> groupAccount = mvGroupAccountRepository.findById(groupId);
        if (groupAccount.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"group account"}, null, null);
        }
        return groupAccount.orElse(null);
    }

    @Override
    public GroupAccount save(GroupAccount groupAccount) {
        return mvGroupAccountRepository.save(groupAccount);
    }

    @Override
    public GroupAccount update(GroupAccount groupAccount, Long groupId) {
        GroupAccount groupAccountOpt = this.findById(groupId, true);

        GroupAccount groupAccountBefore = ObjectUtils.clone(groupAccountOpt);

        groupAccount.setId(groupId);
        GroupAccount groupAccountUpdated = mvGroupAccountRepository.save(groupAccount);

        ChangeLog changeLog = new ChangeLog(groupAccountBefore, groupAccountUpdated);
        systemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_GR_ACC_U, MasterObject.GroupAccount, "Cập nhật thông tin nhóm người dùng", changeLog.getOldValues(), changeLog.getNewValues());

        return groupAccountUpdated;
    }

    @Override
    public String delete(Long groupId) {
        if (this.findById(groupId, true) == null) {
            throw new BadRequestException();
        }
        mvGroupAccountRepository.deleteById(groupId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}