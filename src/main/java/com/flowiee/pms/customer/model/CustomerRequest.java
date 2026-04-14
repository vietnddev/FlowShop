package com.flowiee.pms.customer.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class CustomerRequest {
    private List<Long> customerIds;
    private String name;
    private String sex;
    private Date birthday;
    private String phone;
    private String email;
    private String address;
    private Boolean isVIP;
    private Boolean isBlackList;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
}