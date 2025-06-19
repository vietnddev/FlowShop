package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;

import com.flowiee.pms.modules.system.entity.Category;
import com.flowiee.pms.modules.sales.entity.Supplier;
import com.flowiee.pms.modules.media.entity.FileStorage;
import com.flowiee.pms.modules.inventory.dto.MaterialDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "material")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Material extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "brand_id")
    Category brand;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    Supplier supplier;

    @Column(name = "code", length = 20, unique = false)
    String code;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "unit_id")
    Category unit;

    @Column(name = "location")
    String location;

    @Column(name = "note")
    String note;

    @Column(name = "status", nullable = false)
    boolean status;

    @JsonIgnore
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    List<MaterialHistory> listMaterialHistory;

    @JsonIgnore
    @JsonIgnoreProperties("material")
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<FileStorage> listImages;

    @JsonIgnore
    @JsonIgnoreProperties("material")
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<MaterialTemp> listMaterialTemp;

//    @JsonIgnore
//    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
//    List<TransactionGoodsItem> transactionGoodsItemList;

    public Material(long id) {
        super.id = id;
    }

    public static Material fromMaterialDTO(MaterialDTO dto) {
    	Material material = Material.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .location(dto.getLocation())
                .unit(new Category(dto.getUnitId(), dto.getUnitName()))
                .quantity(dto.getQuantity())
                .note(dto.getNote())
                .build();
        material.setId(dto.getId());
        return material;
    }

    public FileStorage getImage(Long pImageId) {
        if (getListImages() != null) {
            return getListImages().stream()
                    .filter(image -> image.getId().equals(pImageId))
                    .findAny()
                    .orElse(null);
        }
        return null;
    }

    public FileStorage getImage() {
        if (getListImages() != null) {
            for (FileStorage image : getListImages()) {
                if (image.isActive())
                    return image;
            }
            return null;
        }
        return null;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Material [id=");
		builder.append(super.id);
		//builder.append(", ticketImportGoods=");
		//builder.append(ticketImport);
		builder.append(", supplier=");
		builder.append(supplier);
		builder.append(", code=");
		builder.append(code);
		builder.append(", name=");
		builder.append(name);
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append(", unit=");
		builder.append(unit);
		builder.append(", location=");
		builder.append(location);
		builder.append(", note=");
		builder.append(note);
		builder.append(", status=");
		builder.append(status);
		builder.append(", listMaterialHistory=");
		builder.append(listMaterialHistory);
		builder.append("]");
		return builder.toString();
	}
}