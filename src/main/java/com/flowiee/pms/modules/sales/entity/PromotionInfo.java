package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.sales.dto.PromotionInfoDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "promotion_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionInfo extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", length = 1000)
    String description;

    @Column(name = "discount_percent")
    Integer discountPercent;

    @Column(name = "discount_price")
    BigDecimal discountPrice;

    @Column(name = "discount_price_max")
    BigDecimal discountPriceMax;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "end_time", nullable = false)
    LocalDateTime endTime;

    public static PromotionInfo fromDTO(PromotionInfoDTO inputDTO) {
        PromotionInfo promotionInfo = PromotionInfo.builder()
            .title(inputDTO.getTitle())
            .description(inputDTO.getDescription())
            .discountPercent(inputDTO.getDiscountPercent())
            .discountPrice(inputDTO.getDiscountPrice())
            .discountPriceMax(inputDTO.getDiscountPriceMax())
            .build();
        promotionInfo.setId(inputDTO.getId());
        return promotionInfo;
    }
}