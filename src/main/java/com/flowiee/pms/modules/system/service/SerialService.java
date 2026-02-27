package com.flowiee.pms.modules.system.service;

import com.flowiee.pms.common.enumeration.SerialCode;

public interface SerialService {
    String getNextSerial(SerialCode serialCode);
}