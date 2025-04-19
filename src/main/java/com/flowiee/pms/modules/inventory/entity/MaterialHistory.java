package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "material_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialHistory extends BaseEntity implements Serializable {
    @Serial
	static final long serialVersionUID = 1L;

	@ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    Material material;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "field_name", nullable = false)
    String fieldName;

    @Column(name = "old_value")
    String oldValue;

    @Column(name = "newValue")
    String newValue;

    @Override
    public String toString() {
        return "MaterialHistory [id=" + super.getId() + ", material=" + material + ", title=" + title + ", fieldName=" + fieldName + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
    }
}