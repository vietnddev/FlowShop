package com.flowiee.pms.modules.system.entity;

import lombok.*;

import jakarta.persistence.*;

@Builder
@Entity
@Table(name = "import_history_detail")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ImportHistoryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "import_history_id", nullable = false)
    private ImportHistory importHistory;

    @Column(name = "result", nullable = false)
    String result;
}