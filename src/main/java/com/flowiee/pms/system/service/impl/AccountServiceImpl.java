package com.flowiee.pms.system.service.impl;

import com.flowiee.pms.shared.base.BaseService;
import com.flowiee.pms.shared.request.BaseParameter;
import com.flowiee.pms.shared.util.*;
import com.flowiee.pms.shared.constant.Constants;
import com.flowiee.pms.media.entity.FileStorage;
import com.flowiee.pms.shared.enums.*;
import com.flowiee.pms.shared.exception.AppException;
import com.flowiee.pms.shared.exception.BadRequestException;
import com.flowiee.pms.shared.exception.DataExistsException;
import com.flowiee.pms.system.dto.AccountDTO;
import com.flowiee.pms.system.entity.Account;
import com.flowiee.pms.system.enums.AccountStatus;
import com.flowiee.pms.system.repository.AccountRepository;
import com.flowiee.pms.system.service.AccountService;
import com.flowiee.pms.system.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AccountServiceImpl extends BaseService<Account, AccountDTO, AccountRepository> implements AccountService {
    private final SystemLogService mvSystemLogService;

    public AccountServiceImpl(AccountRepository pEntityRepository, SystemLogService pSystemLogService) {
        super(Account.class, AccountDTO.class, pEntityRepository);
        this.mvSystemLogService = pSystemLogService;
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
        account.setFailLogonCount(0);
        account.setStatus(AccountStatus.N.name());
        Account accountSaved = mvEntityRepository.save(account);

        mvSystemLogService.writeLogCreate(MODULE.SYSTEM, ACTION.SYS_ACC_C, MasterObject.Account, "Thêm mới account", username);
        log.info("Insert account success! username={}", username);

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
        log.info("Update account success! username={}", accountUpdated.getUsername());

        return accountUpdated;
    }

    @Override
    public Account updateProfile(Account pAccount) {
        Account lvProfile = SecurityUtils.getCurrentUser().getEntity();
        lvProfile.setPhoneNumber(pAccount.getPhoneNumber());
        lvProfile.setEmail(pAccount.getEmail());
        lvProfile.setAddress(pAccount.getAddress());
        //... more field
        return mvEntityRepository.save(lvProfile);
    }

    @Transactional
    @Override
    public boolean delete(Long accountId) {
        try {
            Optional<Account> account = mvEntityRepository.findById(accountId);
            if (account.isPresent()) {
                mvEntityRepository.delete(account.get());
                mvSystemLogService.writeLogDelete(MODULE.SYSTEM, ACTION.SYS_ACC_D, MasterObject.Account, "Xóa account", account.get().getUsername());
                log.info("Delete account success! username={}", account.get().getUsername());
            }
            return true;
        } catch (Exception ex) {
            throw new AppException("Delete account fail! id=" + accountId, ex);
        }
    }

    @Override
    public AccountDTO getMyProfile() {
        Account lvEntity = mvEntityRepository.findById(SecurityUtils.getCurrentUser().getId())
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
    public List<AccountDTO>find() {
        return super.find(BaseParameter.builder().build()).stream()
                .peek(a -> {
                    a.setPassword(null);
                    a.setAvatar("/media/default/user");
                }).toList();
    }

    @Override
    public Account findByUsername(String username) {
        return mvEntityRepository.findByUsername(username);
    }

    @org.springframework.transaction.annotation.Transactional
    @Override
    public void changePassword(Long pAccountId, String pOldPassword, String pNewPassword) {
        Account lvAccount = findEntById(pAccountId, true);

        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        if (!bCrypt.matches(pOldPassword, lvAccount.getPassword())) {
            throw new AppException("Old password is not correct!");
        }

        mvEntityRepository.updatePassword(lvAccount.getId(), bCrypt.encode(pNewPassword));

        mvSystemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_ACC_CH_PWD, MasterObject.Account, "Change password", "Change password");
    }
}