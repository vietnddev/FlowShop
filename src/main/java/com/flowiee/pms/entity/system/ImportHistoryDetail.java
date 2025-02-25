package com.flowiee.pms.entity.system;

import lombok.*;

import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_history_id", nullable = false)
    private ImportHistory importHistory;

    @Column(name = "result", nullable = false)
    String result;
}