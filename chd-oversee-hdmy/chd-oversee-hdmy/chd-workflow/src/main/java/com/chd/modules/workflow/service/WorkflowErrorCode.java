package com.chd.modules.workflow.service;

import com.chd.common.exception.ExceptionCode;
 
public enum WorkflowErrorCode implements ExceptionCode{

     // ===============工作流相关(16)=============
     DEPLOY_SUCCESS(16000, "部署成功"),

     MODEL_KEY_DUPLICATE(16001, "模型key已存在"),
     DEFINITION_NOT_FOUND(16002, "部署失败，没有获取到流程定义"),
     RESOURCE_NOT_FOUND(16003, "没有获取到资源"),
     STATUS_SET_SUCCESS(16004, "状态切换成功"),
     REVOKE_SUCCESS(16006, "撤销成功"),
     APPROVE_SUCCESS(16007, "审批完成"),
     CLAIM_SUCCESS(16008, "签收成功"),
     DELEGATE_SUCCESS(16009, "委托成功"),
     NOT_CANDIDATE(16010, "您不是当前任务的候选人，操作失败"),
    USER_DEPART_NO_FOUND(16011, "没有找到用户所属部门"),
    PROCESS_DEFINITION_NO_FOUND(16012, "找不到定义流程"),
    PROCESS_SUSPENDED(16013, "此流程已经挂起,请联系系统管理员!");


    private Integer code;

    private String message;

    WorkflowErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static WorkflowErrorCode get(Integer code) {
        WorkflowErrorCode[] values = WorkflowErrorCode.values();
        for (WorkflowErrorCode object : values) {
            if (object.code == code) {
                return object;
            }
        }
        return null;
    }


    public Integer getCode(){
        return this.code;
    }
    public String getMessage(){
        return this.message;
    }
}
