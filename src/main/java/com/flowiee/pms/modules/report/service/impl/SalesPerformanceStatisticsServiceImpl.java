package com.flowiee.pms.modules.report.service.impl;

import com.flowiee.pms.modules.report.service.SalesPerformanceStatisticsService;
import com.flowiee.pms.modules.sales.entity.Order;
import com.flowiee.pms.modules.sales.model.OrderReq;
import com.flowiee.pms.modules.staff.entity.Account;
import com.flowiee.pms.modules.staff.entity.GroupAccount;
import com.flowiee.pms.modules.sales.dto.OrderDTO;
import com.flowiee.pms.modules.report.model.OrderSalesChannelStatisticsModel;
import com.flowiee.pms.modules.report.model.SalesPerformanceStatisticsModel;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.modules.staff.repository.AccountRepository;
import com.flowiee.pms.modules.sales.service.OrderService;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.common.utils.OrderUtils;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesPerformanceStatisticsServiceImpl implements SalesPerformanceStatisticsService {
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final OrderService mvOrderService;
    private final ModelMapper modelMapper;
    private final EntityManager mvEntityManager;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<SalesPerformanceStatisticsModel> getPerformanceEmployee() {
        List<SalesPerformanceStatisticsModel> returnList = new ArrayList<>();
        List<Account> employeeList = accountRepository.findAll();
        for (Account employee : employeeList) {
            List<OrderDTO> orderList = mvOrderService.find(OrderReq.builder().sellerId(employee.getId()).build())
                    .getContent();

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
                       "    SELECT o.CHANNEL, COALESCE(((d.PRICE * d.QUANTITY) - d.EXTRA_DISCOUNT) - o.AMOUNT_DISCOUNT, 0) AS VALUE " +
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
            Integer lvNumberOfProducts = OrderUtils.countItems(lvOrderListBySalesChannel);

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