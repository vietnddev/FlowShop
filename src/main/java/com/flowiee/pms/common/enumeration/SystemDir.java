package com.flowiee.pms.common.enumeration;

import com.flowiee.pms.common.utils.FileUtils;
import lombok.Getter;

@Getter
public enum SystemDir {
    UPLOAD("Store all files uploaded by users"),
    BACKUP("Store backup copies of the files from UPLOAD"),
    RESTORE("Store files temporarily when restoring from an uploaded file"),
    ARCHIVE("Move the current contents of UPLOAD here after they have been restored, for record-keeping");

    private final String name;

    SystemDir(String pName) {
        this.name = pName;
    }

    public int getNumberOfFiles() {
        return FileUtils.fileOfDirectory(FileUtils.getSystemDir(this));
    }

    public long getUsedSpace() {
        return FileUtils.sizeOfDirectory(FileUtils.getSystemDir(this));
    }
}