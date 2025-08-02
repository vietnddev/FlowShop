package com.flowiee.pms.modules.staff.service;

import com.flowiee.pms.modules.staff.dto.AccountDTO;
import com.flowiee.pms.modules.staff.entity.Account;

import java.util.List;

public interface AccountService {
    Account findByUsername(String username);

    List<AccountDTO> find();

    Account findEntById(Long pAccountId, boolean pThrowException);

    AccountDTO findDtoById(Long pAccountId, boolean pThrowException);

    Account save(Account pAccount);

    Account update(Account pAccount, Long pAccountId);

    Account updateProfile(Account pAccount);

    String delete(Long pAccountId);

    AccountDTO getMyProfile();
}