package com.chd.modules.workflow.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WorkflowModelMetaVo {
    /** 自定义属性 */
    private String description;
}
