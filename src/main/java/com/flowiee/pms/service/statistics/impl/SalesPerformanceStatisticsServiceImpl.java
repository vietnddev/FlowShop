package com.flowiee.pms.service.statistics.impl;

import com.flowiee.pms.entity.sales.Order;
import com.flowiee.pms.entity.system.Account;
import com.flowiee.pms.entity.system.GroupAccount;
import com.flowiee.pms.model.dto.OrderDTO;
import com.flowiee.pms.model.statistics.OrderSalesChannelStatisticsModel;
import com.flowiee.pms.model.statistics.SalesPerformanceStatisticsModel;
import com.flowiee.pms.repository.sales.OrderRepository;
import com.flowiee.pms.repository.system.AccountRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.OrderReadService;
import com.flowiee.pms.service.statistics.SalesPerformanceStatisticsService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesPerformanceStatisticsServiceImpl extends BaseService implements SalesPerformanceStatisticsService {
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final OrderReadService orderReadService;
    private final ModelMapper modelMapper;

    @Override
    public List<SalesPerformanceStatisticsModel> getPerformanceEmployee() {
        List<SalesPerformanceStatisticsModel> returnList = new ArrayList<>();
        List<Account> employeeList = accountRepository.findAll();
        for (Account employee : employeeList) {
            List<OrderDTO> orderList = orderReadService.findAll(-1, -1,
                    null, null, null, null, null, employee.getId(),
                    null, null, null, null, null, null, null).getContent();

            String lvEmployeeName = employee.getFullName();
            GroupAccount lvGroupEmployee = employee.getGroupAccount();
            String lvEmployeePosition = lvGroupEmployee != null ? lvGroupEmployee.getGroupName() : "-";

            BigDecimal lvTotalRevenue = BigDecimal.ZERO;
            int lvNumberOfProductsSold = 0;
            for (OrderDTO d : orderList) {
                BigDecimal lvRevenue = OrderUtils.calTotalAmount(d.getListOrderDetail(), d.getAmountDiscount());
                lvTotalRevenue = lvTotalRevenue.add(lvRevenue);
                lvNumberOfProductsSold += OrderUtils.countItemsEachOrder(d.getListOrderDetail());
            }

            Integer lvTotalTransactions = orderList.size();
            Float lvTargetAchievementRate = 0f;
            String lvEffectiveSalesTime= "";

            returnList.add(SalesPerformanceStatisticsModel.builder()
                    .employeeName(lvEmployeeName)
                    .employeePosition(lvEmployeePosition)
                    .totalRevenue(lvTotalRevenue)
                    .totalTransactions(lvTotalTransactions)
                    .targetAchievementRate(lvTargetAchievementRate)
                    .effectiveSalesTime(lvEffectiveSalesTime)
                    .numberOfProductsSold(lvNumberOfProductsSold)
                    .build());
        }
        return returnList;
    }

    @Override
    public List<OrderSalesChannelStatisticsModel> getOrderBySalesChannel() {
        List<OrderSalesChannelStatisticsModel> lvReturnData = new ArrayList<>();
        String lvSQL = "WITH SALES_CHANNEL_TEMP(SALESCHANNELID, NAME, COLOR) AS ( " +
                       "    SELECT ID, NAME, COLOR FROM CATEGORY WHERE TYPE = 'SALES_CHANNEL' AND CODE <> 'ROOT' " +
                       "), " +
                       "ORDER_TEMP(SALESCHANNELID, VALUE) AS ( " +
                       "    SELECT o.CHANNEL, NVL(((d.PRICE * d.QUANTITY) - d.EXTRA_DISCOUNT) - o.AMOUNT_DISCOUNT, 0) AS VALUE " +
                       "    FROM ORDERS o " +
                       "    LEFT JOIN ORDER_DETAIL d ON d.ORDER_ID = o.ID " +
                       "), " +
                       "DATA AS ( " +
                       "    SELECT c.SALESCHANNELID as v0, c.NAME as v1, c.COLOR as v2, SUM(o.VALUE) AS v3 " +
                       "    FROM SALES_CHANNEL_TEMP c " +
                       "    LEFT JOIN ORDER_TEMP o ON o.SALESCHANNELID = c.SALESCHANNELID " +
                       "    GROUP BY c.SALESCHANNELID, c.NAME, c.COLOR " +
                       ") " +
                       "SELECT * FROM DATA";
        Query lvQuery = mvEntityManager.createNativeQuery(lvSQL);
        List<Object[]> lvRawData = lvQuery.getResultList();
        for (Object[] obj : lvRawData) {
            Long lvSalesChannelId = Long.parseLong(CoreUtils.trim(obj[0]));
            String lvSalesChannelName = CoreUtils.trim(obj[1]);
            String lvLabelColor = CoreUtils.trim(obj[2]);
            BigDecimal lvValueOfOrders = new BigDecimal(CoreUtils.trim(obj[3]));
            List<Order> lvOrderListBySalesChannel = orderRepository.countBySalesChannel(lvSalesChannelId);
            Integer lvNumberOfOrders = lvOrderListBySalesChannel.size();
            Integer lvNumberOfProducts = OrderUtils.countItemsListOrder_(lvOrderListBySalesChannel);

            lvReturnData.add(OrderSalesChannelStatisticsModel.builder()
                    .salesChannelName(lvSalesChannelName)
                    .labelColor(lvLabelColor)
                    .numberOfOrders(lvNumberOfOrders)
                    .valueOfOrders(lvValueOfOrders)
                    .numberOfProducts(lvNumberOfProducts)
                    .build());
        }
        mvEntityManager.close();

        return lvReturnData;
    }

    @Override
    public Float getRateOrdersSoldOnOnlineChannels() {
        List<Order> lvOfflineOrdersList = orderRepository.countBySalesChannel("OFF");
        float lvOfflineOrdersQty = lvOfflineOrdersList.size();
        float lvTotalOrdersQty = orderRepository.count();
        float lvRate = lvOfflineOrdersQty / lvTotalOrdersQty * 100;
        return lvRate;
    }
}