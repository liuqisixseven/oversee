package com.chd.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

/**
 *  流程候选用户组
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@TableName("chd_workflow_candidate_group")
public class WorkflowCandidateGroup {
    private String id;//编码
    private String name;//名称
    private String source;//来源：上报问题本部牵头部门,责任单位牵头部门, 责任单位责任部门,督办部门
    private String role;//角色: executor-经办人,manager-负责人,supervisor-分管领导，secretary-书记，specialist-业务员
    private String remark;//备注
    private Date createTime;//创建时间
    private String createBy;//创建人
    private Date updateTime;//修改时间
    private String updateBy;//修改人

    @TableField(exist = false)
    private List<WorkflowProcessDepart> processDeparts;
}
