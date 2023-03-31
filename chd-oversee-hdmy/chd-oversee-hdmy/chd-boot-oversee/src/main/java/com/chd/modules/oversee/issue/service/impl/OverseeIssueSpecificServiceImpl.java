package com.chd.modules.oversee.issue.service.impl;

import com.chd.modules.oversee.issue.entity.OverseeIssueSpecific;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSpecificMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueSubcategoryMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueSpecificService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OverseeIssueSpecificServiceImpl implements IOverseeIssueSpecificService {

    @Autowired
    OverseeIssueSpecificMapper overseeIssueSpecificMapper;

    @Override
    @Transactional
    public Integer insert(OverseeIssueSpecific overseeIssueSpecific) {
        return overseeIssueSpecificMapper.insert(overseeIssueSpecific);
    }

    @Override
    public List<OverseeIssueSpecific> queryList(OverseeIssueSpecific query) {
        return overseeIssueSpecificMapper.queryList(query);
    }

    @Override
    @Transactional
    public Integer updateById(OverseeIssueSpecific overseeIssueSpecific) {
        return overseeIssueSpecificMapper.updateById(overseeIssueSpecific);
    }

    @Override
    @Transactional
    public Integer deleteById(Long id) {
        return overseeIssueSpecificMapper.deleteById(id);
    }

    @Override
    public OverseeIssueSpecific findById(Long id) {
        return overseeIssueSpecificMapper.findById(id);
    }

    @Override
    public OverseeIssueSpecific findByIdList(List<Long> list) {
        return overseeIssueSpecificMapper.findByIdList(list);
    }

    @Override
    @Transactional
    public Integer deleteBatchById(List<Long> list) {
        return overseeIssueSpecificMapper.deleteBatchById(list);
    }
}
