package com.flowiee.pms.modules.user.service;

import com.flowiee.pms.modules.user.entity.Account;

import java.util.List;

public interface AccountService {
    Account findByUsername(String username);

    List<Account> findAll();

    Account findById(Long pAccountId, boolean pThrowException);

    Account save(Account pAccount);

    Account update(Account pAccount, Long pAccountId);

    Account updateProfile(Account pAccount);

    String delete(Long pAccountId);
}