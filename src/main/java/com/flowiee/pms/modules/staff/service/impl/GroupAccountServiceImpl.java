package com.flowiee.pms.modules.staff.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.staff.entity.GroupAccount;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.modules.staff.dto.GroupAccountDTO;
import com.flowiee.pms.modules.staff.repository.GroupAccountRepository;
import com.flowiee.pms.common.enumeration.ACTION;
import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.MasterObject;
import com.flowiee.pms.modules.system.service.SystemLogService;
import com.flowiee.pms.modules.staff.service.GroupAccountService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupAccountServiceImpl extends BaseService<GroupAccount, GroupAccountDTO, GroupAccountRepository> implements GroupAccountService {
    private final SystemLogService mvSystemLogService;

    public GroupAccountServiceImpl(GroupAccountRepository pEntityRepository, SystemLogService pSystemLogService) {
        super(GroupAccount.class, GroupAccountDTO.class, pEntityRepository);
        this.mvSystemLogService = pSystemLogService;
    }

    @Override
    public List<GroupAccountDTO> findAll() {
        return this.findAll(-1, -1).getContent();
    }

    @Override
    public Page<GroupAccountDTO> findAll(int pageSize, int pageNum) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("groupName").ascending());
        Page<GroupAccount> groupAccountPage = mvEntityRepository.findAll(pageable);
        return new PageImpl<>(convertDTOs(groupAccountPage.getContent()), pageable, groupAccountPage.getTotalElements());
    }

    @Override
    public GroupAccountDTO findById(Long groupId, boolean pThrowException) {
        return super.findDtoById(groupId, pThrowException);
    }

    @Override
    public GroupAccountDTO save(GroupAccountDTO pGroupAccount) {
        return super.save(pGroupAccount);
    }

    @Override
    public GroupAccountDTO update(GroupAccountDTO pGroupAccount, Long groupId) {
        GroupAccount lvGroupAccount = super.findById(groupId).orElseThrow(() -> new BadRequestException());

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(lvGroupAccount));

        pGroupAccount.setId(groupId);
        GroupAccount groupAccountUpdated = mvEntityRepository.save(lvGroupAccount);

        changeLog.setOldObject(groupAccountUpdated);
        changeLog.doAudit();

        mvSystemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_GR_ACC_U, MasterObject.GroupAccount, "Cập nhật thông tin nhóm người dùng", changeLog.getOldValues(), changeLog.getNewValues());

        return super.convertDTO(groupAccountUpdated);
    }

    @Override
    public String delete(Long pGroupId) {
        return super.delete(pGroupId);
    }
}