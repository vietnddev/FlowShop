package com.flowiee.pms.modules.system.repository;

import com.flowiee.pms.modules.system.entity.MailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailStatusRepository extends JpaRepository<MailStatus, Long> {
}