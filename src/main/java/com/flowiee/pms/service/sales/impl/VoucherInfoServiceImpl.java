package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.VoucherApply;
import com.flowiee.pms.entity.sales.VoucherInfo;
import com.flowiee.pms.entity.sales.VoucherTicket;
import com.flowiee.pms.exception.*;
import com.flowiee.pms.common.ChangeLog;
import com.flowiee.pms.common.enumeration.*;
import com.flowiee.pms.model.dto.ProductDTO;
import com.flowiee.pms.model.dto.VoucherInfoDTO;
import com.flowiee.pms.repository.sales.VoucherInfoRepository;

import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.VoucherApplyService;
import com.flowiee.pms.service.sales.VoucherService;
import com.flowiee.pms.service.sales.VoucherTicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherInfoServiceImpl extends BaseService implements VoucherService {
    ModelMapper           mvModelMapper;
    VoucherApplyService   mvVoucherApplyService;
    VoucherInfoRepository mvVoucherInfoRepository;
    @Autowired
    @NonFinal
    @Lazy
    VoucherTicketService  mvVoucherTicketService;

    @Override
    public List<VoucherInfoDTO> findAll() {
        Page<VoucherInfoDTO> voucherInfos = this.findAll(-1, -1, null, null, null, null, null);
        return voucherInfos.getContent();
    }

    @Override
    public Page<VoucherInfoDTO> findAll(int pageSize, int pageNum, List<Long> pIds, String pTitle, LocalDateTime pStartTime, LocalDateTime pEndTime, String pStatus) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("createdAt").descending());
        LocalDateTime lvStartTime = getFilterStartTime(pStartTime);
        LocalDateTime lvEndTime = getFilterEndTime(pEndTime);

        Page<VoucherInfo> pageVoucherInfoDTOs = mvVoucherInfoRepository.findAll(null, pTitle, lvStartTime, lvEndTime, pStatus, pageable);

        Type listType = new TypeToken<List<VoucherInfoDTO>>() {}.getType();
        List<VoucherInfoDTO> voucherInfoDTOs = mvModelMapper.map(pageVoucherInfoDTOs.getContent(), listType);

        for (VoucherInfoDTO v : voucherInfoDTOs) {
            v.setStatus(genVoucherStatus(v.getStartTime(), v.getEndTime()));
        }

        return new PageImpl<>(voucherInfoDTOs, pageable, pageVoucherInfoDTOs.getTotalElements());
    }

    @Override
    public VoucherInfoDTO findById(Long entityId, boolean pThrowException) {
        Optional<VoucherInfo> voucherInfo = mvVoucherInfoRepository.findById(entityId);
        if (voucherInfo.isPresent()) {
            VoucherInfoDTO dto = mvModelMapper.map(voucherInfo.get(), VoucherInfoDTO.class);
            dto.setStatus(genVoucherStatus(dto.getStartTime(), dto.getEndTime()));
            return dto;
        }
        if (pThrowException) {
            throw new EntityNotFoundException(new Object[] {"voucher info"}, null, null);
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public VoucherInfoDTO save(VoucherInfoDTO voucherInfo) {
        if (voucherInfo == null)
            throw new BadRequestException();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime lvStartTime = LocalDateTime.parse(voucherInfo.getStartTimeStr() + " 00:00:00", formatter);
            LocalDateTime lvEndTime = LocalDateTime.parse(voucherInfo.getEndTimeStr() + " 00:00:00", formatter);
            voucherInfo.setStartTime(lvStartTime);
            voucherInfo.setEndTime(lvEndTime);

            VoucherInfo voucherSaved = mvVoucherInfoRepository.save(VoucherInfo.fromVoucherDTO(voucherInfo));
            //
            for (ProductDTO product : voucherInfo.getApplicableProducts()) {
                mvVoucherApplyService.save(VoucherApply.builder()
                        .voucherId(voucherSaved.getId())
                        .productId(product.getId())
                        .build());
            }
            //Gen list voucher detail
            List<String> listKeyVoucher = new ArrayList<>();
            while (listKeyVoucher.size() < voucherInfo.getQuantity()) {
                String randomKey = "";
                while (randomKey.isEmpty()) {
                    String tempKey = generateRandomKeyVoucher(voucherInfo.getLength(), voucherInfo.getVoucherType());
                    if (mvVoucherTicketService.findByCode(tempKey) == null) {
                        randomKey = tempKey;
                    }
                }
                if (!listKeyVoucher.contains(randomKey)) {
                    VoucherTicket voucherTicketSaved = mvVoucherTicketService.save(VoucherTicket.builder()
                            .code(randomKey)
                            .length(voucherInfo.getLength())
                            .voucherInfo(voucherSaved)
                            .isUsed(false)
                            .build());
                    if (voucherTicketSaved != null) {
                        listKeyVoucher.add(randomKey);
                    }
                }
            }
            VoucherInfoDTO dto = mvModelMapper.map(voucherSaved, VoucherInfoDTO.class);
            dto.setStatus(genVoucherStatus(dto.getStartTime(), dto.getEndTime()));
            systemLogService.writeLogCreate(MODULE.PRODUCT, ACTION.PRO_VOU_C, MasterObject.VoucherInfo, "Thêm mới voucher", dto.getTitle());
            return dto;
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Override
    public VoucherInfoDTO update(VoucherInfoDTO voucherInfo, Long voucherId) {
        try {
            VoucherInfoDTO voucherOpt = this.findById(voucherId, true);

            VoucherInfoDTO voucherInfoBefore = ObjectUtils.clone(voucherOpt);
            voucherInfo.setId(voucherId);
            VoucherInfo voucherInfoUpdated = mvVoucherInfoRepository.save(voucherInfo);

            ChangeLog changeLog = new ChangeLog(voucherInfoBefore, voucherInfoUpdated);
            systemLogService.writeLogUpdate(MODULE.PRODUCT, ACTION.PRO_VOU_U, MasterObject.VoucherInfo, "Cập nhật voucher " + voucherInfoUpdated.getTitle(), changeLog.getOldValues(), changeLog.getNewValues());

            return mvModelMapper.map(voucherInfoUpdated, VoucherInfoDTO.class);
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR_OCCURRED.getDescription(), "voucher"), ex);
        }
    }

    @Override
    public String delete(Long voucherId) {
        VoucherInfoDTO voucherInfoBefore = this.findById(voucherId, true);

        if (!mvVoucherApplyService.findByVoucherId(voucherId).isEmpty()) {
            throw new DataInUseException(ErrorCode.ERROR_DATA_LOCKED.getDescription());
        }
        mvVoucherInfoRepository.deleteById(voucherId);
        systemLogService.writeLogDelete(MODULE.PRODUCT, ACTION.PRO_VOU_D, MasterObject.VoucherInfo, "Xóa voucher", voucherInfoBefore.getTitle());
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    private String generateRandomKeyVoucher(int lengthOfKey, String voucherType) {
        String characters = "";
        if (isNumberType(voucherType)) {
            characters = "0123456789";
        }
        if (isTextType(voucherType)) {
            characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        if (isBothType(voucherType)) {
            characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        }

        SecureRandom random = new SecureRandom();
        StringBuilder keyVoucher = new StringBuilder();
        for (int i = 0; i < lengthOfKey; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            keyVoucher.append(randomChar);
        }
        return keyVoucher.toString();
    }

    private String genVoucherStatus(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime currentDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        startTime = startTime.withHour(0).withMinute(0).withSecond(0);
        endTime = endTime.withHour(0).withMinute(0).withSecond(0);

        if ((startTime.isBefore(currentDate) || startTime.equals(currentDate)) && (endTime.isAfter(currentDate) || endTime.equals(currentDate))) {
            return VoucherStatus.A.getLabel();
        } else {
            return VoucherStatus.I.getLabel();
        }
    }

    private boolean isNumberType(String voucherType) {
        return VoucherType.NUMBER.name().equals(voucherType);
    }

    private boolean isTextType(String voucherType) {
        return VoucherType.TEXT.name().equals(voucherType);
    }
    private boolean isBothType(String voucherType) {
        return VoucherType.BOTH.name().equals(voucherType);
    }
}