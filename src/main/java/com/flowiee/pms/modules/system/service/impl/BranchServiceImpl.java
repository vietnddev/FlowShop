package com.flowiee.pms.modules.system.service.impl;

import com.flowiee.pms.common.base.service.BaseService;
import com.flowiee.pms.modules.system.entity.Branch;
import com.flowiee.pms.modules.system.dto.BranchDTO;
import com.flowiee.pms.modules.system.repository.BranchRepository;
import com.flowiee.pms.modules.system.service.BranchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImpl extends BaseService<Branch, BranchDTO, BranchRepository> implements BranchService {
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
        return super.findDtoById(branchId, pThrowException);
    }

    @Override
    public BranchDTO save(BranchDTO branch) {
        return super.save(branch);
    }

    @Override
    public BranchDTO update(BranchDTO pBranch, Long pBranchId) {
        BranchDTO lvBranchDto = super.findDtoById(pBranchId, true);
        Branch lvBranch = modelMapper.map(lvBranchDto, Branch.class);

        //lvBranch.setBranchName();

        return super.convertDTO(mvEntityRepository.save(lvBranch));
    }

    @Override
    public String delete(Long branchId) {
        return super.delete(branchId);
    }
}