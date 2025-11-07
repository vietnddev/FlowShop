package com.flowiee.pms.common.utils;

import com.flowiee.pms.common.base.StartUp;
import com.flowiee.pms.common.enumeration.SystemDir;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.FileExtension;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    public static String resourceStaticPath = "src/main/resources/static";
    public static String initCsvDataPath = resourceStaticPath + "/data/csv";
    public static String initXlsxDataPath = resourceStaticPath + "/data/excel";
    public static String reportTemplatePath = resourceStaticPath + "/report";
    public static String excelTemplatePath = resourceStaticPath + "/templates/excel";
    public static Path logoPath = Paths.get(resourceStaticPath + "/dist/img/FlowieeLogo.png");

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
        return Paths.get(initCsvDataPath + File.pathSeparator + "Category.csv").toFile();
    }

    public static File getFileDataSystemInit() {
        return Paths.get(initXlsxDataPath + File.pathSeparator + "SystemDataInit.xlsx").toFile();
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

    public static String getImageTempPath() {
        if (StartUp.getResourceUploadPath() == null) {
            throw new AppException("The uploaded file saving directory is not configured, please try again later!");
        }
        return StartUp.getResourceUploadPath() + File.pathSeparator + "data-temp" + File.pathSeparator;
    }

    public static String getSystemDir(SystemDir pSystemDir) {
        return switch (pSystemDir) {
            case UPLOAD: yield StartUp.getResourceUploadPath();
            case BACKUP: yield StartUp.getResourceBackupPath();
            case RESTORE: yield StartUp.getResourceRestorePath();
            case ARCHIVE: yield StartUp.getResourceArchivePath();
        };
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
        String imageUrl = imageModel.getDirectoryPath() + File.separator + imageModel.getStorageName();
        return addLeadingSlash ? File.separator + imageUrl : imageUrl;
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
//        return new MockMultipartFile(
//                "file",        // Tên trường trong form
//                file.getName(),      // Tên file gốc
//                contentType,         // Loại nội dung
//                fileContent          // Dữ liệu file
//        );
        return new CustomMultipartFile(fileContent, file.getName(), contentType);
    }

    public static void zipDirectory(Path sourceDirPath, Path zipPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Files.walk(sourceDirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                        try (InputStream fis = Files.newInputStream(path)) {
                            zos.putNextEntry(zipEntry);
                            fis.transferTo(zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    public static void unzipFile(Path zipFilePath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newFile = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(newFile);
                } else {
                    Files.createDirectories(newFile.getParent());
                    try (OutputStream os = Files.newOutputStream(newFile)) {
                        zis.transferTo(os);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    public static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); } catch (IOException ignored) {}
                    });
        }
    }

    public static void renameDirectory(Path source, Path target) throws IOException {
        if (Files.exists(source)) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static long sizeOfDirectory(String pDirPath) {
        return org.apache.commons.io.FileUtils.sizeOfDirectory(new File(pDirPath));
    }

    public static int fileOfDirectory(String pDirPath) {
        String[] extension = null; // null = tất cả extension
        return org.apache.commons.io.FileUtils.listFiles(
                new File(pDirPath), extension,
                true   // true = đệ quy vào subfolder
        ).size();
    }

    public static class CustomMultipartFile implements MultipartFile {
        private final byte[] fileContent;
        private final String fileName;
        private final String contentType;

        public CustomMultipartFile(byte[] fileContent, String fileName, String contentType) {
            this.fileContent = fileContent;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return "file"; // Tên trường trong form
        }

        @Override
        public String getOriginalFilename() {
            return this.fileName;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return this.fileContent == null || this.fileContent.length == 0;
        }

        @Override
        public long getSize() {
            return this.fileContent.length;
        }

        @Override
        public byte[] getBytes() {
            return this.fileContent;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.fileContent);
        }

        @Override
        public void transferTo(File dest) throws IOException {
            try (FileOutputStream out = new FileOutputStream(dest)) {
                out.write(this.fileContent);
            }
        }
    }
}