package com.flowiee.pms.system.service;

import java.io.File;
import java.io.IOException;

public interface FileBackupService {
    File createBackupZip() throws IOException;
}