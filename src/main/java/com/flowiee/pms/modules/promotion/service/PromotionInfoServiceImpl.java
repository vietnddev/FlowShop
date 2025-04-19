package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.modules.promotion.entity.PromotionInfo;
import com.flowiee.pms.common.exception.*;
import com.flowiee.pms.modules.product.dto.ProductDTO;
import com.flowiee.pms.modules.promotion.dto.PromotionApplyDTO;
import com.flowiee.pms.modules.promotion.dto.PromotionInfoDTO;
import com.flowiee.pms.modules.promotion.repository.PromotionInfoRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.product.service.ProductInfoService;
import com.flowiee.pms.modules.system.service.MailMediaService;
import com.flowiee.pms.common.enumeration.ErrorCode;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.common.enumeration.PromotionStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PromotionInfoServiceImpl extends BaseService implements PromotionService {
    ModelMapper             mvModelMapper;
    MailMediaService        mvMailMediaService;
    ProductInfoService      mvProductInfoService;
    PromotionApplyService   mvPromotionApplyService;
    PromotionInfoRepository mvPromotionInfoRepository;

    @Override
    public List<PromotionInfoDTO> findAll() {
        Page<PromotionInfoDTO> promotionInfos = this.findAll(-1, -1, null, null, null, null);
        return promotionInfos.getContent();
    }

    @Override
    public Page<PromotionInfoDTO> findAll(int pageSize, int pageNum, String pTitle, LocalDateTime pStartTime, LocalDateTime pEndTime, String pStatus) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());
        LocalDateTime lvStartTime = getFilterStartTime(pStartTime);
        LocalDateTime lvEndTime = getFilterEndTime(pEndTime);

        Page<PromotionInfo> pagePromotionInfos = mvPromotionInfoRepository.findAll(pTitle, lvStartTime, lvEndTime, pStatus, pageable);

        Type listType = new TypeToken<List<PromotionInfoDTO>>() {}.getType();
        List<PromotionInfoDTO> promotionInfoDTOs = mvModelMapper.map(pagePromotionInfos.getContent(), listType);

        for (PromotionInfoDTO p : promotionInfoDTOs) {
            List<ProductDTO> productDTOs = new ArrayList<>();
            for (PromotionApplyDTO promotionApplyDTO : mvPromotionApplyService.findByPromotionId(p.getId())) {
                ProductDTO productDTOOptional = mvProductInfoService.findById(promotionApplyDTO.getProductId(), false);
                if (productDTOOptional != null) {
                    productDTOs.add(productDTOOptional);
                }
            }
            p.setApplicableProducts(productDTOs);
            p.setStatus(genPromotionStatus(p.getStartTime(), p.getEndTime()));
        }

        return new PageImpl<>(promotionInfoDTOs, pageable, pagePromotionInfos.getTotalElements());
    }

    @Override
    public PromotionInfoDTO findById(Long entityId, boolean pThrowException) {
        Optional<PromotionInfo> promotionInfo = mvPromotionInfoRepository.findById(entityId);
        if (promotionInfo.isPresent()) {
            PromotionInfoDTO dto = mvModelMapper.map(promotionInfo.get(), PromotionInfoDTO.class);
            dto.setStatus(genPromotionStatus(dto.getStartTime(), dto.getEndTime()));
            return dto;
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"promotion"}, null, null);
        }
        return null;
    }

    @Transactional
    @Override
    public PromotionInfoDTO save(PromotionInfoDTO promotionInfoDTO) {
        if (promotionInfoDTO == null) {
            throw new BadRequestException();
        }
        try {
            LocalDateTime lvStartTime = LocalDateTime.parse(promotionInfoDTO.getStartTimeStr() + "T00:00");
            LocalDateTime lvEndTime = LocalDateTime.parse(promotionInfoDTO.getEndTimeStr() + "T00:00");

            PromotionInfo promotionInfo = PromotionInfo.fromDTO(promotionInfoDTO);
            promotionInfo.setStartTime(lvStartTime);
            promotionInfo.setEndTime(lvEndTime);

            PromotionInfo promotionInfoSaved = mvPromotionInfoRepository.save(promotionInfo);
            for (ProductDTO product : promotionInfoDTO.getApplicableProducts()) {
                mvProductInfoService.findById(product.getId(), true);
                PromotionApplyDTO promotionApply = new PromotionApplyDTO();
                promotionApply.setPromotionId(promotionInfoSaved.getId());
                promotionApply.setProductId(product.getId());
                mvPromotionApplyService.save(promotionApply);
            }
            PromotionInfoDTO dto = mvModelMapper.map(promotionInfoSaved, PromotionInfoDTO.class);
            dto.setStatus(genPromotionStatus(dto.getStartTime(), dto.getEndTime()));
            return dto;
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Override
    public PromotionInfoDTO update(PromotionInfoDTO inputDTO, Long promotionId) {
        try {
            if (inputDTO == null || promotionId == null || this.findById(promotionId, true) == null) {
                throw new ResourceNotFoundException("Product promotion not found!");
            }
            LocalDateTime lvStartTime = LocalDateTime.parse(inputDTO.getStartTimeStr() + "T00:00");
            LocalDateTime lvEndTime = LocalDateTime.parse(inputDTO.getEndTimeStr() + "T00:00");

            PromotionInfo promotionInfo = PromotionInfo.fromDTO(inputDTO);
            promotionInfo.setStartTime(lvStartTime);
            promotionInfo.setEndTime(lvEndTime);
            promotionInfo.setId(promotionId);
            PromotionInfo promotionInfoSaved = mvPromotionInfoRepository.save(promotionInfo);
            return mvModelMapper.map(promotionInfoSaved, PromotionInfoDTO.class);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "promotion"), ex);
        }
    }

    @Override
    public String delete(Long promotionId) {
        if (this.findById(promotionId, true) == null) {
            throw new ResourceNotFoundException("Promotion not found!");
        }
        if (!mvPromotionApplyService.findByPromotionId(promotionId).isEmpty()) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }
        mvPromotionInfoRepository.deleteById(promotionId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private String genPromotionStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime currentDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        startTime = startTime.withHour(0).withMinute(0).withSecond(0);
        endTime = endTime.withHour(0).withMinute(0).withSecond(0);

        if ((startTime.isBefore(currentDate) || startTime.equals(currentDate)) && (endTime.isAfter(currentDate) || endTime.equals(currentDate))) {
            return PromotionStatus.A.getLabel();
        } else {
            return PromotionStatus.I.getLabel();
        }
    }

    @Override
    public void notifyToCustomer(List<Long> pCustomerIdList, Long pPromotionId) {
        if (ObjectUtils.isEmpty(pCustomerIdList)) {
            throw new BadRequestException("Vui lòng chọn khách hàng cần gửi thông báo!");
        }

        PromotionInfoDTO lvPromotionInfo = this.findById(pPromotionId, true);
        String lvPromotionName = lvPromotionInfo.getTitle();

        for (Long lvCustomerId : pCustomerIdList) {
            //write more business here
            //mvMailMediaService.send(null, null);
        }
    }
}