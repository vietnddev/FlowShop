package com.flowiee.pms.controller.storage;

import com.flowiee.pms.base.controller.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.prefix}/stg/transaction-goods")
@Tag(name = "Ticket import API", description = "Quản lý nhập hàng")
@RequiredArgsConstructor
public class TransactionGoodsController extends BaseController {
}