package com.flowiee.pms.order.service;

import com.flowiee.pms.shared.base.BaseImportService;
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