package com.flowiee.pms.modules.system.repository;

import com.flowiee.pms.common.base.repository.BaseRepository;
import com.flowiee.pms.common.enumeration.SerialCode;
import com.flowiee.pms.modules.system.entity.Serial;
import org.springframework.stereotype.Repository;

@Repository
public interface SerialRepository extends BaseRepository<Serial, String> {
    Serial findById(SerialCode serialCode);
}