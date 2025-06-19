package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.modules.sales.service.PromotionApplyService;
import com.flowiee.pms.modules.sales.entity.PromotionApply;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.modules.sales.dto.PromotionApplyDTO;
import com.flowiee.pms.modules.sales.repository.PromotionApplyRepository;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PromotionApplyServiceImpl implements PromotionApplyService {
    ModelMapper              mvModelMapper;
    PromotionApplyRepository mvPromotionApplyRepository;

    @Override
    public List<PromotionApplyDTO> findAll(Long voucherInfoId , Long productId) {
        return List.of();
    }

    @Override
    public List<PromotionApplyDTO> findAll() {
        return List.of();
    }

    @Override
    public List<PromotionApplyDTO> findByPromotionId(Long voucherId) {
        List<PromotionApply> promotionApply = mvPromotionApplyRepository.findByPromotionId(voucherId);
        Type listType = new TypeToken<List<PromotionApplyDTO>>() {}.getType();
        return mvModelMapper.map(promotionApply, listType);
    }

    @Override
    public PromotionApplyDTO findById(Long promotionId, boolean pThrowException) {
        Optional<PromotionApply> promotionApply = mvPromotionApplyRepository.findById(promotionId);
        if (promotionApply.isPresent()) {
            PromotionApplyDTO dto = mvModelMapper.map(promotionApply.get(), PromotionApplyDTO.class);
            return dto;
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"promotion apply record"}, null, null);
        } else {
            return null;
        }
    }

    @Override
    public PromotionApplyDTO save(PromotionApplyDTO promotionApplyDTO) {
        PromotionApply promotionApply = PromotionApply.fromDTO(promotionApplyDTO);
        PromotionApply promotionApplySaved = mvPromotionApplyRepository.save(promotionApply);
        return mvModelMapper.map(promotionApplySaved, PromotionApplyDTO.class);
    }

    @Override
    public PromotionApplyDTO update(PromotionApplyDTO pPromotionApplyDTO, Long id) {
        PromotionApplyDTO promotionApplyDTO = this.findById(id, true);
        //
        return mvPromotionApplyRepository.save(pPromotionApplyDTO);
    }

    @Override
    public String delete(Long entityId) {
        if (this.findById(entityId, true) == null) {
            mvPromotionApplyRepository.deleteById(entityId);
        }
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

//    private List<PromotionApplyDTO> extractDataQuery(List<Object[]> objects) {
//        List<PromotionApplyDTO> dataResponse = new ArrayList<>();
//        for (Object[] data : objects) {
//            PromotionApplyDTO promotionApplyDTO = new PromotionApplyDTO();
//            promotionApplyDTO.setPromotionApplyId(Integer.parseInt(String.valueOf(data[0])));
//            promotionApplyDTO.setPromotionId(Integer.parseInt(String.valueOf(data[1])));
//            promotionApplyDTO.setPromotionInfoTitle(String.valueOf(data[2]));
//            promotionApplyDTO.setProductId(Integer.parseInt(String.valueOf(data[3])));
//            promotionApplyDTO.setProductName(String.valueOf(data[4]));
//            promotionApplyDTO.setAppliedAt((String.valueOf(data[5])).substring(0, 10));
//            promotionApplyDTO.setAppliedBy(Integer.parseInt(String.valueOf(data[6])));
//            dataResponse.add(promotionApplyDTO);
//        }
//        return dataResponse;
//    }
}