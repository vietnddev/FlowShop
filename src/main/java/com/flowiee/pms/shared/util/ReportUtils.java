package com.flowiee.pms.shared.util;

import java.io.File;
import java.nio.file.Path;

public class ReportUtils {
    public static File getReportTemplate(String reportName) {
        return new File(Path.of(FileUtils.reportTemplatePath + "/" + reportName + ".jasper").toUri());
    }
}