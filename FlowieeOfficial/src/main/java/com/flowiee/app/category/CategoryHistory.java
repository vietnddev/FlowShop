package com.flowiee.app.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.app.base.BaseEntity;
import com.flowiee.app.entity.Price;
import com.flowiee.app.entity.Product;
import com.flowiee.app.entity.ProductAttribute;
import com.flowiee.app.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "category_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoryHistory extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Lob
    @Column(name = "old_value", nullable = false, length = 9999, columnDefinition = "TEXT")
    private String oldValue;

    @Lob
    @Column(name = "new_value", nullable = false, length = 9999, columnDefinition = "TEXT")
    private String newValue;
}