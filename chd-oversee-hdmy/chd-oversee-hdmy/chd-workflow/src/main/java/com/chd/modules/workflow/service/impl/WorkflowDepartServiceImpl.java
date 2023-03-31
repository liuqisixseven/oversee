package com.chd.modules.workflow.service.impl;

import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.mapper.ProcessClassificationMapper;
import com.chd.modules.workflow.mapper.WorkflowDepartMapper;
import com.chd.modules.workflow.service.WorkflowDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowDepartServiceImpl implements WorkflowDepartService {


    @Autowired
    WorkflowDepartMapper workflowDepartMapper;

    @Override
    public WorkflowDepart findDepart(WorkflowDepart depart) {
        return workflowDepartMapper.findDepart(depart);
    }
}
