package com.flowiee.pms.service.sales.impl;

import com.flowiee.pms.entity.sales.VoucherTicket;
import com.flowiee.pms.exception.AppException;
import com.flowiee.pms.exception.BadRequestException;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.model.dto.VoucherInfoDTO;
import com.flowiee.pms.model.dto.VoucherTicketDTO;
import com.flowiee.pms.repository.sales.VoucherTicketRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.sales.VoucherService;
import com.flowiee.pms.service.sales.VoucherTicketService;
import com.flowiee.pms.common.enumeration.MessageCode;
import com.flowiee.pms.common.enumeration.VoucherStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VoucherTicketServiceImpl extends BaseService implements VoucherTicketService {
    ModelMapper             mvModelMapper;
    VoucherService          mvVoucherService;
    VoucherTicketRepository mvVoucherTicketRepository;

    @Override
    public List<VoucherTicket> findAll() {
        return this.findAll(-1, -1, null).getContent();
    }

    @Override
    public Page<VoucherTicket> findAll(int pageSize, int pageNum, Long voucherId) {
        Pageable pageable = getPageable(pageNum, pageSize, Sort.by("id").ascending());
        return mvVoucherTicketRepository.findByVoucherId(voucherId, pageable);
    }

    @Override
    public VoucherTicket findById(Long voucherTicketId, boolean pThrowException) {
        Optional<VoucherTicket> entityOptional = mvVoucherTicketRepository.findById(voucherTicketId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"voucher ticket"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Transactional
    @Override
    public VoucherTicket save(VoucherTicket voucherTicket) {
        if (voucherTicket == null) {
            throw new BadRequestException();
        }
        if (this.findTicketByCode(voucherTicket.getCode()) == null) {
            return mvVoucherTicketRepository.save(voucherTicket);
        } else {
            throw new AppException();
        }
    }

    @Transactional
    @Override
    public VoucherTicket update(VoucherTicket voucherTicket, Long voucherDetailId) {
        if (voucherTicket == null || voucherDetailId == null || voucherDetailId <= 0) {
            throw new BadRequestException();
        }
        voucherTicket.setId(voucherDetailId);
        return mvVoucherTicketRepository.save(voucherTicket);
    }

    @Override
    public String delete(Long ticketId) {
        VoucherTicket voucherTicket = this.findById(ticketId, true);
        if (voucherTicket.isUsed()) {
            throw new AppException("Voucher ticket in use!");
        }
        mvVoucherTicketRepository.deleteById(ticketId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<VoucherTicket> findByVoucherId(Long voucherId) {
        return mvVoucherTicketRepository.findByVoucherId(voucherId, Pageable.unpaged()).getContent();
    }

    @Override
    public VoucherTicket findByCode(String code) {
        return mvVoucherTicketRepository.findByCode(code);
    }

    @Override
    public VoucherTicketDTO isAvailable(String voucherTicketCode) {
        VoucherTicket voucherTicket = mvVoucherTicketRepository.findByCode(voucherTicketCode);
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
        return mvVoucherTicketRepository.findByCode(code);
    }

    @Override
    public String checkTicketToUse(String code) {
        String statusTicket = "";
        VoucherTicket ticket = mvVoucherTicketRepository.findByCode(code);
        if (ticket != null) {
            VoucherInfoDTO voucherInfo = mvVoucherService.findById(ticket.getId(), true);
            statusTicket = voucherInfo.isActiveStatus() ? VoucherStatus.A.getLabel() : VoucherStatus.I.getLabel();
        } else {
            statusTicket = "Invalid!";
        }
        return statusTicket;
    }
}