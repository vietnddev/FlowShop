package com.flowiee.pms.service.statistics.impl;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.entity.product.ProductDetail;
import com.flowiee.pms.model.statistics.DefectiveProductStatisticsModel;
import com.flowiee.pms.repository.product.ProductDetailRepository;
import com.flowiee.pms.service.statistics.ProductStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStatisticsServiceImpl extends BaseService implements ProductStatisticsService {
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