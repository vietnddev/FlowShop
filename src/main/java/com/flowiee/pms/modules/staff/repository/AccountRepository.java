package com.flowiee.pms.modules.staff.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.staff.entity.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    @EntityGraph(value = "Account.withImages", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Account> findById(Long id);

    @Query("from Account a where a.username=:username")
    Account findByUsername(@Param("username") String username);

    Account findByEmail(String email);

    Account findByResetTokens(String token);
}