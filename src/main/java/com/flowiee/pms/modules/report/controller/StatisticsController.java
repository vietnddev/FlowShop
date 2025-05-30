package com.flowiee.pms.modules.report.controller;

import com.flowiee.pms.common.base.controller.BaseController;
import com.flowiee.pms.common.base.controller.ControllerHelper;
import com.flowiee.pms.modules.report.entity.Report;
import com.flowiee.pms.common.model.AppResponse;
import com.flowiee.pms.modules.report.dto.ReportDTO;
import com.flowiee.pms.modules.report.model.DefectiveProductStatisticsModel;
import com.flowiee.pms.modules.report.model.OrderSalesChannelStatisticsModel;
import com.flowiee.pms.modules.report.model.SalesPerformanceStatisticsModel;
import com.flowiee.pms.modules.report.service.ReportService;
import com.flowiee.pms.modules.report.service.ProductStatisticsService;
import com.flowiee.pms.modules.report.service.RevenueStatisticsService;
import com.flowiee.pms.modules.report.service.SalesPerformanceStatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/statistics")
@Tag(name = "Statistics API", description = "Statistics")
@RequiredArgsConstructor
public class StatisticsController extends BaseController {
    private final SalesPerformanceStatisticsService salesPerformanceStatisticsService;
    private final RevenueStatisticsService revenueStatisticsService;
    private final ProductStatisticsService productStatisticsService;
    private final ReportService reportService;
    private final ControllerHelper mvCHelper;

    private AppResponse<ReportDTO> generateResult(Report pReport, Object pReportData) {
        return mvCHelper.success(ReportDTO.builder()
                .reportId(pReport.getReportId())
                .reportName(pReport.getReportName())
                .data(pReportData)
                .build());
    }

    @GetMapping("/CORSLS001")
    public AppResponse<ReportDTO> getRevenueDay(@RequestParam(value = "date", required = false) LocalDate date) {
        Report lvReport = reportService.findById("CORSLS001", true);
        BigDecimal lvData = revenueStatisticsService.getDayRevenue(date, date);
        return generateResult(lvReport, lvData);
    }

    @GetMapping("/CORSLS002")
    public AppResponse<ReportDTO> getRevenueWeek() {
        Report lvReport = reportService.findById("CORSLS002", true);
        BigDecimal lvData = revenueStatisticsService.getWeekRevenue();
        return generateResult(lvReport, lvData);
    }

    @GetMapping("/CORSLS003")
    public AppResponse<ReportDTO> getRevenueMonth(@RequestParam(value = "fmonth", required = false) Integer pFMonth,
                                                  @RequestParam(value = "fyear", required = false) Integer pFYear,
                                                  @RequestParam(value = "tmonth", required = false) Integer pTMonth,
                                                  @RequestParam(value = "tyear", required = false) Integer pTYear) {
        Report lvReport = reportService.findById("CORSLS003", true);
        BigDecimal lvData = revenueStatisticsService.getMonthRevenue(pFMonth, pFYear, pTMonth, pTYear);
        return generateResult(lvReport, lvData);
    }

    @GetMapping("/CORSLS004")
    public AppResponse<ReportDTO> getPerformanceEmployee() {
        Report lvReport = reportService.findById("CORSLS004", true);
        List<SalesPerformanceStatisticsModel> lvData = salesPerformanceStatisticsService.getPerformanceEmployee();
        return generateResult(lvReport, lvData);
    }

    @GetMapping("/CORSLS005")
    public AppResponse<ReportDTO> getOrderCountsBySalesChannel() {
        Report lvReport = reportService.findById("CORSLS005", true);
        List<OrderSalesChannelStatisticsModel> lvData = salesPerformanceStatisticsService.getOrderBySalesChannel();
        return generateResult(lvReport, lvData);
    }

    @GetMapping("/CORSLS006")
    public AppResponse<ReportDTO> getRateOrdersSoldOnOnlineChannels() {
        Report lvReport = reportService.findById("CORSLS006", true);
        String lvData = salesPerformanceStatisticsService.getRateOrdersSoldOnOnlineChannels() + " %";
        return generateResult(lvReport, lvData);
    }

    @GetMapping("/CORPRD001")
    public AppResponse<ReportDTO> getDefectiveProduct() {
        Report lvReport = reportService.findById("CORPRD001", true);
        List<DefectiveProductStatisticsModel> lvData = productStatisticsService.getDefectiveProduct();
        return generateResult(lvReport, lvData);
    }
}