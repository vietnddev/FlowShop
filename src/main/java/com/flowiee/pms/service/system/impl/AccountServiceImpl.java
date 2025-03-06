package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.common.utils.ChangeLog;
import com.flowiee.pms.common.constants.Constants;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.PasswordUtils;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.exception.*;
import com.flowiee.pms.repository.system.AccountRepository;
import com.flowiee.pms.security.UserSession;
import com.flowiee.pms.service.system.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends BaseService implements AccountService {
    private final AccountRepository mvAccountRepository;
    private final UserSession mvUserSession;

    @Override
    public Account findById(Long accountId, boolean pThrowException) {
        Optional<Account> entityOptional = mvAccountRepository.findById(accountId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"account"}, null, null);
        }
        return entityOptional.orElse(null);
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
        if (mvAccountRepository.findByUsername(username) != null)
            throw new DataExistsException(String.format("Username %s existed!", username));
        if (!CoreUtils.validateEmail(email))
            throw new DataExistsException(String.format("Email %s invalid!", email));
        if (mvAccountRepository.findByEmail(email) != null)
            throw new DataExistsException(String.format("Email %s existed!", email));

        account.setRole(Constants.ADMINISTRATOR.equals(lvRole) ? "ADMIN" : "USER");
        account.setPassword(PasswordUtils.encodePassword(lvPassword));
        Account accountSaved = mvAccountRepository.save(account);

        systemLogService.writeLogCreate(MODULE.SYSTEM, ACTION.SYS_ACC_C, MasterObject.Account, "Thêm mới account", username);
        logger.info("Insert account success! username={}", username);

        return accountSaved;
    }

    @Transactional
    @Override
    public Account update(Account account, Long entityId) {
        Account accountOpt = this.findById(entityId, true);

        ChangeLog changeLog = new ChangeLog(ObjectUtils.clone(accountOpt));

        accountOpt.setPhoneNumber(account.getPhoneNumber());
        accountOpt.setEmail(account.getEmail());
        accountOpt.setAddress(account.getAddress());
        accountOpt.setGroupAccount(account.getGroupAccount());
        accountOpt.setBranch(account.getBranch());
        accountOpt.setRole(Constants.ADMINISTRATOR.equals(account.getRole()) ? "ADMIN" : "USER");
        Account accountUpdated = mvAccountRepository.save(accountOpt);

        changeLog.setNewObject(accountUpdated);
        changeLog.doAudit();

        systemLogService.writeLogUpdate(MODULE.SYSTEM, ACTION.SYS_ACC_U, MasterObject.Account, "Cập nhật tài khoản " + accountUpdated.getUsername(), changeLog);
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
        return mvAccountRepository.save(lvProfile);
    }

    @Transactional
    @Override
    public String delete(Long accountId) {
        try {
            Optional<Account> account = mvAccountRepository.findById(accountId);
            if (account.isPresent()) {
                mvAccountRepository.delete(account.get());
                systemLogService.writeLogDelete(MODULE.SYSTEM, ACTION.SYS_ACC_D, MasterObject.Account, "Xóa account", account.get().getUsername());
                logger.info("Delete account success! username={}", account.get().getUsername());
            }
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (Exception ex) {
            throw new AppException("Delete account fail! id=" + accountId, ex);
        }
    }

    @Override
    public List<Account> findAll() {
        return mvAccountRepository.findAll();
    }

    @Override
    public Account findByUsername(String username) {
        return mvAccountRepository.findByUsername(username);
    }
}