package com.flowiee.pms.modules.report.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "report")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Report implements Serializable {
    @Id
    @Column(name = "report_id", nullable = false, length = 20)
    private String reportId;

    @Column(name = "report_name", nullable = false, length = 99)
    private String reportName;

    @Column(name = "locked")
    private Boolean locked;
}