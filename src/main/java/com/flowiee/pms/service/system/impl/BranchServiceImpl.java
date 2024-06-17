package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.entity.system.Branch;
import com.flowiee.pms.repository.system.BranchRepository;
import com.flowiee.pms.service.BaseService;
import com.flowiee.pms.service.system.BranchService;
import com.flowiee.pms.utils.constants.MessageCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl extends BaseService implements BranchService {
    private BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    @Override
    public Optional<Branch> findById(Integer branchId) {
        return branchRepository.findById(branchId);
    }

    @Override
    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    public Branch update(Branch branch, Integer branchId) {
        branch.setId(branchId);
        return branchRepository.save(branch);
    }

    @Override
    public String delete(Integer branchId) {
        branchRepository.deleteById(branchId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}