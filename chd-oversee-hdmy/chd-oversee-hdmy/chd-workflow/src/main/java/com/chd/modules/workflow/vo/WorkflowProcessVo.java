package com.chd.modules.workflow.vo;

import com.chd.modules.workflow.entity.WorkflowProcess;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkflowProcessVo {
    private Integer pageNo;
    private Integer pageSize;

    private String id;
    // 流程定义的类别
    private String category;
    private String name;
    // 流程定义的key
    private String key;
    // 版本
    private int version;
    // 部署id
    private String deploymentId;
    // 资源名称
    private String resourceName;
    private String dgrmResourceName;
    private Date  applyTime;//申请时间
    // 描述
    private String description;
    private boolean hasStartFormKey;
    private boolean hasGraphicalNotation;
    private SuspensionState suspensionState;
    private String tenantId;
    private String engineVersion;
    private String derivedFrom;
    private String derivedFromRoot;
    private int derivedVersion;
    // 部署的 xml
    private String xml;
    private Date deploymentTime;
    // 只展示最新版本
    private boolean lastVersion;
    private String bizId;//业务ID
    private String bizUrl;//浏览详情地址
    private String startUserId;
    private String startUserName;

    public WorkflowProcessVo(ProcessDefinition processDefinition) {
        this.setId(processDefinition.getId());
        this.setCategory(processDefinition.getCategory());
        this.setKey(processDefinition.getKey());
        this.setName(processDefinition.getName());
        this.setDescription(processDefinition.getDescription());
        this.setVersion(processDefinition.getVersion());
        this.setResourceName(processDefinition.getResourceName());
        this.setDeploymentId(processDefinition.getDeploymentId());
        this.setDgrmResourceName(processDefinition.getDiagramResourceName());
        this.setHasStartFormKey(processDefinition.hasStartFormKey());
        this.setHasGraphicalNotation(processDefinition.hasGraphicalNotation());
        this.setSuspensionState(processDefinition.isSuspended() ? SuspensionState.suspended : SuspensionState.active);
        this.setTenantId(processDefinition.getTenantId());
        this.setDerivedFrom(processDefinition.getDerivedFrom());
        this.setDerivedFromRoot(processDefinition.getDerivedFromRoot());
        this.setDerivedVersion(processDefinition.getDerivedVersion());
    }

    public WorkflowProcessVo(ProcessDefinition processDefinition, Date deploymentTime) {
        this(processDefinition);
        this.setDeploymentTime(deploymentTime);
    }

    public WorkflowProcessVo(WorkflowProcess process){
        setBizId(process.getBizId());
        setId(process.getId());
        setBizUrl(process.getBizUrl());
        setCategory(process.getProcessCategory());
        setKey(process.getProcessDefKey());
        setName(process.getTitle());
        setDeploymentId(process.getId());
        setDeploymentTime(process.getApplyTime());
        setStartUserId(process.getStartUserId());
        setStartUserName(process.getStartUserName());
        setApplyTime(process.getApplyTime());
    }

    public enum SuspensionState {
        active,
        suspended,
    }

    public int getOffset(){

        if(getPageNo()==null){
            setPageNo(1);
        }
        if(getPageSize()==null){
            setPageSize(10);
        }
        int offset=(getPageNo()-1)*getPageSize();
        if(offset<0){
            offset=0;
        }
        return offset;
    }
    public int getCurrent(){
        if(getPageNo()==null){
            return 1;
        }
        return getPageNo();
    }
    public int getSize(){
        if(getPageSize()==null){
            return 10;
        }
        return getPageSize();
    }
}
