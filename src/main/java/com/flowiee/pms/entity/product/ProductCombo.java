package com.flowiee.pms.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import com.flowiee.pms.entity.system.FileStorage;
import com.flowiee.pms.model.dto.ProductVariantDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "product_combo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCombo extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "combo_name", nullable = false)
    String comboName;

    @Column(name = "start_date")
    LocalDate startDate;

    @Column(name = "end_date")
    LocalDate endDate;

    @Column(name = "amount_discount", nullable = false)
    BigDecimal amountDiscount;

    @Column(name = "note")
    String note;

    @JsonIgnore
    @JsonIgnoreProperties("productCombo")
    @OneToMany(mappedBy = "productCombo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<FileStorage> listImages;

    @Transient
    BigDecimal totalValue;

    @Transient
    Integer quantity;

    @Transient
    String status;

    @Transient
    List<ProductVariantDTO> applicableProducts;

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
}