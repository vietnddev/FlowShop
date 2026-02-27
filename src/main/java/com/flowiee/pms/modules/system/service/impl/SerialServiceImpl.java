package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.enumeration.SerialCode;
import com.flowiee.pms.modules.system.entity.Serial;
import com.flowiee.pms.modules.system.repository.SerialRepository;
import com.flowiee.pms.modules.system.service.SerialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SerialServiceImpl implements SerialService {
    SerialRepository mvSerialRepository;

    @Transactional
    @Override
    public String getNextSerial(SerialCode serialCode) {
        Serial lvSerial = mvSerialRepository.findById(serialCode);
        if (lvSerial == null) {
            throw  new RuntimeException("serial not found");
        }

        String lvPrefix = lvSerial.getPrefix();
        int lvCurrentValue = lvSerial.getCurrentValue();
        int lvNewValue = lvCurrentValue + 1;
        String lvNextSerial = lvPrefix + lvNewValue;

        lvSerial.setCurrentValue(lvNewValue);
        mvSerialRepository.save(lvSerial);

        return lvNextSerial;
    }
}