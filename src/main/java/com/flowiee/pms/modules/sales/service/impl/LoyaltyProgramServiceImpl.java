package com.flowiee.pms.modules.sales.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.utils.CoreUtils;
import com.flowiee.pms.modules.sales.dto.LoyaltyProgramDTO;
import com.flowiee.pms.modules.sales.service.LoyaltyProgramService;
import com.flowiee.pms.modules.sales.repository.LoyaltyProgramRepository;
import com.flowiee.pms.modules.sales.repository.LoyaltyTransactionRepository;
import com.flowiee.pms.modules.sales.entity.*;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.sales.repository.CustomerRepository;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.enumeration.LoyaltyTransactionType;
import com.flowiee.pms.common.enumeration.MessageCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoyaltyProgramServiceImpl extends BaseService<LoyaltyProgram, LoyaltyProgramDTO, LoyaltyProgramRepository> implements LoyaltyProgramService {
    private final LoyaltyTransactionRepository mvLoyaltyTransactionRepository;
    private final LoyaltyProgramRepository mvLoyaltyProgramRepository;
    private final CustomerRepository mvCustomerRepository;

    private DecimalFormat mvDecimalFormatValue = new DecimalFormat("#,###.00");
    private DecimalFormat mvDecimalFormatPoint = new DecimalFormat("#,###");

    public LoyaltyProgramServiceImpl(LoyaltyProgramRepository pLoyaltyProgramRepository, LoyaltyTransactionRepository pLoyaltyTransactionRepository, CustomerRepository pCustomerRepository) {
        super(LoyaltyProgram.class, LoyaltyProgramDTO.class, pLoyaltyProgramRepository);
        this.mvLoyaltyTransactionRepository = pLoyaltyTransactionRepository;
        this.mvLoyaltyProgramRepository = pLoyaltyProgramRepository;
        this.mvCustomerRepository = pCustomerRepository;
    }

    @Override
    public List<LoyaltyProgram> find() {
        return mvLoyaltyProgramRepository.findAll();
    }

    @Override
    public LoyaltyProgramDTO findById(Long programId, boolean pThrowException) {
        return super.findDtoById(programId, pThrowException);
    }

    @Override
    public LoyaltyProgramDTO save(LoyaltyProgramDTO pProgram) {
        LocalDate lvStartDate = pProgram.getStartDate();
        LocalDate lvEndDate = pProgram.getEndDate();
        if (!lvStartDate.isBefore(lvEndDate))
            throw new BadRequestException("The end time must be greater than the start time!");

        if (Boolean.TRUE.equals(pProgram.getIsDefault())) {
            LoyaltyProgram lvProgramAlreadyDefault = mvLoyaltyProgramRepository.findDefaultProgram();
            lvProgramAlreadyDefault.setIsDefault(false);
            mvLoyaltyProgramRepository.save(lvProgramAlreadyDefault);
        }

        return super.save(pProgram);
    }

    @Override
    public LoyaltyProgramDTO update(LoyaltyProgramDTO updateProgram, Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = super.findEntById(loyaltyProgramId, true);
        if (!existingProgram.isExpired()) {
            existingProgram.setName(updateProgram.getName());
            existingProgram.setDescription(updateProgram.getDescription());
            //existingProgram.setPointConversionRate(updateProgram.getPointConversionRate());
            existingProgram.setStartDate(updateProgram.getStartDate());
            existingProgram.setEndDate(updateProgram.getEndDate());
            existingProgram.setActive(updateProgram.getIsActive());
            existingProgram.setIsDefault(updateProgram.getIsDefault());
            if (updateProgram.getIsDefault()) {
                LoyaltyProgram lvProgramAlreadyDefault = mvLoyaltyProgramRepository.findDefaultProgram();
                lvProgramAlreadyDefault.setIsDefault(false);
                mvLoyaltyProgramRepository.save(lvProgramAlreadyDefault);
            }
        }

        return convertDTO(mvLoyaltyProgramRepository.save(existingProgram));
    }

    @Override
    public String delete(Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = super.findEntById(loyaltyProgramId, true);
        if (!existingProgram.getLoyaltyTransactionList().isEmpty()) {
            throw new BadRequestException("The program has a transaction that does not allow deletion!");
        }
        mvLoyaltyProgramRepository.deleteById(loyaltyProgramId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<LoyaltyProgram> getActivePrograms() {
        return mvLoyaltyProgramRepository.findActiveProgram();
    }

    @Override
    public LoyaltyProgram getDefaultProgram() {
        return mvLoyaltyProgramRepository.findDefaultProgram();
    }

    @Override
    public LoyaltyProgram enableProgram(Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = super.findEntById(loyaltyProgramId, true);
        existingProgram.setActive(true);
        return mvLoyaltyProgramRepository.save(existingProgram);
    }

    @Override
    public LoyaltyProgram disableProgram(Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = this.findEntById(loyaltyProgramId, true);
        existingProgram.setActive(false);
        return mvLoyaltyProgramRepository.save(existingProgram);
    }

    @Transactional
    @Override
    public LoyaltyTransaction accumulatePoints(Order pOrder, Long pProgramId) {
        LoyaltyProgram lvLoyaltyProgram = pProgramId != null ? getProgramForAccumulatePoints(pProgramId) : mvLoyaltyProgramRepository.findDefaultProgram();
        if (lvLoyaltyProgram == null) {
            throw new BadRequestException("No valid loyalty program!");
        }

        BigDecimal lvOrderValue = OrderUtils.calAmount(pOrder.getListOrderDetail(), pOrder.getAmountDiscount());
        int lvPoints = getPoints(lvOrderValue, lvLoyaltyProgram);
        if (lvPoints < 0)
            throw new BadRequestException("Points must not be less than zero!");

        Customer lvCustomer = pOrder.getCustomer();
        mvCustomerRepository.updateBonusPoint(lvCustomer.getId(), lvCustomer.getBonusPoints() + lvPoints);

        return mvLoyaltyTransactionRepository.save(LoyaltyTransaction.builder()
                .customer(lvCustomer)
                .loyaltyProgram(lvLoyaltyProgram)
                .transactionType(LoyaltyTransactionType.ACCUMULATE)
                .points(lvPoints)
                .transactionDate(LocalDateTime.now())
                .remark(String.format("Accumulate %s points from order %s with value %s",
                        mvDecimalFormatPoint.format(lvPoints),
                        pOrder.getCode(),
                        mvDecimalFormatValue.format(lvOrderValue)))
                .build());
    }

    private LoyaltyProgram getProgramForAccumulatePoints(Long pProgramId) {
        return this.findEntById(pProgramId, false);
    }

    private int getPoints(BigDecimal orderValue, LoyaltyProgram program) {
        BigDecimal lvMinApplyValue = CoreUtils.coalesce(program.getMinOrderValue(), BigDecimal.ZERO);
        BigDecimal lvMaxApplyValue = program.getMaxOrderValue();
        BigDecimal lvPointConversionRate = program.getPointConversionRate();

        if (orderValue.compareTo(lvMinApplyValue) >= 0 && (lvMaxApplyValue == null || orderValue.compareTo(lvMaxApplyValue) < 0)) {
            return orderValue.multiply(lvPointConversionRate).setScale(0, RoundingMode.HALF_UP).intValue();
        }

        return 0;
    }

    @Override
    @Transactional
    public void redeemPoints(Long pCustomerId, int pointsToRedeem) {
        Optional<Customer> lvCustomerOpt = mvCustomerRepository.findById(pCustomerId);
        if (lvCustomerOpt.isEmpty())
            throw new ResourceNotFoundException("Customer not found!");

        Customer lvCustomer = lvCustomerOpt.get();
        if (lvCustomer.getBonusPoints() < pointsToRedeem)
            throw new RuntimeException("Insufficient points");

        mvLoyaltyTransactionRepository.save(LoyaltyTransaction.builder()
                .customer(lvCustomer)
                .transactionType(LoyaltyTransactionType.REDEEM)
                .points(pointsToRedeem)
                .transactionDate(LocalDateTime.now())
                .build());

        // Deduct points
        lvCustomer.setBonusPoints(lvCustomer.getBonusPoints() - pointsToRedeem);
        mvCustomerRepository.save(lvCustomer);
    }

    @Override
    @Transactional
    public LoyaltyTransaction revokePoints(Order pOrder) {
        LoyaltyTransaction lvLoyaltyTransaction = pOrder.getLoyaltyTransaction();
        if (lvLoyaltyTransaction == null) {
            throw new AppException(String.format("Order %s does not have an associated LoyaltyTransaction", pOrder.getCode()));
        }

        Customer lvCustomer = lvLoyaltyTransaction.getCustomer();
        lvCustomer.setBonusPoints(lvCustomer.getBonusPoints() - lvLoyaltyTransaction.getPoints());
        mvCustomerRepository.save(lvCustomer);

        return mvLoyaltyTransactionRepository.save(LoyaltyTransaction.builder()
                .customer(lvCustomer)
                .loyaltyProgram(lvLoyaltyTransaction.getLoyaltyProgram())
                .transactionType(LoyaltyTransactionType.REVOKE)
                .points(lvLoyaltyTransaction.getPoints())
                .transactionDate(LocalDateTime.now())
                .remark(String.format("Revoke %s points, related accumulate_id is %s",
                        mvDecimalFormatPoint.format(lvLoyaltyTransaction.getPoints()),
                        lvLoyaltyTransaction.getId()))
                .build());
    }
}