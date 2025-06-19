package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.sales.service.VoucherService;
import com.flowiee.pms.modules.sales.service.VoucherTicketService;
import com.flowiee.pms.modules.sales.entity.VoucherTicket;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.modules.sales.dto.VoucherInfoDTO;
import com.flowiee.pms.modules.sales.dto.VoucherTicketDTO;
import com.flowiee.pms.modules.sales.repository.VoucherTicketRepository;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.common.enumeration.VoucherStatus;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherTicketServiceImpl extends BaseService<VoucherTicket, VoucherTicketDTO, VoucherTicketRepository> implements VoucherTicketService {
    ModelMapper mvModelMapper;
    VoucherService mvVoucherService;

    public VoucherTicketServiceImpl(VoucherTicketRepository pEntityRepository, ModelMapper pModelMapper, VoucherService pVoucherService) {
        super(VoucherTicket.class, VoucherTicketDTO.class, pEntityRepository);
        this.mvModelMapper = pModelMapper;
        this.mvVoucherService = pVoucherService;
    }

    @Override
    public List<VoucherTicketDTO> findAll() {
        return super.findAll();
    }

    @Override
    public Page<VoucherTicket> findAll(int pageSize, int pageNum, Long voucherId) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("id").ascending());
        return mvEntityRepository.findByVoucherId(voucherId, pageable);
    }

    @Override
    public VoucherTicketDTO findById(Long voucherTicketId, boolean pThrowException) {
        return super.findDtoById(voucherTicketId, pThrowException);
    }

    @Transactional
    @Override
    public VoucherTicketDTO save(VoucherTicketDTO voucherTicket) {
        if (voucherTicket == null) {
            throw new BadRequestException();
        }
        if (this.findTicketByCode(voucherTicket.getCode()) == null) {
            return super.save(voucherTicket);
        } else {
            throw new AppException();
        }
    }

    @Transactional
    @Override
    public VoucherTicketDTO update(VoucherTicketDTO voucherTicket, Long voucherDetailId) {
        if (voucherTicket == null || voucherDetailId == null || voucherDetailId <= 0) {
            throw new BadRequestException();
        }
        voucherTicket.setId(voucherDetailId);
        return super.update(voucherTicket, voucherDetailId);
    }

    @Override
    public String delete(Long ticketId) {
        VoucherTicket voucherTicket = super.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticker not found!"));
        if (voucherTicket.isUsed()) {
            throw new AppException("Voucher ticket in use!");
        }
        mvEntityRepository.deleteById(ticketId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<VoucherTicket> findByVoucherId(Long voucherId) {
        return mvEntityRepository.findByVoucherId(voucherId, Pageable.unpaged()).getContent();
    }

    @Override
    public VoucherTicket findByCode(String code) {
        return mvEntityRepository.findByCode(code);
    }

    @Override
    public VoucherTicketDTO isAvailable(String voucherTicketCode) {
        VoucherTicket voucherTicket = mvEntityRepository.findByCode(voucherTicketCode);
        if (voucherTicket == null) {
//            VoucherTicketDTO voucherTicketDTO = new VoucherTicketDTO();
//            voucherTicketDTO.setAvailable("N");
            return new VoucherTicketDTO("N");
        }
        VoucherTicketDTO voucherTicketDTO = mvModelMapper.map(voucherTicket, VoucherTicketDTO.class);
        VoucherInfoDTO voucherInfoDTO = mvVoucherService.findById(voucherTicketDTO.getVoucherInfo().getId(), true);
        if (voucherInfoDTO != null) {
            LocalDateTime currentTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime startTime = voucherInfoDTO.getStartTime().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endTime = voucherInfoDTO.getEndTime().withHour(0).withMinute(0).withSecond(0);
            if ((currentTime.isAfter(startTime) || currentTime.isEqual(startTime)) && (currentTime.isBefore(endTime) || currentTime.isEqual(endTime))) {
                voucherTicketDTO.setAvailable("Y");
            } else {
                voucherTicketDTO.setAvailable("N");
            }
        }
        return voucherTicketDTO;
    }

    @Override
    public VoucherTicket findTicketByCode(String code) {
        return mvEntityRepository.findByCode(code);
    }

    @Override
    public String checkTicketToUse(String code) {
        String statusTicket = "";
        VoucherTicket ticket = mvEntityRepository.findByCode(code);
        if (ticket != null) {
            VoucherInfoDTO voucherInfo = mvVoucherService.findById(ticket.getId(), true);
            statusTicket = voucherInfo.isActiveStatus() ? VoucherStatus.A.getLabel() : VoucherStatus.I.getLabel();
        } else {
            statusTicket = "Invalid!";
        }
        return statusTicket;
    }
}