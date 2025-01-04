package com.flowiee.pms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.pms.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "branch")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Branch extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "branch_code", nullable = false, unique = true)
    String branchCode;

    @Column(name = "branch_name", nullable = false)
    String branchName;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "email")
    String email;

    @Column(name = "address")
    String address;

    @Column(name = "contact_point")
    String contactPoint;

    @JsonIgnore
    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    List<Account> listAccount;

    public Branch(Long id, String branchCode, String branchName) {
        this.id = id;
        this.branchCode = branchCode;
        this.branchName = branchName;
    }
}