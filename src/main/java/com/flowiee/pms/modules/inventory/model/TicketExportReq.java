package com.flowiee.pms.modules.inventory.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketExportReq {
    String title;
    String orderCode;
}