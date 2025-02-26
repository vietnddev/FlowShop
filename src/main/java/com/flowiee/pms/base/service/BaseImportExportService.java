package com.flowiee.pms.base.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class BaseImportExportService extends BaseService {
    protected XSSFSheet mvDataSheet;
    protected String mvDataSheetName = "data";
    protected int mvHeadKeyLine = 1;
    protected int mvDataBeginLine = 3;

    protected XSSFRow getRowHeadKey() {
        return mvDataSheet.getRow(mvHeadKeyLine);
    }
}