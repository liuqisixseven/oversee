package com.chd.modules.workflow.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.repository.Model;

import java.util.Date;
import java.util.Map;

/**
 * Model表单
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkflowModelFormVo {
    private Integer pageNo;
    private Integer pageSize;

    private String id;
    /** 名称 */
    private String name;
    /** 唯一标识，基于key升级版本号 */
    private String key;
    /** 分类 */
    private String category;
    /** 版本 */
    private Integer version;
    /** 附加字段（json） */
    private WorkflowModelMetaVo metaInfo;
    /** 部署id */
    private String deploymentId;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date lastUpdateTime;
    /** 租户id */
    private String tenantId;
    /** xml 模型数据 */
    private String editorSourceValue;
    private Map<String,String> editorSourceExtraValue;
    // 只展示最新版本
    private boolean lastVersion;

    public WorkflowModelFormVo(Model model) {
        this.id = model.getId();
        this.name = model.getName();
        this.key = model.getKey();
        this.category = model.getCategory();
        this.version = model.getVersion();
        this.deploymentId = model.getDeploymentId();
        this.createTime = model.getCreateTime();
        this.lastUpdateTime = model.getLastUpdateTime();
        this.tenantId = model.getTenantId();

        try {
            WorkflowModelMetaVo actModelMeta = new ObjectMapper().readValue(model.getMetaInfo(), WorkflowModelMetaVo.class);
            this.metaInfo = actModelMeta;
        } catch (Exception e) {
            log.error("反序列化失败：", e);
        }
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
