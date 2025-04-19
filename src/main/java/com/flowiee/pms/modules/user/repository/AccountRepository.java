package com.flowiee.pms.modules.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowiee.pms.modules.user.entity.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @EntityGraph(value = "Account.withBranchAndGroupAccount", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Account> findById(Long id);

    @Query("from Account a where a.username=:username")
    Account findByUsername(@Param("username") String username);

    Account findByEmail(String email);

    Account findByResetTokens(String token);
}