package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class LoyaltyProgramDTO extends BaseDTO implements Serializable {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive;

    private Boolean isDefault;
}