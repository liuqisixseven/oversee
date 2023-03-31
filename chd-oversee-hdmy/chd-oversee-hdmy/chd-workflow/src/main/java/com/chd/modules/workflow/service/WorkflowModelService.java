package com.chd.modules.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.exception.BizException;
import com.chd.common.util.JsonUtils;
import com.chd.modules.workflow.entity.WorkflowDeployment;
import com.chd.modules.workflow.mapper.WorkflowDeploymentMapper;
import com.chd.modules.workflow.utils.WorkflowFlowElementUtils;
import com.chd.modules.workflow.vo.WorkflowModelFormVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Bpmn模型数据管理
 */
@Service
public class WorkflowModelService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private WorkflowDeploymentMapper workflowDeploymentMapper;



    public BpmnModel getBpmnModelByProcessDefId(String processDefId) {
        return repositoryService.getBpmnModel(processDefId);
    }

    public List<EndEvent> findEndFlowElement(String processDefId) {
        BpmnModel bpmnModel = getBpmnModelByProcessDefId(processDefId);
        if (bpmnModel != null) {
            Process process = bpmnModel.getMainProcess();
            return process.findFlowElementsOfType(EndEvent.class);
        } else {
            return null;
        }
    }

    public Activity findActivityByName(String processDefId, String name){
        Activity activity = null;
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        Process process = bpmnModel.getMainProcess();
        Collection<FlowElement> list = process.getFlowElements();
        for (FlowElement f : list) {
            if (StringUtils.isNotBlank(name)) {
                if (name.equals(f.getName())) {
                    activity = (Activity) f;
                    break;
                }
            }
        }
        return activity;
    }

    /**
     * 保存bpmn Model
     * @param modelForm
     */
    @Transactional
    public void save(WorkflowModelFormVo modelForm) {
        // 检测key是否重复
        Model lastModel = repositoryService
                .createModelQuery()
                .modelKey(modelForm.getKey())
                .latestVersion()
                .singleResult();
        if (StringUtils.isBlank(modelForm.getId()) && Objects.nonNull(lastModel)) {
            throw new BizException(WorkflowErrorCode.MODEL_KEY_DUPLICATE);
        }
        // 保存模型表
        Model model = repositoryService.newModel();
        model.setName(modelForm.getName());
        model.setCategory(modelForm.getCategory());
        model.setKey(modelForm.getKey());
//        model.setTenantId(modelForm.getTenantId());
        if (Objects.nonNull(lastModel)) {
            model.setVersion(lastModel.getVersion() + 1);
        }
        try {
            String metaInfoStr = new ObjectMapper().writeValueAsString(modelForm.getMetaInfo());
            model.setMetaInfo(metaInfoStr);
        } catch (Exception e) {
            throw new BizException("系列化失败");
        }
        repositoryService.saveModel(model);
        // 保存EditorSource数据
        repositoryService.addModelEditorSource(model.getId(), modelForm.getEditorSourceValue().getBytes());
        repositoryService.addModelEditorSourceExtra( model.getId(),JsonUtils.toJsonStr(modelForm.getEditorSourceExtraValue()).getBytes());
    }


    /**
     * 校验Bpmn数据是否正确
     * @param bpmnData
     * @return
     */
    public boolean checkBpmnModelData(String bpmnData){
        boolean result=false;
//        try {
            if (StringUtils.isBlank(bpmnData)) {
                return result;
            }
            BpmnModel bpmnModel = readXmlModel(bpmnData.getBytes());
            List<FlowElement> flowElements= WorkflowFlowElementUtils.getProcessPath(bpmnModel, null);
            List<UserTask> userTasks=WorkflowFlowElementUtils.getUserTaskFromActivity( flowElements);
            if(CollectionUtils.isNotEmpty(userTasks)){

                for(UserTask userTask:userTasks){
                    int num=0;
                    if(StringUtils.isNotBlank(userTask.getAssignee())){
                        if(userTask.getAssignee().startsWith("${") && userTask.getAssignee().endsWith("}")){
                            if(!userTask.getAssignee().equals("${USER_OWNER}")){
                                throw new BizException(String.format("%s审批节点用户设置错误",userTask.getName()));
                            }
                        }
                        num++;
                    }
                    if(CollectionUtils.isNotEmpty(userTask.getCandidateUsers())){
                        num++;
                    }
                    if(CollectionUtils.isNotEmpty(userTask.getCandidateGroups())){
                        num++;
                    }
                    if(num!=1){
                        throw new BizException(userTask.getName()+"审批节点用户设置错误");
                    }

                }
                result=true;
            }

//        }catch (Exception ex){
//            result=false;
//        }
        return result;
    }

    /**
     * 获取bpmn Model
     * @param id
     * @return
     */
    public WorkflowModelFormVo getModelById(String id) {
        Model model = repositoryService.createModelQuery().modelId(id).singleResult();
        byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
        byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(model.getId());
        WorkflowModelFormVo modelFormVo = new WorkflowModelFormVo(model);
        if (Objects.nonNull(modelEditorSource)) {
            modelFormVo.setEditorSourceValue(new String(modelEditorSource));
        }
        if (Objects.nonNull(modelEditorSourceExtra)) {
            modelFormVo.setEditorSourceExtraValue(
                    JsonUtils.fromJson(new String(modelEditorSourceExtra), HashMap.class)
            );
        }
        modelFormVo.setTenantId( model.getTenantId());
        return modelFormVo;
    }

    /**
     * 获取model 列表
     * @param query
     * @return
     */
    public IPage<Model> findModelList(WorkflowModelFormVo query) {
        // 获取查询流程定义对对象
        ModelQuery modelQuery = repositoryService.createModelQuery().orderByCreateTime();
        if (query.isLastVersion()) {
            modelQuery.latestVersion();
        }
        if (Objects.nonNull(query.getVersion())) {
            modelQuery.modelVersion(query.getVersion());
        }
        // 排序
            modelQuery.desc();
        // 动态查询
        if (StringUtils.isNotBlank(query.getCategory())) {
            modelQuery.modelCategoryLike(query.getCategory() );
        }
        if (StringUtils.isNotBlank(query.getName())) {
            modelQuery.modelNameLike( query.getName() );
        }
        if (StringUtils.isNotBlank(query.getKey())) {
            modelQuery.modelKey(query.getKey());
        }
        List<Model> listpage = modelQuery.listPage(query.getOffset(), query.getSize());

        IPage<Model> result=new Page<Model>(query.getCurrent(),query.getSize());
        result.setRecords(listpage);
        result.setTotal(modelQuery.count());

        return result;
    }

    /**
     * 发布Model
     * @param id
     */
    public void deploy(String id) {
        Model model = repositoryService.createModelQuery().modelId(id).singleResult();
        byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
//        byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(model.getId());
//        BpmnModel bpmnModel= readXmlModel(modelEditorSource);
        Deployment deployment = repositoryService
                .createDeployment()
                .name(model.getName())
                .addString(model.getName() + ".bpmn20.xml",new String(modelEditorSource))
                .category(model.getCategory())
                .key(model.getKey())
//                .tenantId(model.getTenantId())
//                .addBpmnModel(model.getName() + ".bpmn20.xml",bpmnModel)
                .deploy();

        Model model2 = repositoryService.getModel(id);
        model2.setDeploymentId(deployment.getId());
        repositoryService.saveModel(model2);
//        model.setDeploymentId(deployment.getId());
//        repositoryService.saveModel(model);

        WorkflowDeployment workflowDeployment=new WorkflowDeployment();
        workflowDeployment.setId(deployment.getId());
        workflowDeployment.setCategory(deployment.getCategory());
        workflowDeployment.setBpmnXml(modelEditorSource);
        workflowDeploymentMapper.insert(workflowDeployment);

        // 设置流程分类
        List<ProcessDefinition> list = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .list();
        for (ProcessDefinition processDefinition : list) {
            repositoryService.setProcessDefinitionCategory(processDefinition.getId(), model2.getCategory());
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new BizException(WorkflowErrorCode.DEFINITION_NOT_FOUND);
        }
    }

    public BpmnModel readXmlModel(byte[] modelEditorSource){
        BpmnModel bpmnModel =null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(modelEditorSource);
            XMLInputFactory xif = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(inputStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
             bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
            Process process = bpmnModel.getMainProcess();
            FlowElement startElement = process.getInitialFlowElement();
            if (startElement instanceof UserTask) {
                UserTask userTask=(UserTask) startElement;
                String formkey= userTask.getFormKey();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bpmnModel;
    }

    public void deleteModel(String modelId) {
        repositoryService.deleteModel(modelId);
    }

//    public BpmnModel getDeploymentModel(String deploymentId){
//        BpmnModel bpmnModel= null;
//        WorkflowDeployment workflowDeployment= workflowDeploymentMapper.selectById(deploymentId);
//        if(workflowDeployment!=null && workflowDeployment.getBpmnXml()!=null){
//            bpmnModel=readXmlModel(workflowDeployment.getBpmnXml());
//        }
//        return bpmnModel;
//    }


    public List<UserTask> getUserTaskByBpmnModel(BpmnModel bpmnModel){
        List<UserTask> userTasks=new ArrayList<>();
        Collection<FlowElement> flowElements= WorkflowFlowElementUtils.getProcessPath(bpmnModel,null);
        if(flowElements!=null){
            for(FlowElement flowElement:flowElements){
                getUserTaskFromProcess(userTasks,flowElement);
            }
        }
//        if(bpmnModel!=null && bpmnModel.getMainProcess()!=null){
//            Collection<FlowElement> flowElements= bpmnModel.getMainProcess().getFlowElements();
//
//            if(flowElements!=null){
//                for(FlowElement flowElement:flowElements){
//                    getUserTaskFromProcess(userTasks,flowElement);
//                }
//            }
//        }
        return userTasks;
    }
    public List<UserTask> getUserTaskFromProcess(List<UserTask> userTasks,FlowElement flowElement){
        if(flowElement!=null){
            if (flowElement instanceof UserTask) {
                userTasks.add((UserTask) flowElement);
            } else if (flowElement instanceof SubProcess) {
                Collection<FlowElement> flowElements=((SubProcess)flowElement).getFlowElements();
                if(CollectionUtils.isNotEmpty(flowElements)) {
                    for (FlowElement subFlow : flowElements) {
                        getUserTaskFromProcess(userTasks, subFlow);
                    }
                }
            }
        }
        return userTasks;
    }

//    public List<UserTask> getDepolymentUserTask(String depolymentId){
//        BpmnModel bpmnModel=getDeploymentModel(depolymentId);
//        return getUserTaskByBpmnModel(bpmnModel);
//    }

    public BpmnModel getModel(String id){
        Model model = repositoryService.createModelQuery().modelId(id).singleResult();
        if(model!=null) {
            byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
            return readXmlModel(modelEditorSource);
        }
        return null;
    }





}
