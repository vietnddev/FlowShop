package com.flowiee.pms.modules.sales.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "order_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderHistory extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "order_detail_id")
    OrderDetail orderDetail;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "field_name", nullable = false)
    String field;

    @Lob
    @Column(name = "old_value", nullable = false, columnDefinition = "TEXT")
    String oldValue;

    @Lob
    @Column(name = "new_value", nullable = false, columnDefinition = "TEXT")
    String newValue;
}