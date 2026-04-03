package com.flowiee.pms.system.repository;

import com.flowiee.pms.shared.base.BaseRepository;
import com.flowiee.pms.system.enums.SerialCode;
import com.flowiee.pms.system.entity.Serial;
import org.springframework.stereotype.Repository;

@Repository
public interface SerialRepository extends BaseRepository<Serial, String> {
    Serial findById(SerialCode serialCode);
}