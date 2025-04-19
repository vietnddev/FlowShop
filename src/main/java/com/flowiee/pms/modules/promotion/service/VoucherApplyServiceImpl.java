package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.promotion.dto.VoucherApplyDTO;
import com.flowiee.pms.modules.promotion.entity.VoucherApply;
import com.flowiee.pms.modules.promotion.repository.VoucherApplyRepository;
import com.flowiee.pms.common.base.service.BaseService;

import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherApplyServiceImpl extends BaseService implements VoucherApplyService {
    VoucherApplyRepository mvVoucherApplyRepository;

    @Override
    public List<VoucherApplyDTO> findAll(Long voucherInfoId , Long productId) {
        return this.extractDataQuery(mvVoucherApplyRepository.findAll((Long) null));
    }

    @Override
    public List<VoucherApplyDTO> findByProductId(Long productId) {
        return this.extractDataQuery(mvVoucherApplyRepository.findAll(productId));
    }

    @Override
    public List<VoucherApply> findAll() {
        return mvVoucherApplyRepository.findAll();
    }

    @Override
    public List<VoucherApply> findByVoucherId(Long voucherId) {
        return mvVoucherApplyRepository.findByVoucherId(voucherId);
    }

    @Override
    public VoucherApply findById(Long id, boolean pThrowException) {
        Optional<VoucherApply> entityOptional = mvVoucherApplyRepository.findById(id);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"voucher apply"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public VoucherApply save(VoucherApply voucherApply) {
        return mvVoucherApplyRepository.save(voucherApply);
    }

    @Override
    public VoucherApply update(VoucherApply pVoucherApply, Long id) {
        VoucherApply voucherApply = this.findById(id, true);
        //voucherApply.set
        //voucherApply.set
        //voucherApply.set
        return mvVoucherApplyRepository.save(voucherApply);
    }

    @Override
    public String delete(Long entityId) {
        VoucherApply voucherApply = this.findById(entityId, true);
        mvVoucherApplyRepository.deleteById(voucherApply.getId());
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private List<VoucherApplyDTO> extractDataQuery(List<Object[]> objects) {
        List<VoucherApplyDTO> dataResponse = new ArrayList<>();
        for (Object[] data : objects) {
            dataResponse.add(VoucherApplyDTO.builder()
                    .voucherApplyId(Long.parseLong(String.valueOf(data[0])))
                    .voucherInfoId(Long.parseLong(String.valueOf(data[1])))
                    .voucherInfoTitle(String.valueOf(data[2]))
                    .productId(Long.parseLong(String.valueOf(data[3])))
                    .productName(String.valueOf(data[4]))
                    .appliedAt((String.valueOf(data[5])).substring(0, 10))
                    .appliedBy(Long.parseLong(String.valueOf(data[6])))
                    .build());
        }
        return dataResponse;
    }
}