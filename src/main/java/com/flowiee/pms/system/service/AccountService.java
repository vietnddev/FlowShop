package com.flowiee.pms.system.service;

import com.flowiee.pms.shared.base.DeleteService;
import com.flowiee.pms.system.dto.AccountDTO;
import com.flowiee.pms.system.entity.Account;

import java.util.List;

public interface AccountService extends DeleteService {
    Account findByUsername(String username);

    List<AccountDTO> find();

    Account findEntById(Long pAccountId, boolean pThrowException);

    AccountDTO findDtoById(Long pAccountId, boolean pThrowException);

    Account save(Account pAccount);

    Account update(Account pAccount, Long pAccountId);

    Account updateProfile(Account pAccount);

    AccountDTO getMyProfile();

    void changePassword(Long pAccountId, String pOldPassword, String pNewPassword);
}