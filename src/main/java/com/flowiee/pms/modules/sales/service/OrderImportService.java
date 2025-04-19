package com.flowiee.pms.modules.sales.service;

import com.flowiee.pms.common.base.service.BaseImportService;
import org.springframework.stereotype.Service;

@Service
public class OrderImportService extends BaseImportService {
    @Override
    protected void writeData() {

    }

    @Override
    public String approveData() {
        return null;
    }
}