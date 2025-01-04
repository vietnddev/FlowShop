package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "loyalty_program")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoyaltyProgram extends BaseEntity implements Serializable {
    @Column(name = "name", nullable = false, unique = true)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    boolean isActive;

    @Column(name = "is_default", nullable = false)
    Boolean isDefault;

    @JsonIgnore
    @OneToMany(mappedBy = "loyaltyProgram", fetch = FetchType.LAZY)
    List<LoyaltyTransaction> loyaltyTransactionList;

    @JsonIgnore
    @OneToMany(mappedBy = "loyaltyProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoyaltyRule> loyaltyRuleList;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }
}