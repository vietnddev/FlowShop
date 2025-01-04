package com.flowiee.pms.repository.system;

import com.flowiee.pms.entity.system.GroupAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupAccountRepository extends JpaRepository<GroupAccount, Long> {
    @Query("from GroupAccount where groupCode = :groupCode")
    GroupAccount findByCode(@Param("groupCode") String pGroupCode);
}