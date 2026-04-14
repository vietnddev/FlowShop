package com.flowiee.pms.system.service.impl;

import com.flowiee.pms.system.enums.SerialCode;
import com.flowiee.pms.system.entity.Serial;
import com.flowiee.pms.system.repository.SerialRepository;
import com.flowiee.pms.system.service.SerialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SerialServiceImpl implements SerialService {
    SerialRepository mvSerialRepository;

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