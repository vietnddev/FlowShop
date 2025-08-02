package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.common.enumeration.TicketExportStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "ticket_export_goods")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketExport extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "storage_id", nullable = false)
    Storage storage;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "exporter", nullable = false)
    String exporter;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "export_time", nullable = false)
    LocalDateTime exportTime;

    @Column(name = "note", length = 500)
    String note;

    @Column(name = "status")
    String status;

    @JsonIgnore
    @OneToMany(mappedBy = "ticketExport", fetch = FetchType.LAZY)
    List<Order> listOrders;

    @JsonIgnore
    @OneToMany(mappedBy = "ticketExport", fetch = FetchType.LAZY)
    List<ProductVariantExim> listProductVariantTemp;

    @JsonIgnore
    @OneToMany(mappedBy = "ticketExport", fetch = FetchType.LAZY)
    List<FileStorage> listImages;

    @Transient
    Integer totalItems;

    @Transient
    BigDecimal totalValue;

    @Transient
    String storageName;

    @Transient
    String exportTimeStr;

    public TicketExport(Long id) {
        this.id = id;
    }

    public boolean isDraftStatus() {
        return TicketExportStatus.DRAFT.name().equals(status);
    }

    public boolean isCompletedStatus() {
        return TicketExportStatus.COMPLETED.name().equals(status);
    }

    public boolean isCancelStatus() {
        return TicketExportStatus.CANCEL.name().equals(status);
    }
}