package com.flowiee.pms.product.controller;

import com.flowiee.pms.shared.base.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.prefix}/product-damaged")
@Tag(name = "Product API", description = "Quản lý sản phẩm hư hỏng")
@RequiredArgsConstructor
public class ProductDamagedController extends BaseController {

}