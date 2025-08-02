package com.flowiee.pms.modules.sales.dto;

import com.flowiee.pms.common.base.dto.BaseDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderCartDTO extends BaseDTO implements Serializable {
    private Long salesChannelId;
    private Long paymentMethodId;
    private List<ItemsDTO> items;
    private Boolean isFinish;

    @Override
    public String toString() {
        return "OrderCartDTO{" +
                "id=" + id +
                '}';
    }
}