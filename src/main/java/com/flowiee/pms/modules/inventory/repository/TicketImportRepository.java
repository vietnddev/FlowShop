package com.flowiee.pms.modules.inventory.repository;

import com.flowiee.pms.modules.inventory.entity.TicketImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketImportRepository extends JpaRepository<TicketImport, Long> {
    @Query("from TicketImport i " +
           "where (:text is null or i.title like %:text%) " +
           "and (:supplierId is null or i.supplier.id=:supplierId) " +
           "and (:paymentMethod is null or i.paymentMethod.id=:paymentMethod) " +
           "and (:payStatus is null or i.paidStatus=:payStatus) " +
           "and (:importStatus is null or i.status=:importStatus) " +
           "and (:storageId is null or i.storage.id=:storageId)")
    Page<TicketImport> findAll(@Param("text") String text,
                               @Param("supplierId") Long supplierId,
                               @Param("paymentMethod") Long paymentMethod,
                               @Param("payStatus") String payStatus,
                               @Param("importStatus") String importStatus,
                               @Param("storageId") Long storageId,
                               Pageable pageable);

    @Query("from TicketImport i where i.status=:status and i.createdBy=:createdBy")
    TicketImport findDraftGoodsImportPresent(@Param("status") String status, @Param("createdBy") Long createdBy);
}