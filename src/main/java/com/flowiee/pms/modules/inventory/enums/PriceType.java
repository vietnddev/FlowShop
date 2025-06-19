package com.flowiee.pms.modules.inventory.enums;

import lombok.Getter;

@Getter
public enum PriceType {
    STD("Standard Price"),
    RTL("Retail Price"),
    WHO("Wholesale Price"),
    CSP("Cost Price"),
    AGT("Agent Price"),
    SPC("Special Customer Price"),
    SHP("Shopee Price"),
    TTS("TikTok Shop Price"),
    FBK("Facebook Price"),
    PRM("Promotion Price"),
    FLS("Flash Sale Price"),
    HLD("Holiday Price"),
    TMB("Time Based Price");

    private final String description;

    PriceType(String description) {
        this.description = description;
    }
}