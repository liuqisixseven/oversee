//package com.chd.modules.workflow.service.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chd.common.exception.AssertBizException;
//import com.chd.common.exception.BizException;
//import com.chd.common.util.BaseConstant;
//import com.chd.common.util.StringUtil;
//import com.chd.modules.workflow.entity.WorkflowDepart;
//import com.chd.modules.workflow.entity.WorkflowProcessDepart;
//import com.chd.modules.workflow.mapper.WorkflowDepartMapper;
//import com.chd.modules.workflow.mapper.WorkflowProcessUsersMapper;
//import com.chd.modules.workflow.service.WorkflowConstants;
//import com.chd.modules.workflow.service.WorkflowProcessDepartService;
//import com.chd.modules.workflow.service.WorkflowProcessUsersSerivce;
//import com.chd.modules.workflow.service.WorkflowVariablesService;
//import com.chd.modules.workflow.utils.WorkflowFlowElementUtils;
//import com.chd.modules.workflow.vo.WorkflowTaskFormVo;
//import com.chd.modules.workflow.vo.WorkflowUserTaskVo;
//import com.chd.modules.workflow.vo.WorkflowUserVo;
//import com.google.common.collect.Maps;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.flowable.engine.HistoryService;
//import org.flowable.engine.RuntimeService;
//import org.flowable.engine.TaskService;
//import org.flowable.engine.history.HistoricProcessInstance;
//import org.flowable.task.service.impl.persistence.entity.TaskEntity;
//import org.flowable.variable.api.history.HistoricVariableInstance;
//import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class WorkflowVariablesServiceImpl implements WorkflowVariablesService {
//
//    @Autowired
//    private WorkflowProcessDepartService workflowProcessDepartService;
//    @Autowired
//    private HistoryService historyService;
//    @Autowired
//    private RuntimeService runtimeService;
//    @Autowired
//    private WorkflowDepartMapper workflowDepartMapper;
//
//    @Autowired
//    private WorkflowProcessUsersMapper workflowProcessUsersMapper;
//    @Autowired
//    private WorkflowProcessUsersSerivce workflowProcessUsersSerivce;
//
//    @Autowired
//    private TaskService taskService;
//
//
//    @Override
//    public Map<String, Object> taskVariables(WorkflowTaskFormVo form, String processId, TaskEntity task, WorkflowUserVo user) {
//        Map<String,Object> myTaskVariables=new HashMap<>();
//        //TODO  判断当前用户是否在审批节点中
//        //第一、先提取流程变量
//        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(processId).singleResult();
//        if(processInstance!=null){
//            Map<String,Object> processVariables=processInstance.getProcessVariables();
//            if(MapUtils.isNotEmpty( processVariables)){
//                myTaskVariables=processVariables;
//            }
//        }
//        //第二、提取审批上报变量
//        if(MapUtils.isNotEmpty(form.getForm())){
//            myTaskVariables.putAll(form.getForm());
//        }
//        //第三、如果是部门分配审批表单，保存上报的部门
//        if("deptsSelect".equals(form.getFormId())){//部门分配
//            taskForm_selectDepart(form.getForm(),processId,user,myTaskVariables);
//        }else if("selectTaskUser".equals(form.getFormId())){
////            TODO 找出当前用户审批所属的部门ID
//            String departId=null;
//            String source=MapUtils.getString(form.getForm(),"source");
//            List<WorkflowDepart> departList= workflowProcessDepartService.userProcessDepartList(processId,source,user.getId());
//            if(CollectionUtils.isNotEmpty(departList)){
//                departId=departList.get(0).getDepartId();
//            }
//            workflowProcessUsersSerivce.saveProcessUsersForm(form.getForm(),departId,processId,user);
//        }
//
//        updateResponsibleUnitResponsibleDepartmentListPrincipalUserId(myTaskVariables,form.getForm(),processId,task);
//        updateSupervisorList(myTaskVariables,form.getForm(),processId,task);
//
//        //第四、提取流程部门变量
//        List<WorkflowProcessDepart> processDepartList= workflowProcessDepartService.getProcessDepartListByProcessId(processId);
//        Map<String,List<String>> departVariables= workflowProcessDepartService.getProcessDepartVariableMap(processDepartList);
//        myTaskVariables.putAll(departVariables);
//        myTaskVariables.putAll(workflowProcessUsersSerivce.getProcessUsersVariableMap(processId,null,null));
//        Object oldIssueObj = myTaskVariables.get(BaseConstant.PROCESS_OLD_ISSUES_KEY);
//        if(oldIssueObj!=null && Integer.parseInt(oldIssueObj.toString())==1) {
//            WorkflowUtils.setOldIssueVariable(myTaskVariables);
//        }
//        return myTaskVariables;
//    }
//
//
//
//
//    private void updateSupervisorList(Map<String,Object> myTaskVariables,Map<String,Object> form,String processId, TaskEntity task){
//        if(null!=myTaskVariables){
//            Integer isSupervise = (Integer) myTaskVariables.get("isSupervise");
//            if(null!=isSupervise&&isSupervise.intValue()==1){
//                String supervisorOrgIds = (String) myTaskVariables.get("supervisorOrgIds");
//                if(StringUtil.isNotEmpty(supervisorOrgIds)){
//                    List<Map<String, Object>> subprocessUserDataList = getSubprocessUserDataList(supervisorOrgIds);
//                    if(null!=subprocessUserDataList&&subprocessUserDataList.size()>0){
//                        List<Map<String, Object>> supervisorList = (List<Map<String, Object>>) myTaskVariables.get(WorkflowConstants.SUPERVISOR_LIST_KEY);
//                        for(Map<String, Object> subprocessUserData : subprocessUserDataList){
//                            if(null!=supervisorList){
//                                for(Map<String, Object> supervisor : supervisorList){
//                                    String departId = (String) supervisor.get(WorkflowConstants.DEPART_ID_KEY);
//                                    if(StringUtils.isNotEmpty(departId)&&departId.equals((String) subprocessUserData.get(WorkflowConstants.DEPART_ID_KEY))){
//                                        subprocessUserData.put(WorkflowConstants.USER_ID_KEY,subprocessUserData.get(WorkflowConstants.USER_ID_KEY));
//                                    }
//                                }
//                            }
//                            if(null!=form){
//                                String userIds = (String) form.get("userIds");
//                                if(StringUtil.isNotEmpty(userIds)){
//                                    String role = (String) form.get("role");
//                                    String source = (String) form.get("source");
//                                    if(WorkflowConstants.DEPART_ROLE.EXECUTOR.equals(role)&&WorkflowConstants.DEPART_SOURCE.SUPERVISOR.equals(source)){
//                                        // TODO 此处还有问题
//                                        if(null==subprocessUserData.get(WorkflowConstants.USER_ID_KEY)){
//                                            subprocessUserData.put(WorkflowConstants.USER_ID_KEY,getUserIdDataGroup(userIds));
//                                        }
//
////                                        String formId = (String) form.get("formId");
////                                        if(StringUtil.isNotEmpty(formId)&&formId.equals("selectTaskUser")){
////                                            subprocessUserData.put(WorkflowConstants.userIdKey,getUserIdDataGroup(userIds));
////                                        }
//                                        String parentExecutionId = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult().getParentId();
//                                        HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService.createHistoricVariableInstanceQuery();
//                                        List<HistoricVariableInstance> historicVariableInstanceList = historicVariableInstanceQuery.processInstanceId(processId).executionId(task.getExecutionId()).executionId(parentExecutionId).list();
//                                        if(null!=historicVariableInstanceList&&historicVariableInstanceList.size()>0){
//                                            for(HistoricVariableInstance historicVariableInstance : historicVariableInstanceList){
//                                                if(null!=historicVariableInstance){
//                                                    String variableName = historicVariableInstance.getVariableName();
//                                                    if(StringUtil.isNotEmpty(variableName)&&variableName.equals(WorkflowConstants.SUPERVISOR_KEY)){
//                                                        Map<String, Object> value = (Map<String, Object>) historicVariableInstance.getValue();
//                                                        if(null!=value){
//                                                            value.put(WorkflowConstants.USER_ID_KEY,getUserIdDataGroup(userIds));
//                                                            runtimeService.setVariable(parentExecutionId,variableName,value);
//                                                            String departId = (String) value.get(WorkflowConstants.DEPART_ID_KEY);
//                                                            if(StringUtils.isNotEmpty(departId)&&departId.equals(subprocessUserData.get(WorkflowConstants.DEPART_ID_KEY))){
//                                                                subprocessUserData.put(WorkflowConstants.USER_ID_KEY,getUserIdDataGroup(userIds));
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if(null!=subprocessUserDataList&&subprocessUserDataList.size()>0){
//                        myTaskVariables.put(WorkflowConstants.SUPERVISOR_LIST_KEY,subprocessUserDataList);
//                    }
//                }
//            }
//
//        }
//
//    }
//
//    private void updateResponsibleUnitResponsibleDepartmentListPrincipalUserId(Map<String,Object> myTaskVariables,Map<String,Object> form,String processId, TaskEntity task){
//        if(null!=myTaskVariables){
//            String parentExecutionId = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult().getParentId();
//            List<Map<String, Object>> responsibleUnitResponsibleDepartmentList = (List<Map<String, Object>>) myTaskVariables.get(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY);
//            if(null!=responsibleUnitResponsibleDepartmentList&&responsibleUnitResponsibleDepartmentList.size()>0) {
//                for(Map<String, Object> responsibleUnitResponsibleDepartmen : responsibleUnitResponsibleDepartmentList){
//                    if(null!=responsibleUnitResponsibleDepartmen&& StringUtil.isNotEmpty((String) responsibleUnitResponsibleDepartmen.get(WorkflowConstants.DEPART_ID_KEY))){
//                        if(null!=form){
//                            String userIds = (String) form.get("userIds");
//                            if(StringUtil.isNotEmpty(userIds)){
//                                String role = (String) form.get("role");
//                                String source = (String) form.get("source");
//                                if(WorkflowConstants.DEPART_ROLE.SPECIALIST.equals(role)&&WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE.equals(source)){
//                                    // TODO 此处还有问题
//                                    if(null==responsibleUnitResponsibleDepartmen.get(WorkflowConstants.USER_ID_KEY)){
//                                        responsibleUnitResponsibleDepartmen.put(WorkflowConstants.USER_ID_KEY,getUserIdDataGroup(userIds));
//                                    }
//
//
//                                    HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService.createHistoricVariableInstanceQuery();
//                                    List<HistoricVariableInstance> historicVariableInstanceList = historicVariableInstanceQuery.processInstanceId(processId).executionId(task.getExecutionId()).executionId(parentExecutionId).list();
////                                    System.out.println("------- parentExecutionId : "+parentExecutionId);
//                                    if(null!=historicVariableInstanceList&&historicVariableInstanceList.size()>0){
////                                        System.out.println("------- historicVariableInstanceList : "+historicVariableInstanceList.size());
//                                        for(HistoricVariableInstance historicVariableInstance : historicVariableInstanceList){
//                                            if(null!=historicVariableInstance){
//                                                String variableName = historicVariableInstance.getVariableName();
//                                                if(StringUtil.isNotEmpty(variableName)&&variableName.equals(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY)){
//                                                    Map<String, Object> value = (Map<String, Object>) historicVariableInstance.getValue();
////                                                    System.out.println("------- responsibleUnitResponsibleDepartmentKey value : "+JSONObject.toJSONString(value));
//                                                    if(null!=value){
//                                                        value.put(WorkflowConstants.USER_ID_KEY,getUserIdDataGroup(userIds));
//                                                        runtimeService.setVariable(parentExecutionId,variableName,value);
//                                                        String departId = (String) value.get(WorkflowConstants.DEPART_ID_KEY);
//                                                        if(StringUtils.isNotEmpty(departId)&&departId.equals(responsibleUnitResponsibleDepartmen.get(WorkflowConstants.DEPART_ID_KEY))){
//                                                            responsibleUnitResponsibleDepartmen.put(WorkflowConstants.USER_ID_KEY,getUserIdDataGroup(userIds));
//
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            Map<String,Object> responsibleUnitResponsibleDepartmentMap = (Map<String, Object>) runtimeService.getVariable(parentExecutionId, WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY);
//            if(null!=responsibleUnitResponsibleDepartmentMap){
//                String departId = (String) responsibleUnitResponsibleDepartmentMap.get(WorkflowConstants.DEPART_ID_KEY);
//                if(StringUtil.isNotEmpty(departId)){
//                    taskService.setVariable(task.getId(),WorkflowConstants.TASK_DEPART_ID_KEY,departId);
//                    myTaskVariables.put(WorkflowConstants.TASK_DEPART_ID_KEY,departId);
//                }
//            }
//            myTaskVariables.put(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY,responsibleUnitResponsibleDepartmentList);
//        }
//    }
//
//
//    /**
//     * 审批表单设置责任部门
//     * @param form
//     * @param processId
//     * @param user
//     */
//    private void taskForm_selectDepart(Map<String,Object> form,String processId,WorkflowUserVo user,Map<String,Object> myTaskVariables){
//        String departIds=MapUtils.getString(form,"depts");
//        if(StringUtils.isBlank(departIds)){
//            throw new BizException("请设置责任部门");
//        }
//        List<WorkflowDepart> departs= workflowDepartMapper.findHasUserDepartByIds(departIds);
//        List<WorkflowProcessDepart> processDepartList=workflowProcessDepartService.toProcessDepart(departIds, WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_EXECUTE);
//        if(CollectionUtils.isEmpty( departs) || CollectionUtils.isEmpty(processDepartList) || departs.size()!= processDepartList.size()){
//            throw new BizException("部门没有设置审批人");
//        }
//        if(CollectionUtils.isNotEmpty(processDepartList)) {
//            workflowProcessDepartService.batchAddProcessDepart(processDepartList,processId);
//        }
//
//        departIds = departs.stream().filter((workflowDepart) -> (null != workflowDepart && StringUtils.isNotEmpty(workflowDepart.getDepartId()))).map((workflowDepart) -> workflowDepart.getDepartId()).collect(Collectors.joining(","));
//        List<Map<String, Object>> subprocessUserDataList = getSubprocessUserDataList(departIds);
//        myTaskVariables.put(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY,subprocessUserDataList);
//        myTaskVariables.put(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_LIST_KEY + "Size",subprocessUserDataList.size());
////        List<Map<String, Object>> responsibleUnitResponsibleDepartmentList = departs.stream().map((workflowDepart) -> {
////            Map<String, Object> responsibleUnitResponsibleDepartment = Maps.newHashMap();
////            responsibleUnitResponsibleDepartment.put("id", workflowDepart.getId());
////            responsibleUnitResponsibleDepartment.put(WorkflowConstants.departIdKey, workflowDepart.getDepartId());
////            responsibleUnitResponsibleDepartment.put("departName", workflowDepart.getDepartName());
////            return responsibleUnitResponsibleDepartment;
////        }).collect(Collectors.toCollection(ArrayList::new));
////        myTaskVariables.put(WorkflowConstants.responsibleUnitResponsibleDepartmentListKey,responsibleUnitResponsibleDepartmentList);
////        myTaskVariables.put(WorkflowConstants.responsibleUnitResponsibleDepartmentListKey + "Size",responsibleUnitResponsibleDepartmentList.size());
//    }
//
//
//    @Override
//    public List<WorkflowUserTaskVo> setUserTaskListVariables(List<WorkflowUserTaskVo> userTasks, Map variables) {
//        if (CollectionUtils.isNotEmpty(userTasks)) {
//            Map<String,List<WorkflowUserVo>> variableUserMap=new HashMap<>();//流程用户变量
//            Map<String,List<WorkflowDepart>> variableDepartMap=new HashMap<>();//流程用户变量
//            for (WorkflowUserTaskVo userTask : userTasks) {
//                if (CollectionUtils.isNotEmpty(userTask.getVariableUser())) {
//                    Map<String,List<WorkflowUserVo>> taskVariableUserMap=new HashMap<>();
//                    Map<String,List<WorkflowDepart>> taskVariableDepartMap=new HashMap<>();
//                    Iterator<WorkflowUserVo> variableItem = userTask.getVariableUser().iterator();
//                    while (variableItem.hasNext()) {
//                        WorkflowUserVo taskVariable = variableItem.next();//变量
//                        String variableKey=taskVariable.getId();
//                        if(taskVariableUserMap.containsKey(variableKey) || taskVariableDepartMap.containsKey(variableKey)){
//                            if(!variableKey.startsWith(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY)&&variableKey.startsWith(WorkflowConstants.SUPERVISOR_KEY)){
//                                variableItem.remove();
//                                continue;
//                            }
//                        }
//                        List<WorkflowUserVo> userList= variableUserMap.get(variableKey);
//                        if(CollectionUtils.isNotEmpty(userList)){
//                            userTask.getUsers().addAll(userList);
//                            taskVariableUserMap.put(variableKey,userList);
//                            if(!variableKey.startsWith(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY)&&variableKey.startsWith(WorkflowConstants.SUPERVISOR_KEY)){
//                                variableItem.remove();
//                            }
//                            continue;
//                        }
//                        List<WorkflowDepart> departList= variableDepartMap.get(variableKey);
//                        if(CollectionUtils.isNotEmpty( departList)){
//                            userTask.getDeparts().addAll(departList);
//                            taskVariableDepartMap.put(variableKey,departList);
//                            if(!variableKey.startsWith(WorkflowConstants.RESPONSIBLE_UNIT_RESPONSIBLE_DEPARTMENT_KEY)&&variableKey.startsWith(WorkflowConstants.SUPERVISOR_KEY)){
//                                variableItem.remove();
//                            }
//                            continue;
//                        }
//                        userList=new ArrayList<>();
//                        departList=new ArrayList<>();
//                        variableUserMap.put(variableKey,userList);
//                        variableDepartMap.put(variableKey,departList);
//                        taskVariableUserMap.put(variableKey,userList);
//                        taskVariableDepartMap.put(variableKey,departList);
//                        Object value= variables.get(variableKey);
//                        if(value!=null){
//                            if(value instanceof List) {
//                                List valueList = (List) value;
//                                boolean hasValue=false;
//                                for (Object varUser : valueList) {
//                                    if (varUser instanceof WorkflowUserVo) {
//                                        userList.add((WorkflowUserVo) varUser);
//                                        hasValue=true;
//                                    }else if(varUser instanceof WorkflowDepart){
//                                        departList.add((WorkflowDepart) varUser);
//                                        hasValue=true;
//                                    }
//                                }
//                                if(hasValue ){
//                                    if(CollectionUtils.isNotEmpty(userList)) {
//                                        userTask.getUsers().addAll(userList);
//                                        variableItem.remove();
//                                    }else if(CollectionUtils.isNotEmpty(departList)){
//                                        userTask.getDeparts().addAll(departList);
//                                        variableItem.remove();
//                                    }
//
//                                }
//                            }else if(value instanceof  WorkflowUserVo){
//                                userList.add((WorkflowUserVo) value);
//                                userTask.getUsers().addAll(userList);
//                                variableItem.remove();
//                            }else if(value instanceof WorkflowDepart){
//                                departList.add((WorkflowDepart) value);
//                                userTask.getDeparts().addAll(departList);
//                                variableItem.remove();
//                            }
//                        }
//
//                    }
//
//                }
//            }
//        }
//        return userTasks;
//    }
//
//    @Override
//    public List<Map<String, Object>> getSubprocessUserDataList(String orgIds) {
//        List<Map<String, Object>> userDataList = new ArrayList<>();
//        if(StringUtil.isNotEmpty(orgIds)){
//            String[] supervisorOrgIdArray = orgIds.split(",");
//            for(String orgId : supervisorOrgIdArray){
//                if(StringUtil.isNotEmpty(orgId)){
//                    Map<String, Object> userData = Maps.newHashMap();
//                    userData.put(WorkflowConstants.DEPART_ID_KEY,orgId);
//                    List<WorkflowUserVo> workflowManageUserVos = workflowDepartMapper.manageUsersByDepartId(orgId);
//                    AssertBizException.isTrue((null!=workflowManageUserVos&&workflowManageUserVos.size()>0),"责任部门负责人不存在");
//                    WorkflowUserVo workflowManageUserVo = workflowManageUserVos.get(0);
//                    AssertBizException.isTrue((null!=workflowManageUserVo&&StringUtil.isNotEmpty(workflowManageUserVo.getId())),"责任部门负责人不存在");
//                    userData.put(WorkflowConstants.MANAGE_USER_ID_KEY,getUserIdDataGroup(workflowProcessDepartService.generateDepart(orgId, WorkflowConstants.DEPART_ROLE.MANAGER).getId()));
//                    List<WorkflowUserVo> workflowSupervisorUserVos = workflowDepartMapper.supervisorUsersByDepartId(orgId);
//                    AssertBizException.isTrue((null!=workflowSupervisorUserVos&&workflowSupervisorUserVos.size()>0),"责任部门分管领导不存在");
//                    WorkflowUserVo workflowSupervisorUserVo = workflowSupervisorUserVos.get(0);
//                    AssertBizException.isTrue((null!=workflowSupervisorUserVo&&StringUtil.isNotEmpty(workflowSupervisorUserVo.getId())),"责任部门分管领导不存在");
//                    userData.put(WorkflowConstants.SUPERVISOR_USER_ID_KEY,getUserIdDataGroup(workflowProcessDepartService.generateDepart(orgId, WorkflowConstants.DEPART_ROLE.SUPERVISOR).getId()));
//                    userDataList.add(userData);
//                }
//            }
//        }
//        return userDataList;
//    }
//
//    @Override
//    public List<String> getUserIdDataGroup(String ids){
//        List<String> userIdDataArray = new ArrayList<>();
//        if(StringUtils.isNotEmpty(ids)){
//            String[] idArray = ids.split(",");
//            if(null!=idArray&&idArray.length>0){
//                for(String id : idArray){
//                    if(StringUtils.isNotEmpty(id)){
//                        userIdDataArray.add(id);
//                    }
//                }
//            }
//        }
//        return userIdDataArray;
//    }
//}
