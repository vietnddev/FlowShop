package com.flowiee.pms.common.base.service;

import com.flowiee.pms.common.base.service.BaseService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseImportExportService extends BaseService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected XSSFSheet mvDataSheet;
    protected String mvDataSheetName = "data";
    protected int mvHeadKeyLine = 1;
    protected int mvDataBeginLine = 3;

    protected XSSFRow getRowHeadKey() {
        return mvDataSheet.getRow(mvHeadKeyLine);
    }
}