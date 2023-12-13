package com.flowiee.app.service.impl;

import com.flowiee.app.entity.*;
import com.flowiee.app.exception.DataInUseException;
import com.flowiee.app.model.role.SystemModule;
import com.flowiee.app.repository.CategoryRepository;
import com.flowiee.app.service.*;
import com.flowiee.app.utils.*;
import com.flowiee.app.repository.FileStorageRepository;
import com.flowiee.app.repository.FlowieeImportRepository;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
	private static final String MODULE = SystemModule.CATEGORY.name();
	
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryHistoryService categoryHistoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ImportService importService;
    @Autowired
    private FlowieeImportRepository flowieeImportRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private FileStorageRepository fileStorageRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MaterialService materialService;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Integer entityId) {
        return categoryRepository.findById(entityId).orElse(null);
    }

    @Override
    public String save(Category entity) {
        if (entity == null) {
            return AppConstants.SERVICE_RESPONSE_FAIL;
        }
        categoryRepository.save(entity);
        return AppConstants.SERVICE_RESPONSE_SUCCESS;
    }

    @Transactional
    @Override
    public String update(Category entity, Integer entityId) {
        if (entity == null || entityId == null || entityId <= 0) {
            return AppConstants.SERVICE_RESPONSE_FAIL;
        }
        Category categoryBefore = this.findById(entityId);
        categoryBefore.compareTo(entity).forEach((key, value) -> {
            CategoryHistory categoryHistory = new CategoryHistory();
            categoryHistory.setTitle("Update category");
            categoryHistory.setCategory(new Category(entityId, null));
            categoryHistory.setFieldName(key);
            categoryHistory.setOldValue(value.substring(0, value.indexOf("#")));
            categoryHistory.setNewValue(value.substring(value.indexOf("#") + 1));
            categoryHistoryService.save(categoryHistory);
        });
        entity.setId(entityId);
        categoryRepository.save(entity);
        return AppConstants.SERVICE_RESPONSE_SUCCESS;
    }

    @Transactional
    @Override
    public String delete(Integer entityId) {
        if (entityId == null || entityId <= 0 || this.findById(entityId) == null) {
            return AppConstants.SERVICE_RESPONSE_FAIL;
        }
        if (categoryInUse(entityId)) {
            throw new DataInUseException(MessagesUtil.ERROR_LOCKED);
        } else {
            categoryRepository.deleteById(entityId);
        }
        return AppConstants.SERVICE_RESPONSE_SUCCESS;
    }

    @Override
    public List<Category> findRootCategory() {
        return categoryRepository.findRootCategory();
    }

    @Override
    public List<Category> findSubCategory(String categoryType) {
        return categoryRepository.findSubCategory(categoryType);
    }

    @Override
    public List<Category> findSubCategory(List<String> categoryTypes) {
        return categoryRepository.findSubCategory(categoryTypes);
    }

    @Override
    public Category findSubCategoryDefault(String categoryType) {
        return categoryRepository.findSubCategoryDefault(categoryType);
    }

    @Override
    public List<Category> findSubCategoryUnDefault(String categoryType) {
        return categoryRepository.findSubCategoryUnDefault(categoryType);
    }

    @Override
    public Boolean categoryInUse(Integer categoryId) {
        Category category = this.findById(categoryId);
        switch (category.getType()) {
            case "UNIT":
                if (!productService.findByProductType(categoryId).isEmpty() || !materialService.findByUnit(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "FABRICTYPE":
                if (!productVariantService.findByFabricType(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "PAYMETHOD":
                if (!orderPayService.findByPayMethod(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "SALESCHANNEL":
                if (!orderService.findBySalesChannel(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "SIZE":
                if (!productVariantService.findBySize(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "COLOR":
                if (!productVariantService.findByColor(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "PRODUCTTYPE":
                if (!productService.findByProductType(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "DOCUMENTTYPE":
                if (!documentService.findByDoctype(categoryId).isEmpty()) {
                    return true;
                }
                break;
            case "ORDERSTATUS":
                if (!orderService.findByOrderStatus(categoryId).isEmpty()) {
                    return true;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + category.getType());
        }
        return false;
    }

    @Transactional
	@Override
	public String importData(MultipartFile fileImport, String categoryType) {
		Date startTimeImport = new Date();
        String resultOfFlowieeImport = "";
        String detailOfFlowieeImport = "";
        int importSuccess = 0;
        int totalRecord = 0;
        boolean isImportSuccess = true;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(fileImport.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 3; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    String categoryCode = row.getCell(1).getStringCellValue();
                    String categoryName = row.getCell(2).getStringCellValue();
                    String categoryNote = row.getCell(3).getStringCellValue();
                    //Nếu name null -> không ínsert data null vào database
                    if (categoryName == null || categoryName.length() == 0) {
                        XSSFCellStyle cellStyle = workbook.createCellStyle();
                        XSSFFont fontStyle = workbook.createFont();
                        row.getCell(1).setCellStyle(CommonUtil.highlightDataImportEror(cellStyle, fontStyle));
                        row.getCell(2).setCellStyle(CommonUtil.highlightDataImportEror(cellStyle, fontStyle));
                        row.getCell(3).setCellStyle(CommonUtil.highlightDataImportEror(cellStyle, fontStyle));
                        continue;
                    }

                    Category category = new Category();
                    category.setType(categoryType);
                    category.setCode(!categoryCode.isEmpty() ? categoryCode : CommonUtil.getMaDanhMuc(categoryName));
                    category.setName(categoryName);
                    category.setNote(categoryNote);

                    if (!"OK".equals(this.save(category))) {
                        isImportSuccess = false;
                        XSSFCellStyle cellStyle = workbook.createCellStyle();
                        XSSFFont fontStyle = workbook.createFont();
                        row.getCell(1).setCellStyle(CommonUtil.highlightDataImportEror(cellStyle, fontStyle));
                        row.getCell(2).setCellStyle(CommonUtil.highlightDataImportEror(cellStyle, fontStyle));
                        row.getCell(3).setCellStyle(CommonUtil.highlightDataImportEror(cellStyle, fontStyle));
                    } else {
                        importSuccess++;
                    }
                    totalRecord++;
                }
            }
            workbook.close();

            if (isImportSuccess) {
                resultOfFlowieeImport = MessagesUtil.IMPORT_DM_DONVITINH_SUCCESS;
                detailOfFlowieeImport = importSuccess + " / " + totalRecord;
            } else {
                resultOfFlowieeImport = MessagesUtil.IMPORT_DM_DONVITINH_FAIL;
                detailOfFlowieeImport = importSuccess + " / " + totalRecord;
            }
            //Save file attach to storage
            FileStorage fileStorage = new FileStorage(fileImport, SystemModule.CATEGORY.name());
            fileStorage.setGhiChu("IMPORT");
            fileStorage.setStatus(false);
            fileStorage.setActive(false);
            fileStorage.setAccount(accountService.findCurrentAccount());
            fileStorageService.saveFileOfImport(fileImport, fileStorage);

            //Save import
            FlowieeImport flowieeImport = new FlowieeImport();
            flowieeImport.setModule(MODULE);
            flowieeImport.setEntity(Category.class.getName());
            flowieeImport.setAccount(accountService.findCurrentAccount());
            flowieeImport.setStartTime(startTimeImport);
            flowieeImport.setEndTime(new Date());
            flowieeImport.setResult(resultOfFlowieeImport);
            flowieeImport.setDetail(detailOfFlowieeImport);
            flowieeImport.setSuccessRecord(importSuccess);
            flowieeImport.setTotalRecord(totalRecord);
            flowieeImport.setFileId(fileStorageRepository.findByCreatedTime(fileStorage.getCreatedAt()).getId());
            importService.save(flowieeImport);

            Notification notification = new Notification();
            notification.setTitle(resultOfFlowieeImport);
            notification.setSend(CommonUtil.SYS_NOTI_ID);
            notification.setReceive(CommonUtil.getCurrentAccountId());
            notification.setType(MessagesUtil.NOTI_TYPE_IMPORT);
            notification.setContent(resultOfFlowieeImport);
            notification.setReaded(false);
            notification.setImportId(flowieeImportRepository.findByStartTime(flowieeImport.getStartTime()).getId());
            notificationService.save(notification);

            return AppConstants.SERVICE_RESPONSE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AppConstants.SERVICE_RESPONSE_FAIL;
	}

	@Override
	public byte[] exportTemplate(String categoryType) {
		return CommonUtil.exportTemplate(AppConstants.TEMPLATE_IE_DM_CATEGORY);
	}

	@Override
	public byte[] exportData(String categoryType) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        String filePathOriginal = CommonUtil.PATH_TEMPLATE_EXCEL + "/" + AppConstants.TEMPLATE_IE_DM_CATEGORY + ".xlsx";
        String filePathTemp = CommonUtil.PATH_TEMPLATE_EXCEL + "/" + AppConstants.TEMPLATE_IE_DM_CATEGORY + "_" + Instant.now(Clock.systemUTC()).toEpochMilli() + ".xlsx";
        File fileDeleteAfterExport = new File(Path.of(filePathTemp).toUri());
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(Files.copy(Path.of(filePathOriginal),
								                     Path.of(filePathTemp),
								                     StandardCopyOption.REPLACE_EXISTING).toFile());
            XSSFSheet sheet = workbook.getSheetAt(0);
            List<Category> listData = this.findAll();
            for (int i = 0; i < listData.size(); i++) {
                XSSFRow row = sheet.createRow(i + 3);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(listData.get(i).getCode());
                row.createCell(2).setCellValue(listData.get(i).getName());
                row.createCell(3).setCellValue(listData.get(i).getNote());
                for (int j = 0; j <= 3; j++) {
                    row.getCell(j).setCellStyle(CommonUtil.setBorder(workbook.createCellStyle()));
                }
            }
            workbook.write(stream);
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileDeleteAfterExport.exists()) {
                fileDeleteAfterExport.delete();
            }
        }
        return stream.toByteArray();
	}
}