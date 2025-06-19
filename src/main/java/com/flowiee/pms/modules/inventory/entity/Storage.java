package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "storage")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Storage extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "code", unique = true)
    String code;

    @Column(name = "location")
    String location;

    @Column(name = "area")
    Double area;

    @Column(name = "holdable_quantity")
    Integer holdableQty;

    @Column(name = "hold_warning_percent")
    Integer holdWarningPercent;

    @Column(name = "description")
    String description;

    @Column(name = "is_default")
    Boolean isDefault;

    @Column(name = "status", nullable = false)
    String status;

    @JsonIgnore
    @OneToMany(mappedBy = "storage", fetch = FetchType.LAZY)
    List<TicketImport> listTicketImports;

    @JsonIgnore
    @OneToMany(mappedBy = "storage", fetch = FetchType.LAZY)
    List<TicketExport> listTicketExports;

    public Storage(Long id) {
        this.id = id;
    }
}