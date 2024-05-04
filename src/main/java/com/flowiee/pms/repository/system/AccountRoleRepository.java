package com.flowiee.pms.repository.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flowiee.pms.entity.system.AccountRole;

import java.util.List;

@Transactional
@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {
    List<AccountRole> findByAccountId(Integer accountId);

    List<AccountRole> findByGroupId(Integer accountId);

    @Query("from AccountRole where accountId=:accountId and module=:module and action=:action")
    AccountRole isAuthorized(@Param("accountId") Integer accountId, @Param("module") String module, @Param("action") String action);

    @Modifying
    void deleteByAccountId(Integer accountId);
}