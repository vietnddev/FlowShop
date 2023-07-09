package com.flowiee.app.sanpham.controller;

import com.flowiee.app.common.authorization.KiemTraQuyenModuleSanPham;
import com.flowiee.app.file.service.FileStorageService;
import com.flowiee.app.hethong.service.AccountService;
import com.flowiee.app.common.utils.DateUtil;
import com.flowiee.app.common.utils.PagesUtil;
import com.flowiee.app.sanpham.entity.BienTheSanPham;
import com.flowiee.app.sanpham.entity.GiaSanPham;
import com.flowiee.app.sanpham.entity.ThuocTinhSanPham;
import com.flowiee.app.sanpham.model.TrangThai;
import com.flowiee.app.sanpham.services.BienTheSanPhamService;
import com.flowiee.app.sanpham.services.GiaSanPhamService;
import com.flowiee.app.sanpham.services.ThuocTinhSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(path = "/san-pham/variant")
public class SanPhamBienTheController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private BienTheSanPhamService bienTheSanPhamService;
    @Autowired
    private ThuocTinhSanPhamService thuocTinhSanPhamService;
    @Autowired
    private GiaSanPhamService giaSanPhamService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private KiemTraQuyenModuleSanPham kiemTraQuyenModuleSanPham;

    @GetMapping(value = "{id}")
    public ModelAndView showDetailProduct(@PathVariable("id") int id) {
        /* Show trang chi tiết của biến thể
         * */
        if (!accountService.isLogin()) {
            return new ModelAndView(PagesUtil.PAGE_LOGIN);
        }
        ModelAndView modelAndView = new ModelAndView(PagesUtil.PAGE_SANPHAM_BIENTHE);
        modelAndView.addObject("bienTheSanPham", new BienTheSanPham());
        modelAndView.addObject("thuocTinhSanPham", new ThuocTinhSanPham());
        modelAndView.addObject("giaBanSanPham", new GiaSanPham());
        modelAndView.addObject("listThuocTinh", thuocTinhSanPhamService.getAllAttributes(id));
        modelAndView.addObject("bienTheSanPhamId", id);
        modelAndView.addObject("bienTheSanPham", bienTheSanPhamService.findById(id));
        modelAndView.addObject("listImageOfSanPhamBienThe", fileStorageService.getImageOfSanPhamBienThe(id));
        modelAndView.addObject("listPrices", giaSanPhamService.findByBienTheSanPhamId(id));
        return modelAndView;
    }

    @PostMapping(value = "/insert")
    public String insertVariants(HttpServletRequest request, @ModelAttribute("bienTheSanPham") BienTheSanPham bienTheSanPham) {
        if (!accountService.isLogin()) {
            return PagesUtil.PAGE_LOGIN;
        }
        bienTheSanPham.setTrangThai(TrangThai.KINH_DOANH.name());
        bienTheSanPham.setMaSanPham(DateUtil.now("yyyyMMddHHmmss"));
        bienTheSanPhamService.save(bienTheSanPham);
        //Khởi tạo giá default của giá bán
        giaSanPhamService.save(GiaSanPham.builder().bienTheSanPham(bienTheSanPham).giaBan(0D).trangThai(true).build());
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping(value = "/update/{id}")
    public String update(HttpServletRequest request, @PathVariable("id") int id) {
        if (!accountService.isLogin()) {
            return PagesUtil.PAGE_LOGIN;
        }
        if (bienTheSanPhamService.findById(id) != null) {
            //
            System.out.println("Update successfully");
        } else {
            System.out.println("Record not found!");
        }
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping(value = "/delete/{id}")
    public String delete(HttpServletRequest request, @PathVariable("variantID") int variantID) {
        if (!accountService.isLogin()) {
            return PagesUtil.PAGE_LOGIN;
        }
        if (bienTheSanPhamService.findById(variantID) != null) {
            bienTheSanPhamService.detele(variantID);
            System.out.println("Delete successfully");
        } else {
            System.out.println("Record not found!");
        }
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping(value = "/gia-ban/update/{id}")
    public String updateGiaBan(HttpServletRequest request,
                               @ModelAttribute("giaSanPham") GiaSanPham giaSanPham,
                               @PathVariable("id") int idBienTheSanPham) {
        if (!accountService.isLogin()) {
            return PagesUtil.PAGE_LOGIN;
        }
        if (kiemTraQuyenModuleSanPham.kiemTraQuyenQuanLyGiaBan()) {
            int idGiaBanHienTai = Integer.parseInt(request.getParameter("idGiaBan"));
            giaSanPhamService.update(giaSanPham, idBienTheSanPham, idGiaBanHienTai);
        }
        return "redirect:" + request.getHeader("referer");
    }
}