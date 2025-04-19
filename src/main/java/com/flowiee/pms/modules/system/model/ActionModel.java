package com.flowiee.pms.modules.system.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ActionModel {
    String actionKey;
    String actionLabel;
    String moduleKey;
}