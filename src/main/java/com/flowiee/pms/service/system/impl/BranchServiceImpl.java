package com.flowiee.pms.service.system.impl;

import com.flowiee.pms.base.service.BaseServiceNew;
import com.flowiee.pms.entity.system.Branch;
import com.flowiee.pms.model.dto.BranchDTO;
import com.flowiee.pms.repository.system.BranchRepository;
import com.flowiee.pms.service.system.BranchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImpl extends BaseServiceNew<Branch, BranchDTO, BranchRepository> implements BranchService {
    private final ModelMapper modelMapper;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, ModelMapper modelMapper) {
        super(Branch.class, BranchDTO.class, branchRepository);
        this.modelMapper = modelMapper;
    }

    @Override
    public List<BranchDTO> findAll() {
        return super.convertDTOs(mvEntityRepository.findAll());
    }

    @Override
    public BranchDTO findById(Long branchId, boolean pThrowException) {
        return super.findById(branchId, pThrowException);
    }

    @Override
    public BranchDTO save(BranchDTO branch) {
        return super.save(branch);
    }

    @Override
    public BranchDTO update(BranchDTO pBranch, Long pBranchId) {
        BranchDTO lvBranchDto = super.findById(pBranchId, true);
        Branch lvBranch = modelMapper.map(lvBranchDto, Branch.class);

        //lvBranch.setBranchName();

        return super.convertDTO(mvEntityRepository.save(lvBranch));
    }

    @Override
    public String delete(Long branchId) {
        return super.delete(branchId);
    }
}