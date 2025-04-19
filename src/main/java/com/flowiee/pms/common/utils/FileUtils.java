package com.flowiee.pms.common.utils;

import com.flowiee.pms.common.base.StartUp;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.FileExtension;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class FileUtils {
    public static String resourceStaticPath = "src/main/resources/static";
    public static String fileUploadPath = StartUp.getResourceUploadPath() + "/uploads/";
    public static String initCsvDataPath = resourceStaticPath + "/data/csv";
    public static String initXlsxDataPath = resourceStaticPath + "/data/excel";
    public static String reportTemplatePath = resourceStaticPath + "/report";
    public static String excelTemplatePath = resourceStaticPath + "/templates/excel";
    public static Path logoPath = Paths.get(resourceStaticPath + "/dist/img/FlowieeLogo.png");

    public static void createCellCombobox(XSSFWorkbook workbook, XSSFSheet sheet, XSSFSheet hsheet, List<String> listValue, int row, int column, String nameName) {
        //Put các tên danh mục vào column trong sheet danh mục ẩn
        for (int i = 0; i < listValue.size(); i++) {
            XSSFRow hideRow = hsheet.getRow(i);
            if (hideRow == null) {
                hideRow = hsheet.createRow(i);
            }
            hideRow.createCell(column).setCellValue(listValue.get(i));
        }

        // Khởi tạo name cho mỗi loại danh mục
        Name namedRange = workbook.createName();
        namedRange.setNameName(nameName);
        String colName = CellReference.convertNumToColString(column);
        namedRange.setRefersToFormula(hsheet.getSheetName() + "!$" + colName + "$1:$" + colName + "$" + listValue.size());

        sheet.autoSizeColumn(column); //Auto điều chỉnh độ rộng cột

        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        CellRangeAddressList addressList = new CellRangeAddressList(row, row, column, column); //Tạo dropdownlist cho một cell
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

    private List<String> getOnlineFile(String pQueryURL, int pConnectTimeOut, int pReadTimeOut, String pProxyIP, int pProxyPort) throws Exception
    {
        Proxy lvProxy = null;
        if(pProxyIP != null && pProxyPort > 0)
            lvProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(pProxyIP, pProxyPort));

        List<String> lvOutput = new LinkedList<>();
        URL lvURL = new URL(pQueryURL);
        URLConnection lvURLConnection = lvProxy == null? lvURL.openConnection() : lvURL.openConnection(lvProxy);
        lvURLConnection.setConnectTimeout(pConnectTimeOut);
        lvURLConnection.setReadTimeout(pReadTimeOut);
        BufferedReader lvReader = new BufferedReader(new InputStreamReader(lvURLConnection.getInputStream()));
        String lvReadLine;
        while ((lvReadLine = lvReader.readLine()) != null)
        {
            lvOutput.add(lvReadLine);
        }

        return lvOutput;
    }

    public static File getFileDataCategoryInit() {
        return Paths.get(initCsvDataPath + "/Category.csv").toFile();
    }

    public static File getFileDataSystemInit() {
        return Paths.get(initXlsxDataPath + "/SystemDataInit.xlsx").toFile();
    }

    public static String getFileExtension(String fileName) {
        String extension = "";
        if (ObjectUtils.isNotEmpty(fileName)) {
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
                extension = fileName.substring(lastIndex + 1);
            }
        }
        return extension;
    }

    public static String genRandomFileName() {
        return UUID.randomUUID().toString();
    }

    public static String getFileUploadPath() {
        if (StartUp.getResourceUploadPath() == null) {
            throw new AppException("The uploaded file saving directory is not configured, please try again later!");
        }
        return StartUp.getResourceUploadPath() + "/uploads/";
    }

    public static String getImageTempPath() {
        if (StartUp.getResourceUploadPath() == null) {
            throw new AppException("The uploaded file saving directory is not configured, please try again later!");
        }
        return StartUp.getResourceUploadPath() + "/data-temp/";
    }

    public static boolean isAllowUpload(String fileExtension, boolean throwException, String message) {
        if (ObjectUtils.isNotEmpty(fileExtension)) {
            for (FileExtension ext : FileExtension.values()) {
                if (ext.key().equalsIgnoreCase(fileExtension) && ext.isAllowUpload()) {
                    return true;
                }
            }
        }
        if (throwException) {
            throw new AppException(ErrorCode.FileDoesNotAllowUpload, new Object[]{fileExtension}, message, null, null);
        }
        return false;
    }

    public static String getImageUrl(FileStorage imageModel, boolean addLeadingSlash) {
        if (imageModel == null) {
            return null;
        }
        String imageUrl = imageModel.getDirectoryPath() + "/" + imageModel.getStorageName();
        return addLeadingSlash ? "/" + imageUrl : imageUrl;
    }

    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        // Xác định ContentType
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream"; // Mặc định nếu không xác định được
        }
        // Đọc nội dung file thành byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());
        // Tạo MultipartFile
        return new MockMultipartFile(
                "file",        // Tên trường trong form
                file.getName(),      // Tên file gốc
                contentType,         // Loại nội dung
                fileContent          // Dữ liệu file
        );
    }

    public static File getFileUploaded(FileStorage fileModel) {
        Path path = Paths.get(getFileUploadPath() + File.separator + fileModel.getDirectoryPath() + File.separator + fileModel.getStorageName());
        return new File(path.toUri());
    }
}