package com.flowiee.pms.modules.staff.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.FileUtils;
import com.flowiee.pms.common.utils.PasswordUtils;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.staff.dto.AccountDTO;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.common.security.UserSession;
import com.flowiee.pms.modules.staff.service.AccountService;
import com.flowiee.pms.modules.system.service.SystemLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl extends BaseService<Account, AccountDTO, AccountRepository> implements AccountService {
    private final SystemLogService mvSystemLogService;
    private final UserSession mvUserSession;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public AccountServiceImpl(AccountRepository pEntityRepository, SystemLogService pSystemLogService, UserSession pUserSession) {
        super(Account.class, AccountDTO.class, pEntityRepository);
        this.mvSystemLogService = pSystemLogService;
        this.mvUserSession = pUserSession;
    }

    @Override
    public Account findEntById(Long accountId, boolean pThrowException) {
        return super.findEntById(accountId, pThrowException);
    }

    @Override
    public AccountDTO findDtoById(Long pAccountId, boolean pThrowException) {
        return super.findDtoById(pAccountId, pThrowException);
    }

    @Override
    public Account save(Account account) {
        String name = account.getFullName();
        String username = account.getUsername();
        String email = account.getEmail();
        String lvRole = account.getRole();
        String lvPassword = account.getPassword();

        if (CoreUtils.isAnySpecialCharacter(name))
            throw new BadRequestException(String.format("Account name can't allow include special characters!", name));
        if (mvEntityRepository.findByUsername(username) != null)
            throw new DataExistsException(String.format("Username %s existed!", username));
        if (!CoreUtils.validateEmail(email))
            throw new DataExistsException(String.format("Email %s invalid!", email));
        if (mvEntityRepository.findByEmail(email) != null)
            throw new DataExistsException(String.format("Email %s existed!", email));

        account.setRole(Constants.ADMINISTRATOR.equals(lvRole) ? "ADMIN" : "USER");
        account.setPassword(PasswordUtils.encodePassword(lvPassword));
        Account accountSaved = mvEntityRepository.save(account);

        mvSystemLogService.writeLogCreate(MODULE.SYSTEM, ACTION.SYS_ACC_C, MasterObject.Account, "Thêm mới account", username);
        logger.info("Insert account success! username={}", username);

        return accountSaved;
    }

    @Transactional
    @Override
    public Account update(Account account, Long entityId) {
        Account accountOpt = this.findEntById(entityId, true);

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(accountOpt));

        accountOpt.setPhoneNumber(account.getPhoneNumber());
        accountOpt.setEmail(account.getEmail());
        accountOpt.setAddress(account.getAddress());
        accountOpt.setGroupAccount(account.getGroupAccount());
        accountOpt.setBranch(account.getBranch());
        accountOpt.setRole(Constants.ADMINISTRATOR.equals(account.getRole()) ? "ADMIN" : "USER");
        Account accountUpdated = mvEntityRepository.save(accountOpt);

        changeLog.setNewObject(accountUpdated);
        changeLog.doAudit();

        mvSystemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_ACC_U, MasterObject.Account, "Cập nhật tài khoản " + accountUpdated.getUsername(), changeLog);
        logger.info("Update account success! username={}", accountUpdated.getUsername());

        return accountUpdated;
    }

    @Override
    public Account updateProfile(Account pAccount) {
        Account lvProfile = mvUserSession.getUserPrincipal().getEntity();
        lvProfile.setPhoneNumber(pAccount.getPhoneNumber());
        lvProfile.setEmail(pAccount.getEmail());
        lvProfile.setAddress(pAccount.getAddress());
        //... more field
        return mvEntityRepository.save(lvProfile);
    }

    @Transactional
    @Override
    public String delete(Long accountId) {
        try {
            Optional<Account> account = mvEntityRepository.findById(accountId);
            if (account.isPresent()) {
                mvEntityRepository.delete(account.get());
                mvSystemLogService.writeLogDelete(MODULE.SYSTEM, ACTION.SYS_ACC_D, MasterObject.Account, "Xóa account", account.get().getUsername());
                logger.info("Delete account success! username={}", account.get().getUsername());
            }
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (Exception ex) {
            throw new AppException("Delete account fail! id=" + accountId, ex);
        }
    }

    @Override
    public AccountDTO getMyProfile() {
        Account lvEntity = mvEntityRepository.findById(mvUserSession.getUserPrincipal().getId())
                .orElseThrow(() -> new BadRequestException("Invalid information!"));
        String lvAvt = lvEntity.getListAvatar().stream()
                .filter(FileStorage::isActive)
                .findFirst()
                .map(i -> FileUtils.getImageUrl(i, true))
                .orElse("/media/default/user");

        AccountDTO lvDto = new AccountDTO();
        lvDto.setId(lvEntity.getId());
        lvDto.setUsername(lvEntity.getUsername());
        lvDto.setFullName(lvEntity.getFullName());
        lvDto.setEmail(lvEntity.getEmail());
        lvDto.setPhoneNumber(lvEntity.getPhoneNumber());
        lvDto.setSex(lvEntity.isSex());
        lvDto.setStatus(lvEntity.getStatus());
        lvDto.setAvatar(lvAvt);
        //more fields

        return lvDto;
    }

    @Override
    public List<AccountDTO> findAll() {
        return super.findAll().stream()
                .peek(a -> {
                    a.setPassword(null);
                    a.setAvatar("/media/default/user");
                }).toList();
    }

    @Override
    public Account findByUsername(String username) {
        return mvEntityRepository.findByUsername(username);
    }
}