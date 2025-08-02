package com.flowiee.pms.modules.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "category_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryHistory extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "field", nullable = false)
    String field;

    @Lob
    @Column(name = "old_value", nullable = false, columnDefinition = "TEXT")
    String oldValue;

    @Lob
    @Column(name = "new_value", nullable = false, columnDefinition = "TEXT")
    String newValue;

	@Override
	public String toString() {
		return "CategoryHistory [id=" + super.id + ", category=" + category + ", title=" + title + ", fieldName=" + field + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
	}
}