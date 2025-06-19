package com.flowiee.pms.modules.system.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationDTO extends BaseDTO implements Serializable {
    private Long send;
    private Long receive;
    private String type;
    private String title;
    private String content;
    private Boolean readed;
    private Long importId;
}