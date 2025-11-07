package com.flowiee.pms.modules.system.dto;

import lombok.Data;

@Data
public class GeoLocationResult {
    private String ip;
    private String country;
    private String city;
    private String source; // ipwho / ipapi
}