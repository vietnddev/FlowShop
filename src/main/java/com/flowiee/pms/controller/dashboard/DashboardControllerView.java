package com.flowiee.pms.controller.dashboard;

import com.flowiee.pms.model.DashboardModel;
import com.flowiee.pms.base.BaseController;
import com.flowiee.pms.common.enumeration.Pages;
import com.flowiee.pms.service.dashboard.DashboardService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DashboardControllerView extends BaseController {
    DashboardService mvDashboardService;

    @GetMapping
    @PreAuthorize("@vldDashboard.readDashboard(true)")
    public ModelAndView reportDoanhThu() {
        ModelAndView modelAndView = new ModelAndView(Pages.PRO_DASHBOARD.getTemplate());

        DashboardModel dashboardModel = mvDashboardService.loadDashboard();

        //Today
        //Doanh thu hôm nay
        modelAndView.addObject("doanhThuHomNay", dashboardModel.getRevenueToday());
        //Số lượng đơn hàng hôm nay
        modelAndView.addObject("soLuongDonHangHomNay", dashboardModel.getOrdersNewTodayQty());
        modelAndView.addObject("danhSachDonHangHomNay", dashboardModel.getListOrdersToday());

        //Total products
        modelAndView.addObject("totalProducts", dashboardModel.getTotalProducts());

        //Doanh thu trong tháng này
        modelAndView.addObject("doanhThuThangNay", dashboardModel.getRevenueThisMonth());
        //Số lượng khách hàng mới trong tháng
        modelAndView.addObject("khachHangMoiTrongThang", dashboardModel.getCustomersNewInMonthQty());
        modelAndView.addObject("danhSachkhachHangMoiTrongThang", dashboardModel.getListCustomersNewInMonth());

        //Pie chart - Doanh thu theo kênh bán hàng
        modelAndView.addObject("doanhThuOfKBH_listTen", dashboardModel.getRevenueSalesChannel().keySet());
        modelAndView.addObject("doanhThuOfKBH_listDoanhThu", dashboardModel.getRevenueSalesChannel().values());
        modelAndView.addObject("doanhThuOfKBH_listMauSac", null);

        //Line chart - Doanh thu các tháng theo năm
        modelAndView.addObject("doanhThuOfMonth_listDoanhThu", dashboardModel.getRevenueMonthOfYear().values());

        //Line chart - Doanh thu các ngày theo tháng
        modelAndView.addObject("doanhThuOfDay_listNgay", dashboardModel.getRevenueDayOfMonth().keySet());
        modelAndView.addObject("doanhThuOfDay_listDoanhThu", dashboardModel.getRevenueDayOfMonth().values());

        //Bar chart - Top sản phẩm bán chạy
        modelAndView.addObject("topSanPham_listTenSanPham", dashboardModel.getProductsTopSellQty().keySet());
        modelAndView.addObject("topSanPham_listSoLuong", dashboardModel.getProductsTopSellQty().values());

        //Bar chart - Top sản phẩm bán chạy
        //modelAndView.addObject("topSanPhamx_listTenSanPham", dashboardModel.getProductsTopSellRevenue().keySet());
        //modelAndView.addObject("topSanPhamx_listSoLuong", dashboardModel.getProductsTopSellRevenue().values());

        return baseView(modelAndView);
    }
}