package com.flowiee.pms.modules.system.service;

import java.io.File;
import java.io.IOException;

public interface FileBackupService {
    File createBackupZip() throws IOException;
}