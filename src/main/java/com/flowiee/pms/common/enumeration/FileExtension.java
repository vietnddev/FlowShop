package com.flowiee.pms.common.enumeration;

import lombok.Getter;

@Getter
public enum FileExtension {
    PNG("png", true),
    JPG("jpg", true),
    JPEG("jpeg", true),
    PDF("pdf", false),
    XLS("xls", false),
    XLSX("xlsx", false),
    PPT("ppt", false),
    PPTX("pptx", false),
    DOC("doc", false),
    DOCX("docx", false),
    TXT("txt", false),
    LOG("log", false),
    JSON("json", false);

    private final String key;
    private final boolean isAllowUpload;

    FileExtension(String key, boolean isAllowUpload) {
        this.key = key;
        this.isAllowUpload = isAllowUpload;
    }

    public String key() {
        return this.name().toLowerCase();
    }
}