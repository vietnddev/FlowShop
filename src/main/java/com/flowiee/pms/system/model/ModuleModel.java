package com.flowiee.pms.system.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleModel {
    String moduleKey;
    String moduleLabel;
}