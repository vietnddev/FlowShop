package com.flowiee.pms.modules.product.repository;

import com.flowiee.pms.modules.product.entity.ImageCrawled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageCrawlerRepository extends JpaRepository<ImageCrawled, Long> {
    List<ImageCrawled> findByProductId(Long productId);
}