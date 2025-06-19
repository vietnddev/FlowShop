package com.flowiee.pms.modules.staff.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.staff.entity.GroupAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupAccountRepository extends BaseRepository<GroupAccount, Long> {
    @Query("from GroupAccount where groupCode = :groupCode")
    GroupAccount findByCode(@Param("groupCode") String pGroupCode);
}