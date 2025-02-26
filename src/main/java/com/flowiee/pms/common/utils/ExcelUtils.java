package com.flowiee.pms.common.utils;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class ExcelUtils {
    public static void createDropdownList(XSSFWorkbook pWorkbook, XSSFSheet sheet,
                                          XSSFSheet hsheet, //Sheet ẩn chứa dữ liệu danh mục
                                          List<String> pListValue, //["Apple", "Banana", "Cherry"]
                                          int pRowSelectionBegin, int pRowSelectionEnd, int pColumn,
                                          String nameName //Tên vùng dữ liệu danh mục trên sheet ẩn
    ) {
        //Put các tên danh mục vào column trong sheet danh mục ẩn
        for (int i = 0; i < pListValue.size(); i++) {
            XSSFRow hideRow = hsheet.getRow(i);
            if (hideRow == null) {
                hideRow = hsheet.createRow(i);
            }
            hideRow.createCell(pColumn).setCellValue(pListValue.get(i));
        }

        // Khởi tạo name cho mỗi loại danh mục
        Name namedRange = pWorkbook.createName();
        namedRange.setNameName(nameName);
        String colName = CellReference.convertNumToColString(pColumn);
        namedRange.setRefersToFormula(hsheet.getSheetName() + "!$" + colName + "$1:$" + colName + "$" + pListValue.size());

        sheet.autoSizeColumn(pColumn); //Auto điều chỉnh độ rộng cột

        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        CellRangeAddressList addressList = new CellRangeAddressList(pRowSelectionBegin, pRowSelectionEnd, pColumn, pColumn); //Tạo dropdownlist cho một cell
        DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(nameName);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        dataValidation.setSuppressDropDownArrow(true); //Hiển thị mũi tên xổ xuống để chọn giá trị
        dataValidation.setShowErrorBox(true); //Hiển thị hộp thoại lỗi khi chọn giá trị không hợp lệ
        dataValidation.createErrorBox("Error", "Giá trị đã chọn không hợp lệ!");
        dataValidation.setEmptyCellAllowed(false); //Không cho phép ô trống trong dropdownlist
        dataValidation.setShowPromptBox(true); //Hiển thị hộp nhắc nhở khi người dùng chọn ô
        dataValidation.createPromptBox("Danh mục hệ thống", "Vui lòng chọn giá trị!"); //Tạo hộp thoại nhắc nhở khi click chuột vào cell

        sheet.addValidationData(dataValidation);
    }
}