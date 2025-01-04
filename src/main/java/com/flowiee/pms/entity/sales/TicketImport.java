package com.flowiee.pms.entity.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;

import com.flowiee.pms.entity.category.Category;
import com.flowiee.pms.entity.product.MaterialTemp;
import com.flowiee.pms.entity.product.ProductVariantExim;
import com.flowiee.pms.entity.storage.Storage;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.model.dto.TicketImportDTO;
import com.flowiee.pms.common.enumeration.TicketImportStatus;
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
@Table(name = "ticket_import_goods")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketImport extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id", nullable = false)
    Storage storage;

	@Column(name = "title", nullable = false)
    String title;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

    @Column(name = "discount")
    Float discount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method")
    Category paymentMethod;

    @Column(name = "paid_amount")
    Float paidAmount;

    @Column(name = "pay_status")
    String paidStatus;

    @Column(name = "importer")
    String importer;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
//    @JsonSerialize(using = DateSerializer.class)
//    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @Column(name = "import_time")
    LocalDateTime importTime;

    @Column(name = "note")
    String note;

    @Column(name = "status", nullable = false)
    String status;

    @JsonIgnore
    @OneToMany(mappedBy = "ticketImport", fetch = FetchType.LAZY)
    List<MaterialTemp> listMaterialTemps;

    @JsonIgnore
    @OneToMany(mappedBy = "ticketImport", fetch = FetchType.LAZY)
    List<ProductVariantExim> listProductVariantTemps;

    @JsonIgnore
    @OneToMany(mappedBy = "ticketImport", fetch = FetchType.LAZY)
    List<FileStorage> listImages;

    @Transient
    BigDecimal totalValue;

    @Transient
    Integer totalItems;

    public TicketImport(Long id) {
        super.id = id;
    }

    public TicketImport(Long id, String title) {
        super.id = id;
        this.title = title;
    }

    public static TicketImport fromTicketImportDTO(TicketImportDTO dto) {
        TicketImport ticketImport = TicketImport.builder()
            .title(dto.getTitle())
            .supplier((dto.getSupplierId() != null && dto.getSupplierId() > 0) ? new Supplier(dto.getSupplierId(), dto.getSupplierName()) : null)
            .importer(dto.getImporter())
            .importTime(dto.getImportTime())
            .note(dto.getNote())
            .status(dto.getStatus())
            .listProductVariantTemps(dto.getListProductVariantTemp())
            .listMaterialTemps(dto.getListMaterialTemp())
            .build();
        ticketImport.setId(dto.getId());
        return ticketImport;
    }

    public boolean isCompletedStatus() {
        return TicketImportStatus.COMPLETED.name().equals(status);
    }

    public boolean isCancelStatus() {
        return TicketImportStatus.CANCEL.name().equals(status);
    }

	@Override
	public String toString() {
		return "TicketImportGoods [id=" + super.id + ", title=" + title + ", supplier=" + supplier + ", discount=" + discount
				+ ", paymentMethod=" + paymentMethod + ", paidAmount=" + paidAmount + ", paidStatus=" + paidStatus
				+ ", note=" + note + ", status=" + status + "]";
	}
}