package com.flowiee.pms.modules.sales.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.modules.sales.entity.OrderReturn;
import org.springframework.data.jpa.repository.Query;

public interface OrderReturnRepository extends BaseRepository<OrderReturn, Long> {
    @Query("select o.returnsCode from OrderReturn o order by o.id desc fetch first 1 rows only")
    String findLastestCode();
}