package com.flowiee.pms.common.utils;

import com.flowiee.pms.common.enumeration.MODULE;
import com.flowiee.pms.common.enumeration.SystemDir;
import com.flowiee.pms.modules.system.model.ServerInfo;
import com.flowiee.pms.modules.system.model.ShopInfo;
import com.flowiee.pms.common.security.UserPrincipal;
import com.flowiee.pms.common.enumeration.CATEGORY;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.*;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class CommonUtils {
    public static Map<String, String> mvEndPointHeaderConfig = new HashMap<>();
    public static Map<String, String> mvEndPointSideBarConfig = new HashMap<>();
    public static ShopInfo mvShopInfo;
    public static ServerInfo mvServerInfo;
    public static final String productID = "SMS";
    public static String defaultCountryCode = "VN";
    public static String defaultNewPassword = "123456";

    public static String formatToVND(Object currency) {
        @SuppressWarnings("deprecation")
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        currencyFormat.setCurrency(Currency.getInstance("VND"));
        return currency != null ? currencyFormat.format(currency) : "0 VND";
    }

    public static String genCategoryCodeByName(String categoryName) {
        String normalized = Normalizer.normalize(categoryName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("[^\\p{ASCII}]");
        String strTemp = pattern.matcher(normalized).replaceAll("").replaceAll(" ", "").toUpperCase();

        StringBuilder result = new StringBuilder();
        for (char c : strTemp.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static String now(String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime now = LocalDateTime.now();
        return dateTimeFormatter.format(now);
    }

    public static String getCategoryType(String key) {
        Map<String, String> map = new HashMap<>();
        for (CATEGORY c : CATEGORY.values()) {
            map.put(c.getKey(), c.getName());
        }
        return map.get(key);
    }

    public static String getPathDirectory(MODULE pModule) {
        return getPathDirectory(pModule.name());
    }

    public static String getPathDirectory(String systemModule) {
        try {
            StringBuilder path = new StringBuilder(FileUtils.getSystemDir(SystemDir.UPLOAD));
            if (MODULE.PRODUCT.name().equals(systemModule)) {
                path.append("product");
            } else if (MODULE.CATEGORY.name().equals(systemModule)) {
                path.append("category");
            } else if (MODULE.STORAGE.name().equals(systemModule)) {
                path.append("storage");
            } else if (MODULE.SALES.name().equals(systemModule)) {
                path.append("sales");
            } else if (MODULE.SYSTEM.name().equals(systemModule)) {
                path.append("system");
            } else if ("data-temp".equals(systemModule)) {
                path.append("data-temp");
            }
            path.append(File.separator).append(LocalDateTime.now().getYear());
            path.append(File.separator).append(LocalDateTime.now().getMonth().getValue());
            path.append(File.separator).append(LocalDateTime.now().getDayOfMonth());
            File folder = new File(path.toString());
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("mkdirs OK " + folder.getAbsolutePath());
                }
            }
            return path.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static byte[] exportTemplate(String templateName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        String filePathOriginal = FileUtils.excelTemplatePath + "/" + templateName + ".xlsx";
        String filePathTemp = FileUtils.excelTemplatePath + "/" + templateName + "_" + Instant.now(Clock.systemUTC()).toEpochMilli() + ".xlsx";
        File fileDeleteAfterExport = null;
        try {
            fileDeleteAfterExport = new File(Path.of(filePathTemp).toUri());
            XSSFWorkbook workbook = new XSSFWorkbook(Files.copy(Path.of(filePathOriginal), Path.of(filePathTemp), StandardCopyOption.REPLACE_EXISTING).toFile());
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

    public static XSSFCellStyle highlightCellInvalidValue(XSSFCellStyle cellStyle, XSSFFont fontStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        fontStyle.setColor(IndexedColors.RED.getIndex());

        cellStyle.setFont(fontStyle);

        return cellStyle;
    }

    public static UserPrincipal getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (!"anonymousUser".equalsIgnoreCase(authentication.getPrincipal().toString())) {
                return (UserPrincipal) authentication.getPrincipal();
            }
        }
        return UserPrincipal.anonymousUser();
    }

    public static CATEGORY getCategoryEnum(String name) {
        for (CATEGORY c : CATEGORY.values()) {
            if (c.name().equals(name) || c.name().equals(getCategoryType(name))) {
                return c;
            }
        }
        return null;
    }

    public static String getServerURL() {
        return "http://" + CommonUtils.mvServerInfo.ip() + ":" + CommonUtils.mvServerInfo.port();
    }
}