package com.flowiee.pms.shared.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

@NoRepositoryBean
public interface SoftDeleteRepository<E, ID> extends JpaRepository<E, ID> {
    @Modifying
    @Query("""
        update #{#entityName} e
        set e.deletedAt = :deletedAt,
            e.deletedBy = :deletedBy
        where e.id = :id
    """)
    void softDelete(@Param("id") ID id,
                    @Param("deletedAt") LocalDateTime deletedAt,
                    @Param("deletedBy") String deletedBy);
}