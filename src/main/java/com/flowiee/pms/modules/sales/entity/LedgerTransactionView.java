package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vw_general_ledger_transaction")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LedgerTransactionView {
    @Id
    @Column(name = "tran_id")
    Integer tranId;

    @Column(name = "tran_code")
    String tranCode;

    @Column(name = "tran_type")
    String tranType;

    @Column(name = "description")
    String description;

    @Column(name = "value")
    BigDecimal value;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "tran_time")
    LocalDateTime tranTime;
}