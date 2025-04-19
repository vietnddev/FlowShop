package com.flowiee.pms.modules.system.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleModel {
    Long accountId;
    Long groupId;
    ModuleModel module;
    ActionModel action;
    Boolean isAuthor;
}