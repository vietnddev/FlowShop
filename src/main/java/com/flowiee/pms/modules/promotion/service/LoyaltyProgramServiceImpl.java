package com.flowiee.pms.modules.promotion.service;

import com.flowiee.pms.modules.promotion.repository.LoyaltyProgramRepository;
import com.flowiee.pms.modules.promotion.repository.LoyaltyRuleRepository;
import com.flowiee.pms.modules.promotion.repository.LoyaltyTransactionRepository;
import com.flowiee.pms.modules.sales.entity.*;
import com.flowiee.pms.common.exception.AppException;
import com.flowiee.pms.common.exception.BadRequestException;
import com.flowiee.pms.common.exception.EntityNotFoundException;
import com.flowiee.pms.common.exception.ResourceNotFoundException;
import com.flowiee.pms.modules.sales.repository.CustomerRepository;
import com.flowiee.pms.modules.sales.repository.OrderRepository;
import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.common.utils.OrderUtils;
import com.flowiee.pms.common.enumeration.LoyaltyTransactionType;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoyaltyProgramServiceImpl extends BaseService implements LoyaltyProgramService {
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final LoyaltyProgramRepository loyaltyProgramRepository;
    private final LoyaltyRuleRepository loyaltyRuleRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<LoyaltyProgram> findAll() {
        return loyaltyProgramRepository.findAll();
    }

    @Override
    public LoyaltyProgram findById(Long programId, boolean pThrowException) {
        Optional<LoyaltyProgram> entityOptional = loyaltyProgramRepository.findById(programId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"loyalty program"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public LoyaltyProgram save(LoyaltyProgram program) {
        LocalDateTime lvStartDate = program.getStartDate();
        LocalDateTime lvEndDate = program.getEndDate();
        if (!lvStartDate.isBefore(lvEndDate))
            throw new BadRequestException("The end time must be greater than the start time!");

        return loyaltyProgramRepository.save(program);
    }

    @Override
    public LoyaltyProgram update(LoyaltyProgram updateProgram, Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = findById(loyaltyProgramId, true);
        if (!existingProgram.isExpired()) {
            existingProgram.setName(updateProgram.getName());
            existingProgram.setDescription(updateProgram.getDescription());
            //existingProgram.setPointConversionRate(updateProgram.getPointConversionRate());
            existingProgram.setStartDate(updateProgram.getStartDate());
            existingProgram.setEndDate(updateProgram.getEndDate());
            existingProgram.setActive(updateProgram.isActive());
        }

        return loyaltyProgramRepository.save(existingProgram);
    }

    @Override
    public String delete(Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = findById(loyaltyProgramId, true);
        if (!existingProgram.getLoyaltyTransactionList().isEmpty()) {
            throw new BadRequestException("The program has a transaction that does not allow deletion!");
        }
        loyaltyProgramRepository.deleteById(loyaltyProgramId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<LoyaltyProgram> getActivePrograms() {
        return loyaltyProgramRepository.findActiveProgram();
    }

    @Override
    public LoyaltyProgram getDefaultProgram() {
        return loyaltyProgramRepository.findDefaultProgram();
    }

    @Override
    public LoyaltyProgram enableProgram(Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = this.findById(loyaltyProgramId, true);
        existingProgram.setActive(true);
        return loyaltyProgramRepository.save(existingProgram);
    }

    @Override
    public LoyaltyProgram disableProgram(Long loyaltyProgramId) {
        LoyaltyProgram existingProgram = this.findById(loyaltyProgramId, true);
        existingProgram.setActive(false);
        return loyaltyProgramRepository.save(existingProgram);
    }

    @Transactional
    @Override
    public void accumulatePoints(Order pOrder, Long pProgramId) {
        LoyaltyProgram lvLoyaltyProgram = getProgramForAccumulatePoints(pProgramId);
        if (lvLoyaltyProgram == null)
            throw new BadRequestException("No loyalty program applied!");

        Optional<Customer> lvCustomerOpt = customerRepository.findById(pOrder.getCustomer().getId());
        if (lvCustomerOpt.isEmpty())
            throw new ResourceNotFoundException("Customer not found!");

        BigDecimal lvTotalAmount = OrderUtils.calTotalAmount_(pOrder.getListOrderDetail(), pOrder.getAmountDiscount());
        BigDecimal lvPoints = getPoints(lvTotalAmount, lvLoyaltyProgram);
        if (lvPoints.doubleValue() <= 0)
            throw new BadRequestException("Points must be greater than zero!");

        Customer lvCustomer = lvCustomerOpt.get();
        lvCustomer.setBonusPoints(lvCustomer.getBonusPoints() + lvPoints.intValue());
        customerRepository.save(lvCustomer);

        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomer(lvCustomer);
        transaction.setLoyaltyProgram(lvLoyaltyProgram);
        transaction.setTransactionType(LoyaltyTransactionType.ACCUMULATE);
        transaction.setPoints(lvPoints.intValue());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setRemark(String.format("Accumulate %s points from order %s", lvPoints.intValue(), pOrder.getCode()));
        loyaltyTransactionRepository.save(transaction);
    }

    private LoyaltyProgram getProgramForAccumulatePoints(Long pProgramId) {
        LoyaltyProgram lvLoyaltyProgram = this.findById(pProgramId, false);
        if (lvLoyaltyProgram != null) {
            return lvLoyaltyProgram;
        } else {
            LoyaltyProgram lvDefaultProgram = loyaltyProgramRepository.findDefaultProgram();
            if (lvDefaultProgram != null) {
                return lvDefaultProgram;
            }
        }
        return null;
    }

    private BigDecimal getPoints(BigDecimal orderValue, LoyaltyProgram program) {
        List<LoyaltyRule> ruleList = program.getLoyaltyRuleList();
        if (ObjectUtils.isEmpty(ruleList)) {
            throw new AppException("No applicable loyalty rule found!");
        }
        for (LoyaltyRule rule : ruleList) {
            BigDecimal lvMinValue = rule.getMinOrderValue();
            BigDecimal lvMaxValue = rule.getMaxOrderValue();
            BigDecimal lvPointConversionRate = rule.getPointConversionRate();

            if (orderValue.compareTo(lvMinValue) >= 0 && (lvMaxValue == null || orderValue.compareTo(lvMaxValue) < 0)) {
                return orderValue.multiply(lvPointConversionRate).setScale(0, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.ZERO;
    }

    @Transactional
    @Override
    public void redeemPoints(Long pCustomerId, int pointsToRedeem) {
        Optional<Customer> lvCustomerOpt = customerRepository.findById(pCustomerId);
        if (lvCustomerOpt.isEmpty())
            throw new ResourceNotFoundException("Customer not found!");

        Customer lvCustomer = lvCustomerOpt.get();
        if (lvCustomer.getBonusPoints() < pointsToRedeem)
            throw new RuntimeException("Insufficient points");

        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomer(lvCustomer);
        transaction.setTransactionType(LoyaltyTransactionType.REDEEM);
        transaction.setPoints(pointsToRedeem);
        transaction.setTransactionDate(LocalDateTime.now());
        loyaltyTransactionRepository.save(transaction);

        // Deduct points
        lvCustomer.setBonusPoints(lvCustomer.getBonusPoints() - pointsToRedeem);
        customerRepository.save(lvCustomer);
    }

    @Override
    public void addRule(LoyaltyRule loyaltyRule, Long programId) {
        LoyaltyProgram loyaltyProgram = this.findById(programId, true);
        if (loyaltyProgram.isExpired() || !loyaltyProgram.isActive()) {
            throw new BadRequestException("Program is inactive!");
        }
        if (loyaltyRule.getMinOrderValue() == null || loyaltyRule.getMinOrderValue().doubleValue() <= 0) {
            throw new BadRequestException("Min value must be greater than 0!");
        }
        if (loyaltyProgram.getLoyaltyRuleList() == null) loyaltyProgram.setLoyaltyRuleList(new ArrayList<>());

        loyaltyProgram.getLoyaltyRuleList().add(loyaltyRule);

        loyaltyProgramRepository.save(loyaltyProgram);
    }

    @Override
    public void updateRule(LoyaltyRule pLoyaltyRule, Long pRuleId, Long pProgramId) {
        if (pLoyaltyRule.getMinOrderValue() == null || pLoyaltyRule.getMinOrderValue().doubleValue() <= 0)
            throw new BadRequestException("Min value must be greater than 0!");

        LoyaltyProgram loyaltyProgram = this.findById(pProgramId, true);
        if (loyaltyProgram.isExpired() || !loyaltyProgram.isActive()) {
            throw new BadRequestException("Program is inactive!");
        }

        LoyaltyRule lvLoyaltyRule = findRuleById(pRuleId, true);
        lvLoyaltyRule.setMinOrderValue(pLoyaltyRule.getMinOrderValue());
        lvLoyaltyRule.setMaxOrderValue(pLoyaltyRule.getMaxOrderValue());
        lvLoyaltyRule.setPointConversionRate(pLoyaltyRule.getPointConversionRate());
        lvLoyaltyRule.setCurrencyUnit(pLoyaltyRule.getCurrencyUnit());

        loyaltyRuleRepository.save(lvLoyaltyRule);
    }

    @Override
    public void removeRule(Long pRuleId) {
        LoyaltyRule lvLoyaltyRule = findRuleById(pRuleId, true);
        loyaltyRuleRepository.deleteById(lvLoyaltyRule.getId());
    }

    @Override
    public LoyaltyRule findRuleById(Long pRuleId, boolean throwException) {
        Optional<LoyaltyRule> loyaltyRule = loyaltyRuleRepository.findById(pRuleId);
        if (loyaltyRule.isEmpty())
            new BadRequestException("Loyalty rule doesn't exists!");

        return loyaltyRule.get();
    }
}