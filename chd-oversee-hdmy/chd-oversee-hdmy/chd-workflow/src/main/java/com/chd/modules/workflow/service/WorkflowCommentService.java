package com.chd.modules.workflow.service;

import com.chd.common.util.JsonUtils;
import com.chd.modules.workflow.entity.WorkflowComment;
import com.chd.modules.workflow.mapper.WorkflowCommentMapper;
import com.chd.modules.workflow.vo.WorkflowUserVo;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowCommentService {
    @Autowired
    private WorkflowCommentMapper workflowCommentMapper;
    @Autowired
    private WorkflowUserService workflowUserService;


    /**
     * 添加备注
     * @param comment 参数
     */
    public Integer addComment(WorkflowComment comment) {
        comment.setCreateTime(new Date());
       return workflowCommentMapper.insertComment(comment);
    }

    /**
     * 通过流程实例id获取审批意见列表
     * @param processInstanceId 流程实例id
     * @return
     */
    public List<WorkflowComment> getCommentByProcessInstanceId(String processInstanceId){
        return workflowCommentMapper.commentListByProcessId(processInstanceId);
    }

    public List<WorkflowComment> findListByTaskKey(String processId,String taskKey){
        return workflowCommentMapper.findListByTaskKey(processId, taskKey);
    }
    public List<WorkflowComment> findOrgidListByTaskKey(String processId,String taskKey){
        return workflowCommentMapper.findOrgidListByTaskKey(processId, taskKey);
    }

    public List<WorkflowComment> findListByTaskKeys(String processId,List<String> taskKeyList){
        return workflowCommentMapper.findListByTaskKeys(processId, taskKeyList);
    }

    public Integer addStartProcessComment( String processInstanceId, String userId,String remark, String comment,Map<String,Object> form){
        return addComment(null,null,"发起",userId,processInstanceId, WorkflowConstants.CommentTypeEnum.FQ,comment,form,remark);
    }
    public Integer addTaskComment(Task task,String userId, String processInstanceId, WorkflowConstants.CommentTypeEnum type,String remark, String comment,Map<String,Object> form){
        return addComment(task.getId(),task.getTaskDefinitionKey(),task.getName(),userId,processInstanceId,type,comment,form,remark);
    }

    public Integer addComment(Task task,String userId, String processInstanceId, WorkflowConstants.CommentTypeEnum type, String comment,Map<String,Object> form){
        return addComment(task.getId(),task.getTaskDefinitionKey(),task.getName(),userId,processInstanceId,type,comment,form,"");
    }

    public Integer addComment(String taskId,String taskKey,String taskName, String userId, String processInstanceId, WorkflowConstants.CommentTypeEnum type, String comment) {
        return addComment(taskId,taskKey,taskName,userId,processInstanceId,type,comment,null,"");
    }

    public Integer addComment(String taskId,String taskKey,String taskName, String userId, String processInstanceId, WorkflowConstants.CommentTypeEnum type, String comment, Map<String,Object> form,String remark) {
        WorkflowUserVo user= workflowUserService.getUserById(userId);
        WorkflowComment workflowComment=new WorkflowComment();
        workflowComment.setTaskId(taskId);
        workflowComment.setTaskKey(taskKey);
        workflowComment.setUserId(userId);
        workflowComment.setUserName(user.getName());
        workflowComment.setProcessInstanceId(processInstanceId);
        workflowComment.setTaskType(type.toString());
        if(StringUtils.isNotBlank(remark)) {
            workflowComment.setTypeName(type.getName() + remark);
        }else{
            workflowComment.setTypeName(type.getName());
        }
        workflowComment.setTaskName(taskName);
        workflowComment.setComment(comment);
        workflowComment.setCreateTime(new Date());

        Integer result= workflowCommentMapper.insertComment(workflowComment);
        if(result!=null && result.intValue()>0){
            if(form!=null && form.size()>0){
                workflowCommentMapper.insertCommentFormData(workflowComment.getId(),JsonUtils.toJsonStr(form));
            }

        }
        return result;
    }

}
