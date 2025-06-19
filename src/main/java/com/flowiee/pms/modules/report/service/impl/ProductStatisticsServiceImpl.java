package com.flowiee.pms.modules.report.service.impl;

import com.flowiee.pms.modules.inventory.entity.ProductDetail;
import com.flowiee.pms.modules.report.model.DefectiveProductStatisticsModel;
import com.flowiee.pms.modules.inventory.repository.ProductDetailRepository;
import com.flowiee.pms.modules.report.service.ProductStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStatisticsServiceImpl implements ProductStatisticsService {
    private final ProductDetailRepository productVariantRepository;

    @Override
    public List<DefectiveProductStatisticsModel> getDefectiveProduct() {
        List<DefectiveProductStatisticsModel> lvReturnList = new ArrayList<>();
        List<ProductDetail> lvProducts = productVariantRepository.findDefective();
        for (ProductDetail p : lvProducts) {
            String lvProductName = p.getVariantName();
            BigDecimal defectiveQuantity = new BigDecimal(p.getDefectiveQty());
            BigDecimal lvTotalQuantity = new BigDecimal(p.getStorageQty());
            BigDecimal lvRate = lvTotalQuantity.compareTo(BigDecimal.ZERO) > 0
                    ? defectiveQuantity.divide(lvTotalQuantity).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            lvReturnList.add(DefectiveProductStatisticsModel.builder()
                    .productName(lvProductName)
                    .defectiveQuantity(defectiveQuantity.intValue())
                    .totalQuantity(lvTotalQuantity.intValue())
                    .rate(lvRate.toPlainString() + " %")
                    .build());
        }
        return lvReturnList;
    }
}