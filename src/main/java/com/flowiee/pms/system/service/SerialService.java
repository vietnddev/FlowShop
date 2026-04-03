package com.flowiee.pms.system.service;

import com.flowiee.pms.system.enums.SerialCode;

public interface SerialService {
    String getNextSerial(SerialCode serialCode);
}