package com.flowiee.app.sanpham.services.impl;

import com.flowiee.app.hethong.model.action.SanPhamAction;
import com.flowiee.app.hethong.model.module.SystemModule;
import com.flowiee.app.hethong.service.SystemLogService;
import com.flowiee.app.sanpham.entity.BienTheSanPham;
import com.flowiee.app.sanpham.entity.ThuocTinhSanPham;
import com.flowiee.app.sanpham.repository.ThuocTinhSanPhamRepository;
import com.flowiee.app.sanpham.services.ThuocTinhSanPhamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ThuocTinhSanPhamServiceImpl implements ThuocTinhSanPhamService {
    private static final Logger logger = LoggerFactory.getLogger(ThuocTinhSanPhamServiceImpl.class);
    private static final String module = SystemModule.SAN_PHAM.getLabel();

    @Autowired
    private ThuocTinhSanPhamRepository productAttributeRepository;
    @Autowired
    private SystemLogService systemLogService;


    @Override
    public List<ThuocTinhSanPham> getAllAttributes(int productVariantID){
        BienTheSanPham bienTheSanPham = new BienTheSanPham();
        bienTheSanPham.setId(productVariantID);
        return productAttributeRepository.findByBienTheSanPham(bienTheSanPham);
    }

    @Override
    public void saveAttribute(ThuocTinhSanPham productAttribute){
        productAttributeRepository.save(productAttribute);
        systemLogService.writeLog(module, SanPhamAction.UPDATE_SANPHAM.name(), "Thêm mới thuộc tính sản phẩm");
    }

    @Override
    public String updateAttribute(ThuocTinhSanPham attribute, Integer attributeId) {
        systemLogService.writeLog(module, SanPhamAction.UPDATE_SANPHAM.name(), "Cập nhật thuộc tính sản phẩm");
        return null;
    }

    @Override
    public ThuocTinhSanPham findById(int attributeID){
    	return productAttributeRepository.findById(attributeID).orElse(null);
    }

    @Override
    public void deleteAttribute(int attributeID) {
    	productAttributeRepository.deleteById(attributeID);
        systemLogService.writeLog(module, SanPhamAction.UPDATE_SANPHAM.name(), "Xóa thuộc tính sản phẩm");
    }
}