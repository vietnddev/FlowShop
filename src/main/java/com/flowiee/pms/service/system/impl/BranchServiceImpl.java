package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.entity.system.Branch;
import com.flowiee.pms.exception.EntityNotFoundException;
import com.flowiee.pms.repository.system.BranchRepository;
import com.flowiee.pms.base.service.BaseService;
import com.flowiee.pms.service.system.BranchService;
import com.flowiee.pms.common.enumeration.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchServiceImpl extends BaseService implements BranchService {
    BranchRepository mvBranchRepository;

    @Override
    public List<Branch> findAll() {
        return mvBranchRepository.findAll();
    }

    @Override
    public Branch findById(Long branchId, boolean pThrowException) {
        Optional<Branch> entityOptional = mvBranchRepository.findById(branchId);
        if (entityOptional.isEmpty() && pThrowException) {
            throw new EntityNotFoundException(new Object[] {"branch"}, null, null);
        }
        return entityOptional.orElse(null);
    }

    @Override
    public Branch save(Branch branch) {
        return mvBranchRepository.save(branch);
    }

    @Override
    public Branch update(Branch branch, Long branchId) {
        branch.setId(branchId);
        return mvBranchRepository.save(branch);
    }

    @Override
    public String delete(Long branchId) {
        mvBranchRepository.deleteById(branchId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}