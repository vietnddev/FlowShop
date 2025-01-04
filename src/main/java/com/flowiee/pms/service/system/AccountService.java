package com.flowiee.pms.service.system;

import com.flowiee.pms.entity.system.Account;

import java.util.List;

public interface AccountService {
    Account findByUsername(String username);

    List<Account> findAll();

    Account findById(Long pAccountId, boolean pThrowException);

    Account save(Account pAccount);

    Account update(Account pAccount, Long pAccountId);

    String delete(Long pAccountId);
}