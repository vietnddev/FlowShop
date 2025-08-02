package com.flowiee.pms.modules.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.common.base.entity.BaseEntity;
import com.flowiee.pms.modules.sales.entity.Customer;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "product_review")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductReview extends BaseEntity implements Serializable {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @NotBlank(message = "Review content cannot be empty")
    @Column(name = "review_content")
    private String reviewContent;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;
}