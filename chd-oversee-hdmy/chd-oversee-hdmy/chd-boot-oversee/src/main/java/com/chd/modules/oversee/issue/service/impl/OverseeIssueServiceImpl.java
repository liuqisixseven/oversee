package com.chd.modules.oversee.issue.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.api.CommonAPI;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.system.vo.DictModel;
import com.chd.common.system.vo.LoginUser;
import com.chd.common.util.*;
import com.chd.modules.oversee.hdmy.entity.MyOrg;
import com.chd.modules.oversee.hdmy.mapper.MyOrgMapper;
import com.chd.modules.oversee.issue.entity.*;

import com.chd.modules.oversee.issue.mapper.*;
import com.chd.modules.oversee.issue.service.*;
import com.chd.modules.workflow.controller.WorkflowProcessController;
import com.chd.modules.workflow.entity.ProcessClassification;
import com.chd.modules.workflow.entity.WorkflowComment;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.chd.modules.workflow.mapper.WorkflowProcessMapper;
import com.chd.modules.workflow.service.*;
import com.chd.modules.workflow.utils.WorkflowFlowElementUtils;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.task.api.Task;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date: 2022-08-03
 * @Version: V1.0
 */
@Service()
@Transactional(readOnly = true)
@Primary
public class OverseeIssueServiceImpl extends ServiceImpl<OverseeIssueMapper, OverseeIssue> implements IOverseeIssueService {

    @Autowired
    OverseeIssueMapper overseeIssueMapper;

    @Autowired
    @Lazy
    ISpecificIssuesService specificIssuesService;

    @Autowired
    @Lazy
    WorkflowProcessController workflowProcessController;

    @Autowired
    OverseeIssueCategoryMapper overseeIssueCategoryMapper;

    @Autowired
    OverseeIssueSubcategoryMapper overseeIssueSubcategoryMapper;

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MyOrgMapper myOrgMapper;

    @Autowired
    @Lazy
    private IOverseeIssueCategoryService overseeIssueCategoryService;

    @Autowired
    private WorkflowProcessMapper workflowProcessMapper;

    @Autowired
    private OverseeIssueSpecificMapper overseeIssueSpecificMapper;

    @Autowired
    @Lazy
    private IOverseeIssueAppendixService overseeIssueAppendixService;

    @Autowired
    @Lazy
    private IProcessClassificationService processClassificationService;

    @Autowired
    private OverseeIssueFileMapper overseeIssueFileMapper;

    @Autowired
    @Lazy
    private IIssuesSupervisorService issuesSupervisorService;

    @Autowired
    @Lazy
    private IReasonCancellationService reasonCancellationService;

    @Autowired
    IIssuesAllocationService issuesAllocationService;

    @Autowired
    @Lazy
    IOverseeIssueRoleService overseeIssueRoleService;

    @Autowired
    @Lazy
    IIssuesRectificationMeasureService issuesRectificationMeasureService;

    @Autowired
    private WorkflowCommentService workflowCommentService;

    @Autowired
    private IOverseeUserSignatureService overseeUserSignatureService;

    @Autowired
    protected CommonAPI commonAPI;
    @Autowired
    private ISystemTemplateService systemTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private WorkflowDepartService workflowDepartService;

    @Autowired
    private WorkflowProcessDepartService workflowProcessDepartService;

    @Autowired
    private IOverseeIssueSpecificService overseeIssueSpecificService;

    @Autowired
    private TaskService taskService;


    @Value("${chd.path.upload}")
    private String upLoadPath;

    //    @Autowired
//    private IOssFileService ossFileService;
    private static final String PROCESS_CATEGORY_ISSUES = "ISSUES";


    @Override
    @Transactional
    public int addOrUpdate(OverseeIssue overseeIssue) {
        return addOrUpdate(overseeIssue, false);
    }


    @Override
    @Transactional
    public int addOrUpdate(OverseeIssue overseeIssue, boolean allUpdate) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null != overseeIssue), "请传递问题数据");

        Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getUpdateUserId())), "未获取到修改用户id");
        overseeIssue.setUpdateTime(new Date());

//        责任部门设置
        if(StringUtil.isNotEmpty(overseeIssue.getResponsibleMainOrgIds())||StringUtil.isNotEmpty(overseeIssue.getResponsibleCoordinationOrgIds())){
            Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getResponsibleMainOrgIds())), "请传递主要责任部门");
            if(StringUtil.isNotEmpty(overseeIssue.getResponsibleMainOrgIds())){
                overseeIssue.setResponsibleMainOrgIds(Arrays.asList((overseeIssue.getResponsibleMainOrgIds()).split(",")).stream().distinct().filter((orgId)->StringUtil.isNotEmpty(orgId)).collect(Collectors.joining(",")));
            }
            if(StringUtil.isNotEmpty(overseeIssue.getResponsibleCoordinationOrgIds())){
                overseeIssue.setResponsibleCoordinationOrgIds(Arrays.asList((overseeIssue.getResponsibleCoordinationOrgIds()).split(",")).stream().distinct().filter((orgId)->StringUtil.isNotEmpty(orgId)).collect(Collectors.joining(",")));
            }
            String responsibleOrgIds = Arrays.asList((overseeIssue.getResponsibleMainOrgIds() + (StringUtil.isNotEmpty(overseeIssue.getResponsibleCoordinationOrgIds())?("," + overseeIssue.getResponsibleCoordinationOrgIds()):"")).split(",")).stream().distinct().filter((orgId)->StringUtil.isNotEmpty(orgId)).collect(Collectors.joining(","));
            overseeIssue.setResponsibleOrgIds(responsibleOrgIds);
        }

        if (null != overseeIssue.getId() && overseeIssue.getId().intValue() > 0) {
            //            更新内容
            if (StringUtil.isNotEmpty(overseeIssue.getSpecificIssuesContent())) {
                overseeIssue.setSpecificIssuesId(addOrUpdateSpecificIssuesContent(overseeIssue.getSpecificIssuesContent(), overseeIssue.getCreateUserId(), overseeIssue.getId()));
            }
            addOrUpdateCount = overseeIssueMapper.updateById(overseeIssue);
        } else {
            overseeIssue.setCreateUserId(overseeIssue.getUpdateUserId());
            if (StringUtil.isEmpty(overseeIssue.getHeadquartersLeadDepartmentManagerUserId())) {
                overseeIssue.setHeadquartersLeadDepartmentManagerUserId(overseeIssue.getUpdateUserId());
            }
            Assert.isTrue((null != overseeIssue.getSource()), "请选择问题来源");
            Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getHeadquartersLeadDepartmentOrgId())), "请选择本部牵头部门");
            Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getResponsibleUnitOrgId())), "请选择责任单位");
            if (overseeIssue.getSource().intValue() != 1) {
                Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getResponsibleLeadDepartmentOrgId())), "请选择责任单位牵头部门");
            } else {
//                巡视问题 责任单位牵头部门就是本部牵头部门
                overseeIssue.setResponsibleLeadDepartmentOrgId(overseeIssue.getHeadquartersLeadDepartmentOrgId());
            }
            Assert.isTrue((null != overseeIssue.getCheckTime()), "检查时间");
            Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getApprovalBody())), "请输入批准主体");
            Assert.isTrue((overseeIssue.getApprovalBody().length() <= 50), "批准主体最长不能超过50个字符");
            Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getNum())), "请输入问题编码");
            Assert.isTrue((overseeIssue.getNum().length() <= 50), "批准主体最长不能超过50个字符");
            Assert.isTrue((null != overseeIssue.getIssueCategoryId() && overseeIssue.getIssueCategoryId().intValue() > 0), "请选择问题大类");
            Assert.isTrue((null != overseeIssue.getIssueSubcategoryId() && overseeIssue.getIssueSubcategoryId().intValue() > 0), "请选择问题小类");
            if (null == overseeIssue.getIsSupervise()) {
                overseeIssue.setIsSupervise(-1);
            }
            Assert.isTrue((overseeIssue.getIsSupervise().intValue() == -1 || overseeIssue.getIsSupervise().intValue() == 1), "请选择正确的督办类型");
            if (null == overseeIssue.getIsSign()) {
                overseeIssue.setIsSign(-1);
            }
            Assert.isTrue((overseeIssue.getIsSign().intValue() == -1 || overseeIssue.getIsSign().intValue() == 1), "请选择正确的会签类型");
            Assert.isTrue((StringUtil.isNotEmpty(overseeIssue.getSpecificIssuesContent())), "请输入具体问题");


            Integer specificIssuesId = addSpecificIssuesContent(overseeIssue.getSpecificIssuesContent(), overseeIssue.getCreateUserId());
            Assert.isTrue((null != specificIssuesId && specificIssuesId > 0), "增加问题主体id异常");
            overseeIssue.setSpecificIssuesId(specificIssuesId);
            addOrUpdateCount = overseeIssueMapper.insert(overseeIssue);

        }

        if (addOrUpdateCount > 0) {

//           更新流程分类
            Integer source = overseeIssue.getSource();
            if (null != source) {
                String processCategoryIssues = PROCESS_CATEGORY_ISSUES;
                List<ProcessClassification> processClassificationList = processClassificationService.list(Wrappers.<ProcessClassification>lambdaQuery().eq(ProcessClassification::getType, 1).eq(ProcessClassification::getSubcategory, source).eq(ProcessClassification::getDataType, 1).orderByAsc(ProcessClassification::getSort));
                if (null != processClassificationList && processClassificationList.size() > 0) {
                    if (StringUtils.isNotEmpty(processClassificationList.get(0).getValue())) {
                        processCategoryIssues = processClassificationList.get(0).getValue();
                    }
                }
                overseeIssue.setProcessClassificationValue(processCategoryIssues);
            }


//            更新附件
            if (allUpdate || (null != overseeIssue.getOverseeIssueAppendixList() && overseeIssue.getOverseeIssueAppendixList().size() > 0)) {
                if ((null != overseeIssue.getOverseeIssueAppendixList() && overseeIssue.getOverseeIssueAppendixList().size() > 0)) {
                    for (OverseeIssueAppendix overseeIssueAppendix : overseeIssue.getOverseeIssueAppendixList()) {
                        overseeIssueAppendix.setUpdateUserId(overseeIssue.getUpdateUserId());
                        overseeIssueAppendix.setIssueId(overseeIssue.getId());
                    }
                }
                overseeIssueAppendixService.addOrUpdateList(overseeIssue.getOverseeIssueAppendixList(), overseeIssue.getId(), OverseeIssueAppendix.PROBLEM_ENTRY_TYPE, overseeIssue.getUpdateUserId());
            }


//            更新责任部门
            if (allUpdate || StringUtil.isNotEmpty(overseeIssue.getResponsibleOrgIds())) {
                issuesAllocationService.addOrUpdateOrgIds(overseeIssue.getResponsibleOrgIds(), overseeIssue.getId(), overseeIssue.getUpdateUserId(),overseeIssue.getResponsibleMainOrgIds(),overseeIssue.getResponsibleCoordinationOrgIds());
//                issuesAllocationService.addOrUpdateOrgIds(overseeIssue.getResponsibleMainOrgIds(), overseeIssue.getId(), overseeIssue.getUpdateUserId());
//                issuesAllocationService.addOrUpdateOrgIds(overseeIssue.getResponsibleCoordinationOrgIds(), overseeIssue.getId(), overseeIssue.getUpdateUserId());
            }

//            更新督办部门  StringUtil.isNotEmpty(overseeIssue.getSupervisorOrgIds())
            if (allUpdate || (StringUtil.isNotEmpty(overseeIssue.getSupervisorOrgIds()))) {
                issuesSupervisorService.addOrUpdateOrgIds(overseeIssue.getSupervisorOrgIds(), overseeIssue.getId(), overseeIssue.getUpdateUserId(),overseeIssue.getIssuesSupervisorList());
            }

            if (null != overseeIssue.getSpecificIssuesId() && overseeIssue.getSpecificIssuesId().intValue() > 0) {
                SpecificIssues specificIssuesById = specificIssuesService.getById(overseeIssue.getSpecificIssuesId());
                if (null != specificIssuesById) {
                    if (null == specificIssuesById.getIssueId() || specificIssuesById.getIssueId().longValue() <= 0L) {
                        specificIssuesById.setIssueId(overseeIssue.getId());
                        specificIssuesService.updateById(specificIssuesById);
                    }
                }
            }

            // 同步用户角色数据
            overseeIssueRoleService.synchronization(overseeIssue.getId());

            redisCacheOverseeIssue(overseeIssue.getId());
        }

        return addOrUpdateCount;
    }

    private Integer addSpecificIssuesContent(String specificIssuesContent, String updateUserId) {
        return addOrUpdateSpecificIssuesContent(specificIssuesContent, updateUserId, null);
    }

    private Integer addOrUpdateSpecificIssuesContent(String specificIssuesContent, String updateUserId, Long issueId) {
        SpecificIssues specificIssues = new SpecificIssues();
        specificIssues.setSpecificIssuesContent(specificIssuesContent);
        specificIssues.setUpdateUserId(updateUserId);
        if (null != issueId && issueId.longValue() > 0L) {
            specificIssues.setIssueId(issueId);
            List<SpecificIssues> specificIssuesList = specificIssuesService.list(Wrappers.<SpecificIssues>lambdaQuery().eq(SpecificIssues::getIssueId, specificIssues.getIssueId()).eq(SpecificIssues::getDataType, 1));
            if (null != specificIssuesList && specificIssuesList.size() > 0) {
                SpecificIssues specificIssuesS = specificIssuesList.get(0);
                if (null != specificIssuesS && null != specificIssuesS.getId() && specificIssuesS.getId().intValue() > 0) {
                    specificIssues.setId(specificIssuesS.getId());
                }
            }
        }
        int addSpecificIssuesCount = specificIssuesService.addOrUpdate(specificIssues);
        Assert.isTrue((addSpecificIssuesCount > 0), "增加问题主体异常");
        return specificIssues.getId();
    }

    @Override
    public IPage<OverseeIssue> selectOverseeIssuePageVo(Page<?> page, Map<String, Object> map) {
        return overseeIssueMapper.selectOverseeIssuePageVo(page, map);
    }

    @Override
    public List<OverseeIssue> selectOverseeIssueList(Map<String, Object> map) {
        return overseeIssueMapper.selectOverseeIssueList(map);
    }

    @Override
    public OverseeIssue redisCacheOverseeIssue(Long overseeIssueId) {
        OverseeIssue overseeIssueById = null;
        if (null != overseeIssueId && overseeIssueId.intValue() > 0) {
            overseeIssueById = overseeIssueMapper.selectById(overseeIssueId);
            if (null != overseeIssueById) {

                if (null != overseeIssueById.getSpecificIssuesId() && overseeIssueById.getSpecificIssuesId().intValue() > 0) {
                    SpecificIssues specificIssuesById = specificIssuesService.getById(overseeIssueById.getSpecificIssuesId());
                    if (null != specificIssuesById) {
                        if (StringUtil.isNotEmpty(specificIssuesById.getSpecificIssuesContent())) {
                            overseeIssueById.setSpecificIssuesContent(specificIssuesById.getSpecificIssuesContent());
                        }
                    }
                }

//                缓存责任单位名称
                overseeIssueById.setResponsibleUnitOrgName(getMyOrgName(overseeIssueById.getResponsibleUnitOrgId()));

//                缓存责任单位责任部门
                List<IssuesAllocation> issuesAllocations = issuesAllocationService.list(Wrappers.<IssuesAllocation>lambdaQuery().eq(IssuesAllocation::getIssueId, overseeIssueId).eq(IssuesAllocation::getDataType, 1));
                if (null != issuesAllocations && issuesAllocations.size() > 0) {
                    overseeIssueById.setResponsibleUnitResponsibleDepartmentOrgName(issuesAllocations.stream().filter((issuesAllocation) -> null != issuesAllocation && StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId())).map((issuesAllocation) -> getMyOrgName(issuesAllocation.getResponsibleDepartmentOrgId())).collect(Collectors.joining(",")));
                    overseeIssueById.setResponsibleMainOrgIds(issuesAllocations.stream().filter((issuesAllocation) -> null != issuesAllocation && StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId()) && null!= issuesAllocation.getDepartmentType() && issuesAllocation.getDepartmentType().intValue()==2).map((issuesAllocation) -> issuesAllocation.getResponsibleDepartmentOrgId()).collect(Collectors.joining(",")));
                    overseeIssueById.setResponsibleCoordinationOrgIds(issuesAllocations.stream().filter((issuesAllocation) -> null != issuesAllocation && StringUtil.isNotEmpty(issuesAllocation.getResponsibleDepartmentOrgId()) && null!= issuesAllocation.getDepartmentType() && issuesAllocation.getDepartmentType().intValue()==1).map((issuesAllocation) -> issuesAllocation.getResponsibleDepartmentOrgId()).collect(Collectors.joining(",")));
                }

//              缓存附件信息
                Map<String, Object> selectOverseeIssueAppendixMap = Maps.newHashMap();
                selectOverseeIssueAppendixMap.put("issueId", overseeIssueId);
                overseeIssueById.setOverseeIssueAppendixList(overseeIssueAppendixService.selectOverseeIssueAppendixList(selectOverseeIssueAppendixMap));


//                缓存关联问题编号
                if (StringUtil.isNotEmpty(overseeIssueById.getRelatedIssueIds())) {
                    if (overseeIssueById.getRelatedIssueDataList() == null || overseeIssueById.getRelatedIssueDataList().size() <= 0) {
                        List<Map<String, Object>> relatedIssueDataList = new ArrayList<>();
                        String[] relatedIssuesIdArray = overseeIssueById.getRelatedIssueIds().split(",");
                        if (null != relatedIssuesIdArray && relatedIssuesIdArray.length > 0) {
                            for (String relatedIssuesId : relatedIssuesIdArray) {
                                if (StringUtil.isNotEmpty(relatedIssuesId)) {
                                    try {
                                        OverseeIssue relatedIssuesOverseeIssue = getRedisCacheOverseeIssue(Long.parseLong(relatedIssuesId));
                                        if (null != relatedIssuesOverseeIssue) {
                                            Map<String, Object> relatedIssuesMap = Maps.newHashMap();
                                            relatedIssuesMap.put("id", relatedIssuesOverseeIssue.getId());
                                            if (StringUtil.isNotEmpty(relatedIssuesOverseeIssue.getNum())) {
                                                relatedIssuesMap.put("num", relatedIssuesOverseeIssue.getNum());
                                            }
                                            relatedIssueDataList.add(relatedIssuesMap);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (null != relatedIssueDataList && relatedIssueDataList.size() > 0) {
                            overseeIssueById.setRelatedIssueDataList(relatedIssueDataList);
                        }
                    }
                }

                // 缓存督办部门数据
//                overseeIssueById.setIssuesSupervisorList(issuesSupervisorService.list(Wrappers.<IssuesSupervisor>lambdaQuery().eq(IssuesSupervisor::getIssueId, overseeIssueId).eq(IssuesSupervisor::getDataType, 1)));


                redisCacheOverseeIssue(overseeIssueById);
            }


        }
        return overseeIssueById;
    }

    private String getMyOrgName(String orgId) {
        String orgName = null;
        List<MyOrg> myOrgs = myOrgMapper.selectList(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getOrgId, orgId));
        if (null != myOrgs && myOrgs.size() > 0) {
            return myOrgs.get(0).getOrgName();
        }
        return orgName;
    }

    public OverseeIssue redisCacheOverseeIssue(OverseeIssue overseeIssue) {
        if (null != overseeIssue) {
            if (null != overseeIssue.getId()) {
                redisTemplate.opsForValue().set(BaseConstant.OVERSEE_ISSUE_ID_DATA_PREFIX + overseeIssue.getId(), overseeIssue, Duration.ofDays(360));
                Map<String, Object> overseeIssueMap = JSONObject.parseObject(JSONObject.toJSONString(overseeIssue)).getInnerMap();
                if (null != overseeIssueMap) {
                    redisTemplate.opsForValue().set(BaseConstant.OVERSEE_ISSUE_MAP_ID_DATA_PREFIX + overseeIssue.getId(), overseeIssueMap, Duration.ofDays(360));
                }
            }
        }
        return overseeIssue;
    }

    public XWPFTemplate getCancelANumberWordExport(OverseeIssue overseeIssue){
        try {
            Map<String, Object> dataMap = Maps.newHashMap();

            Assert.isTrue((null != overseeIssue && null != overseeIssue.getId() && overseeIssue.getId().longValue() > 0L), "请传递问题id");
            OverseeIssue redisCacheOverseeIssue = getRedisCacheOverseeIssue(overseeIssue.getId());
            Assert.isTrue((null != redisCacheOverseeIssue && null != redisCacheOverseeIssue.getId() && redisCacheOverseeIssue.getId().longValue() == overseeIssue.getId().longValue()), "不存在当前问题");
            Assert.isTrue((null != redisCacheOverseeIssue.getDataType() && redisCacheOverseeIssue.getDataType().intValue() == 1), "不存在当前问题");

            redisCacheOverseeIssue.setSpecificIssuesContent(HTMLUtils.getTitle(redisCacheOverseeIssue.getSpecificIssuesContent()));


//            Result<Map<String, Object>> cancelANumberResult = getCancelANumber(redisCacheOverseeIssue, req);
            Map<String, Object> selectMap = Maps.newHashMap();
            selectMap.put("overseeIssueId", overseeIssue.getId());
            Map<String, Object> cancelANumberResult = reasonCancellationService.getCancelANumber(selectMap);
            if (null != cancelANumberResult && cancelANumberResult.size()>0) {
                Map<String, Object> cancelANumberMap = cancelANumberResult;
                if (null != cancelANumberMap) {
                    cancelANumberMap.put("accountabilityHandlingCount", (null != cancelANumberMap.get("accountabilityHandlingList") ? (((List) cancelANumberMap.get("accountabilityHandlingList")).size()) : 0));
                    cancelANumberMap.put("rectifyViolationsCount", (null != cancelANumberMap.get("rectifyViolationsList") ? (((List) cancelANumberMap.get("rectifyViolationsList")).size()) : 0));
                    cancelANumberMap.put("improveRegulationsCount", (null != cancelANumberMap.get("improveRegulationsList") ? (((List) cancelANumberMap.get("improveRegulationsList")).size()) : 0));
                    BigDecimal allBigDecimal = new BigDecimal("0");
                    BigDecimal recoveryIllegalDisciplinaryFundsAllBigDecimal = (BigDecimal) cancelANumberMap.get("recoveryIllegalDisciplinaryFundsAllBigDecimal");
                    if (null != recoveryIllegalDisciplinaryFundsAllBigDecimal) {
                        allBigDecimal = allBigDecimal.add(recoveryIllegalDisciplinaryFundsAllBigDecimal);
                    }
                    BigDecimal recoverDamagesAllBigDecimal = (BigDecimal) cancelANumberMap.get("recoverDamagesAllBigDecimal");
                    if (null != recoverDamagesAllBigDecimal) {
                        allBigDecimal = allBigDecimal.add(recoverDamagesAllBigDecimal);
                    }
                    cancelANumberMap.put("allBigDecimal", allBigDecimal);
                    redisCacheOverseeIssue.setCancelANumberMap(cancelANumberMap);
                }
            }

            String submitStateSrc = "";
            if (null != redisCacheOverseeIssue.getSubmitState()) {
                submitStateSrc = commonAPI.translateDict(BaseConstant.ISSUE_SUBMIT_STATE_DICT_KEY, redisCacheOverseeIssue.getSubmitState() + "");
                if (redisCacheOverseeIssue.getSubmitState().intValue() == 2) {
                    submitStateSrc = "该问题已整改完成，建议销号。";
                } else {
                    submitStateSrc = "该问题已取得阶段性成效但需继续整改。";
                }
            }

            String sourceSrc = "";
            Integer source =0;
            if (null != redisCacheOverseeIssue.getSource()) {
                sourceSrc = commonAPI.translateDict(BaseConstant.ISSUE_SOURCE_DICT_KEY, redisCacheOverseeIssue.getSource() + "");
                source=redisCacheOverseeIssue.getSource();
            }

            dataMap.put("overseeIssue", redisCacheOverseeIssue);
            dataMap.put("submitStateSrc", submitStateSrc);
            dataMap.put("sourceSrc", sourceSrc);
            if (StringUtil.isNotEmpty(redisCacheOverseeIssue.getProcessId())) {
                String processId = redisCacheOverseeIssue.getProcessId();
                List<DictModel> taskSetList = commonAPI.queryDictItemsByCode(BaseConstant.TASK_KEY_DICT_KEY);
                Map<String, List<String>> typeMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(taskSetList)) {
                    for (DictModel dictModel : taskSetList) {
                        if (null != dictModel && StringUtil.isNotEmpty(dictModel.getValue())) {
                            typeMap.put(dictModel.getText(), Arrays.asList(dictModel.getValue().split(",")));
                        }
                    }
                } else {
                    log.warn("审核意见流程节点task_key没有设置正确！");
                }
                String nodeId = typeMap.get("SECRETARY_RESPONSIBLE_TASK_KEY_IDS").get(0);
                if(source==1){
                    //巡视最后的书记节点名称需要特殊化处理
                    nodeId = "DECIDE_REJECT_"+nodeId;
                }
                dataMap.putAll(findListByTaskKeyToMap(processId, Arrays.asList(nodeId), "comment5", "comment5Signature", "commentTime5"));
                dataMap.putAll(findListByTaskKeyToMap(processId, typeMap.get("LEAD_RESPONSIBLE_LEADER_TASK_KEY_IDS"), "comment4", "comment4Signature", "commentTime4"));
                dataMap.putAll(findListByTaskKeyToMap(processId, typeMap.get("LEAD_RESPONSIBLE_TASK_KEY_IDS"), "comment3", "comment3Signature", "commentTime3"));
                dataMap.putAll(findListByTaskKeyToMap(processId, typeMap.get("RESPONSIBLE_LEADER_TASK_KEY_IDS"), "comment2", "comment2Signature", "commentTime2"));
                dataMap.putAll(findListByTaskKeyToMap(processId, typeMap.get("RESPONSIBLE_TASK_KEY_IDS"), "comment1", "comment1Signature", "commentTime1"));
            }

            String cancellationWordPath = "";
            List<SystemTemplate> systemTemplateList = systemTemplateService.list(Wrappers.<SystemTemplate>lambdaQuery().eq(SystemTemplate::getTypeCode, BaseConstant.CANCELLATION_FORM_TEMPLATE_KEY).eq(SystemTemplate::getDataType, 1).orderByDesc(SystemTemplate::getUpdateTime));
            if (CollectionUtils.isNotEmpty(systemTemplateList)) {
                SystemTemplate systemTemplate = systemTemplateList.get(0);
                if (null != systemTemplate && StringUtil.isNotEmpty(systemTemplate.getAppendixPath())) {
                    cancellationWordPath = upLoadPath + File.separator + systemTemplate.getAppendixPath();
                }
            }
            if (StringUtil.isEmpty(cancellationWordPath)) {
                cancellationWordPath = upLoadPath + "/cancellationWord.docx";
            }

            return XWPFTemplate.compile(cancellationWordPath).render(dataMap);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);

        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
        return null;
    }

    @Override
    public IPage<OssFileVo> queryOssFilePage(OverseeIssueQueryVo<?> query) {
        return overseeIssueMapper.queryOssFilePage(query);
    }

    private Map<String, Object> findListByTaskKeyToMap(String processId, List<String> taskKeyList, String commentContentName, String commentSignature, String commentTimeName) {
        try {
            List<WorkflowComment> workflowCommentList = workflowCommentService.findListByTaskKeys(processId, taskKeyList);
            if (null != workflowCommentList && workflowCommentList.size() > 0) {
                Map<String, Object> dataMap=setCancalData(workflowCommentList.get(0),commentContentName,commentSignature,commentTimeName);
                //需要把委派和协助部门审批意见一起获取到
                if("comment1".equals(commentContentName)){
                    String wp = WorkflowConstants.CommentTypeEnum.WP.toString();
                    String sp=WorkflowConstants.CommentTypeEnum.SP.toString();
                    //设置委派数据和协助部门审批数据
                    List<Map<String, Object>> wpDataList = Lists.newArrayList();
                    List<WorkflowComment> wpWorkflowCommentList =workflowCommentList.stream().filter(a->a.getTaskType().equals(wp)).collect(Collectors.toList());
                    for (int i = 0; i < wpWorkflowCommentList.size(); i++) {
                        wpDataList.add(setCancalData(wpWorkflowCommentList.get(i),commentContentName,commentSignature,commentTimeName));
                    }
                    dataMap.put(wp,wpDataList);
                    List<WorkflowComment> spWorkflowCommentList =workflowCommentList.stream().filter(a->a.getTaskType().equals(sp)).collect(Collectors.toList());
                    spWorkflowCommentList.remove(0);
                    List<Map<String, Object>> spDataList = Lists.newArrayList();
                    for (int i = 0; i < spWorkflowCommentList.size(); i++) {
                        spDataList.add(setCancalData(spWorkflowCommentList.get(i),commentContentName,commentSignature,commentTimeName));
                    }
                    dataMap.put(sp,spDataList);
                }
                return dataMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> setCancalData(WorkflowComment workflowComment, String commentContentName, String commentSignature, String commentTimeName){
        Map<String, Object> map = Maps.newHashMap();
        String commentContent = "";
        PictureRenderData commentSignatureContent = null;
        Date time = null;
        //只取最新的，驳回的不需要
        if (StringUtil.isNotEmpty(workflowComment.getComment())) {
            commentContent = workflowComment.getComment();
        }
        if (null == commentSignatureContent) {
            OverseeUserSignature overseeUserSignature = overseeUserSignatureService.getSignatureLocalPath(overseeUserSignatureService.findByUserId(workflowComment.getUserId()));
            if (null != overseeUserSignature && StringUtil.isNotEmpty(overseeUserSignature.getSignatureLocalPath())) {
                commentSignatureContent = new PictureRenderData(100, 60, overseeUserSignature.getSignatureLocalPath());
            }
        }
        if (null != workflowComment.getCreateTime()) {
            if (time == null || time.getTime() < workflowComment.getCreateTime().getTime()) {
                time = workflowComment.getCreateTime();
            }
        }
        map.put(commentContentName, commentContent);
        map.put(commentSignature, commentSignatureContent);
        map.put(commentTimeName, ((null != time ? DateUtils.formatDate(time, DateUtils.FORMAT_DATETIME) : "")));
        return map;
    }

    @Override
    public OverseeIssue getRedisCacheOverseeIssue(Long overseeIssueId) {
        OverseeIssue overseeIssue = null;
        if (null != overseeIssueId && overseeIssueId.intValue() > 0) {
            try {
                overseeIssue = (OverseeIssue) redisTemplate.opsForValue().get(BaseConstant.OVERSEE_ISSUE_ID_DATA_PREFIX + overseeIssueId);
            } catch (Exception e) {
                System.out.println(e);
            }
            if (null == overseeIssue || null == overseeIssue.getId() || overseeIssue.getId().intValue() <= 0) {
                try {
                    overseeIssue = redisCacheOverseeIssue(overseeIssueId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return overseeIssue;
    }

    @Override
    public OverseeIssueDetailVo getIssueDetailById(Long issueId) {
        return getIssueDetailById(issueId,null);
    }



    @Override
    public OverseeIssueDetailVo getIssueDetailById(Long issueId,String bindResponsibleOrgIds) {
        OverseeIssueDetailVo result = overseeIssueMapper.getIssueDetailById(issueId);
        if (result != null) {

            List<String> bindResponsibleOrgIdList = null;
            if(StringUtil.isNotEmpty(bindResponsibleOrgIds)){
                String[] bindResponsibleOrgIdArray = bindResponsibleOrgIds.split(",");
                if(null!=bindResponsibleOrgIdArray&&bindResponsibleOrgIdArray.length>0){
                    bindResponsibleOrgIdList = Arrays.asList(bindResponsibleOrgIdArray);
                }
            }

            Map<String, Object> selectIssuesRectificationMeasureMap = Maps.newHashMap();
            selectIssuesRectificationMeasureMap.put("issueId", issueId);
            selectIssuesRectificationMeasureMap.put("orgIdList", bindResponsibleOrgIdList);
            List<IssuesRectificationMeasure> issuesRectificationMeasureList = issuesRectificationMeasureService.selectIssuesRectificationMeasureList(selectIssuesRectificationMeasureMap);
            result.setIssuesRectificationMeasureList(issuesRectificationMeasureList);
            List<OverseeIssueAppendix> overseeIssueAppendixList = overseeIssueAppendixService.list(Wrappers.<OverseeIssueAppendix>lambdaQuery().eq(OverseeIssueAppendix::getIssueId, issueId).eq(OverseeIssueAppendix::getDataType,1));//.eq(OverseeIssueAppendix::getDataId, 1),现在DB这个字段值为null
            overseeIssueAppendixService.synchronizationOverseeIssueAppendixListFileName(overseeIssueAppendixList);
            result.setOverseeIssueAppendixList(overseeIssueAppendixList); //.eq(OverseeIssueAppendix::getDataType, OverseeIssueAppendix.RECTIFICATION_MEASURES_TYPE)
            result.setIssuesRectificationMeasureAppendixIds(overseeIssueAppendixList.stream().filter((overseeIssueAppendix) -> (null != overseeIssueAppendix && null != overseeIssueAppendix.getType() && overseeIssueAppendix.getType().intValue() == OverseeIssueAppendix.RECTIFICATION_MEASURES_TYPE)).map((overseeIssueAppendix) -> overseeIssueAppendix.getAppendixPath()).collect(Collectors.joining(",")));


            OverseeIssueSpecific query = new OverseeIssueSpecific();
            query.setIssueId(result.getId());
            query.setOrgIdList(bindResponsibleOrgIdList);
            List<OverseeIssueSpecific> specificList = overseeIssueSpecificMapper.queryList(query);
            result.setSpecificList(specificList);


            OverseeIssue redisCacheOverseeIssue = getRedisCacheOverseeIssue(issueId);
            if (null != redisCacheOverseeIssue) {
//                result.setOverseeIssueAppendixList(redisCacheOverseeIssue.getOverseeIssueAppendixList());
                result.setRelatedIssueDataList(redisCacheOverseeIssue.getRelatedIssueDataList());
            }
            result.setIssuesSupervisorList(issuesSupervisorService.list(Wrappers.<IssuesSupervisor>lambdaQuery().eq(IssuesSupervisor::getIssueId, issueId).eq(IssuesSupervisor::getIssuesProcessType, 2).eq(IssuesSupervisor::getShowType, 1).eq(IssuesSupervisor::getDataType, 1)));
            result.setSuperviseMapData((Map<String, Object>) redisTemplate.opsForValue().get(issueId + WorkflowConstants.SUPERVISE_MAP_DATA_KEY));


            Map<String, Object> selectMap = Maps.newHashMap();
            selectMap.put("overseeIssueId", result.getId());
            selectMap.put("orgIdList", bindResponsibleOrgIdList);
            result.setCancelANumberMap(reasonCancellationService.getCancelANumber(selectMap));

            List<OverseeIssueFile> fileList = overseeIssueFileMapper.queryListByIssueId(issueId);

            String responsibleMainOrgIdNames = issuesAllocationService.getIssuesAllocationDepartmentNames(result.getId(), IssuesAllocation.MAIN_RESPONSIBLE_DEPARTMENT);
            result.setResponsibleMainOrgNames(StringUtil.isNotEmpty(responsibleMainOrgIdNames)?responsibleMainOrgIdNames:result.getResponsibleDeparts());
            result.setResponsibleCoordinationOrgNames(issuesAllocationService.getIssuesAllocationDepartmentNames(result.getId(),IssuesAllocation.COOPERATE_RESPONSIBLE_DEPARTMENT));


            // 查询督办部门和配合督办部门
            String supervisorMainOrg = issuesSupervisorService.getIssuesAllocationDepartmentNames(result.getId(), IssuesAllocation.MAIN_RESPONSIBLE_DEPARTMENT);
            String supervisorCoordinationOrg = issuesSupervisorService.getIssuesAllocationDepartmentNames(result.getId(), IssuesAllocation.COOPERATE_RESPONSIBLE_DEPARTMENT);
            // 如果没有主要督办部门 但有配合督办部门 则不显示 如果配合督办部门和主要督办部门都没有 则显示所有督办部门
            result.setSupervisorMainOrgNames(StringUtil.isNotEmpty(supervisorMainOrg)?supervisorMainOrg:StringUtils.isNotEmpty(supervisorCoordinationOrg)?"":result.getSupervisorOrg());
            result.setSupervisorCoordinationOrgNames(supervisorCoordinationOrg);

            result.setFileList(fileList);
        }
        return result;
    }

    @Override
    public IPage<OverseeIssueDetailVo> queryIssueDetailPage(OverseeIssueQueryVo query) {
        IPage<OverseeIssueDetailVo> overseeIssueDetailVoIPage = overseeIssueMapper.queryIssueDetailPage(query);
        if(null!=overseeIssueDetailVoIPage&&CollectionUtils.isNotEmpty(overseeIssueDetailVoIPage.getRecords())){
            for(OverseeIssueDetailVo overseeIssueDetailVo : overseeIssueDetailVoIPage.getRecords()){
                if(null!=overseeIssueDetailVo&&null!=overseeIssueDetailVo.getId()){
                    try {
                        // 查询责任部门和配合责任部门
                        String responsibleMainOrgIdNames = issuesAllocationService.getIssuesAllocationDepartmentNames(overseeIssueDetailVo.getId(), IssuesAllocation.MAIN_RESPONSIBLE_DEPARTMENT);
                        overseeIssueDetailVo.setResponsibleMainOrgNames(StringUtil.isNotEmpty(responsibleMainOrgIdNames)?responsibleMainOrgIdNames:overseeIssueDetailVo.getResponsibleDeparts());
                        overseeIssueDetailVo.setResponsibleCoordinationOrgNames(issuesAllocationService.getIssuesAllocationDepartmentNames(overseeIssueDetailVo.getId(),IssuesAllocation.COOPERATE_RESPONSIBLE_DEPARTMENT));


                        // 查询督办部门和配合督办部门
                        String supervisorMainOrg = issuesSupervisorService.getIssuesAllocationDepartmentNames(overseeIssueDetailVo.getId(), IssuesAllocation.MAIN_RESPONSIBLE_DEPARTMENT);
                        String supervisorCoordinationOrg = issuesSupervisorService.getIssuesAllocationDepartmentNames(overseeIssueDetailVo.getId(), IssuesAllocation.COOPERATE_RESPONSIBLE_DEPARTMENT);
                        // 如果没有主要督办部门 但有配合督办部门 则不显示 如果配合督办部门和主要督办部门都没有 则显示所有督办部门
                        overseeIssueDetailVo.setSupervisorMainOrgNames(StringUtil.isNotEmpty(supervisorMainOrg)?supervisorMainOrg:StringUtils.isNotEmpty(supervisorCoordinationOrg)?"":overseeIssueDetailVo.getSupervisorOrg());
                        overseeIssueDetailVo.setSupervisorCoordinationOrgNames(supervisorCoordinationOrg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return overseeIssueDetailVoIPage;
    }


    @Override
    public void getExamineAndVerifyData(List<OverseeIssueDetailVo> OverseeIssueDetails, Boolean isTtmlToText){
        isTtmlToText = null != isTtmlToText ? isTtmlToText : true;
        String newlineCharacter = isTtmlToText ? String.valueOf((char)10) : "<br>";
        if (CollectionUtils.isNotEmpty(OverseeIssueDetails)) {
            for (OverseeIssueDetailVo detail : OverseeIssueDetails) {
                if (null != detail && null != detail.getId()) {
                    OverseeIssueSpecific overseeIssueSpecific = new OverseeIssueSpecific();
                    overseeIssueSpecific.setIssueId(detail.getId());
                    List<OverseeIssueSpecific> overseeIssueSpecifics = overseeIssueSpecificService.queryList(overseeIssueSpecific);
                    if(StringUtils.isNotEmpty(detail.getResponsibleMainOrgNames()))detail.setResponsibleMainOrgNames(detail.getResponsibleMainOrgNames().replaceAll(",",";"));
                    if(StringUtils.isNotEmpty(detail.getResponsibleCoordinationOrgNames()))detail.setResponsibleCoordinationOrgNames(detail.getResponsibleCoordinationOrgNames().replaceAll(",",";"));
                    if(StringUtils.isNotEmpty(detail.getSupervisorMainOrgNames()))detail.setSupervisorMainOrgNames(detail.getSupervisorMainOrgNames().replaceAll(",",";"));
                    if(StringUtils.isNotEmpty(detail.getSupervisorCoordinationOrgNames()))detail.setSupervisorCoordinationOrgNames(detail.getSupervisorCoordinationOrgNames().replaceAll(",",";"));
                    if (CollectionUtils.isNotEmpty(overseeIssueSpecifics)) {
                        StringBuilder improveAction = new StringBuilder();
                        for (OverseeIssueSpecific specific : overseeIssueSpecifics) {
                            if (specific.getSpecificType() != null && OverseeConstants.SpecificType.IMPROVE_ACTION == specific.getSpecificType().intValue()) {
                                String content = specific.getContent();
                                if(StringUtil.isNotEmpty(content)){
                                    content = content.replaceAll("<br>",newlineCharacter);
                                    improveAction.append(getContentIsTtmlToText(content,isTtmlToText,newlineCharacter));
                                }

                            }
                        }
                        String improveActionSrc = improveAction.toString();
                        if(StringUtil.isNotEmpty(improveActionSrc))improveActionSrc = improveActionSrc.replace("<br>",newlineCharacter);
                        detail.setImproveAction(improveActionSrc);
                    }

                    List<ReasonCancellation> reasonCancellationList = reasonCancellationService.list(Wrappers.<ReasonCancellation>lambdaQuery().eq(ReasonCancellation::getIssueId, detail.getId()).eq(ReasonCancellation::getDataType, 1));
                    if (CollectionUtils.isNotEmpty(reasonCancellationList)) {
                        StringBuilder rectification = new StringBuilder();
                        for (ReasonCancellation reasonCancellation : reasonCancellationList) {
                            if (null != reasonCancellation && StringUtil.isNotEmpty(reasonCancellation.getReason())) {
                                String replace = reasonCancellation.getReason().replace("<br>", newlineCharacter);
                                rectification.append(getContentIsTtmlToText(replace,isTtmlToText,newlineCharacter));
                            }
                        }
                        String rectificationSrc = rectification.toString();
                        if(StringUtil.isNotEmpty(rectificationSrc))rectificationSrc = rectificationSrc.replace("<br>",newlineCharacter);
                        detail.setRectification(rectificationSrc);
                    }

                }
//				 if(CollectionUtils.isNotEmpty(detail.getSpecificList())){
//					 StringBuilder improveAction=new StringBuilder();
//					 for(OverseeIssueSpecific specific:detail.getSpecificList()){
//						 if(specific.getSpecificType()!=null &&OverseeConstants.SpecificType.IMPROVE_ACTION== specific.getSpecificType().intValue()){
//							 improveAction.append(specific.getContent());
//						 }
//					 }
//					 detail.setImproveAction(improveAction.toString());
//				 }

            }
        }
    }


    private String getContentIsTtmlToText(String content,Boolean isTtmlToText,String newlineCharacter){
        isTtmlToText = null != isTtmlToText ? isTtmlToText : true;
        String newContent = content;
        if(StringUtil.isNotEmpty(content)){
            if(isTtmlToText){
                newContent = HTMLUtils.getInnerText(content) ;
                if(StringUtil.isNotEmpty(newContent)){
                    newContent += newlineCharacter;
                }
            }
        }
        return newContent;
    }




    private List<MyOrg> getDepartByName(String name) {
        QueryWrapper query = new QueryWrapper();
        query.eq("org_short_name", name);
        List<MyOrg> org = myOrgMapper.selectList(query);
        if (CollectionUtils.isEmpty(org)) {
            query = new QueryWrapper();
            query.eq("org_name", name);
            org = myOrgMapper.selectList(query);
        }
        return org;
    }

    private List<MyOrg> findSubDepartByName(String name, String parentOrgId) {
        List<MyOrg> result = new ArrayList<>();
        List<MyOrg> orgList = myOrgMapper.selectList(Wrappers.<MyOrg>lambdaQuery().eq(MyOrg::getParentOrgId, parentOrgId));
        if (CollectionUtils.isNotEmpty(orgList)) {
            for (MyOrg org : orgList) {
                if (org.getOrgName().equals(name)) {
                    result.add(org);
                } else if (org.getOrgShortName().equals(name)) {
                    result.add(org);
                }
                List<MyOrg> subResult = findSubDepartByName(name, org.getOrgId());
                if (CollectionUtils.isNotEmpty(subResult)) {
                    result.addAll(subResult);
                }
            }
        }
        return result;
    }

    private String joinOrgListId(List<MyOrg> orgs) {
        if (CollectionUtils.isEmpty(orgs)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        result.append(orgs.get(0).getOrgId());
        int i = 1;
        for (int size = orgs.size(); i < size; i++) {
            MyOrg org = orgs.get(i);
            result.append(",").append(org.getOrgId());
        }
        return result.toString();
    }


    @Override
    @Transactional
    public Map<String, Object> importOfflineIssueData(List<OverseeIssueDetailVo> list, LoginUser user) {
        Map<String, Object> resultMap = Maps.newHashMap();
        int resultCount = 0;
        String errorRow = "";
        try {
            if (CollectionUtils.isNotEmpty(list)) {
                Date now = new Date();
                String userId = user.getId();
                for (int i = 0; i < list.size(); i++) {
                    try {
                        OverseeIssueDetailVo item = list.get(i);
                        if (null != item.getSource()) {
                            String jsonStr = JsonUtils.toJsonStr(item);
                            OverseeIssue overseeIssue = JsonUtils.fromJson(jsonStr, OverseeIssue.class);
                            overseeIssue.setCreateUserId(userId);
                            overseeIssue.setUpdateTime(now);
                            overseeIssue.setCreateTime(now);
                            overseeIssue.setUpdateUserId(userId);
                            if ("XC-CFD-201803-026".equals(item.getNum())) {
                                log.warn("找到测试记录");
                            }
                            if (StringUtils.isNotBlank(item.getIssueCategory()) && StringUtils.isNotBlank(item.getIssueSubcategory())) {
                                OverseeIssueCategory category = overseeIssueCategoryService.createOrReplaceCategory(item.getIssueCategory(), item.getIssueSubcategory(), user);
                                if (category != null) {
                                    overseeIssue.setIssueCategoryId(category.getId());
                                    if (CollectionUtils.isNotEmpty(category.getSubcategoryList())) {
                                        overseeIssue.setIssueSubcategoryId(category.getSubcategoryList().get(0).getId());
                                    }
                                }
                            }
                            Assert.notNull(overseeIssue.getIssueCategoryId(), "问题大类不能为空");
                            Assert.notNull(overseeIssue.getIssueSubcategoryId(), "问题小类不能为空");


                            if (StringUtils.isNotBlank(item.getResponsibleUnitOrg())) {
                                if (!item.getResponsibleUnitOrg().trim().equals(BaseConstant.HEADQUARTERS_ORG_NAME)) {
                                    List<MyOrg> org = getDepartByName(item.getResponsibleUnitOrg());
                                    overseeIssue.setResponsibleUnitOrgId(joinOrgListId(org));
                                } else {
                                    overseeIssue.setResponsibleUnitOrgId(BaseConstant.HEADQUARTERS_ORG_ID);
                                }


                                //寻找责任单位下的本部牵头部门
                                if (StringUtils.isNotBlank(overseeIssue.getResponsibleUnitOrgId())) {
//                                  责任单位牵头部门
                                    if (StringUtils.isNotBlank(item.getResponsibleLeadDepartmentOrg())) {
                                        List<MyOrg> responsibleLeadDepart = findSubDepartByName(item.getResponsibleLeadDepartmentOrg(), overseeIssue.getResponsibleUnitOrgId());
                                        overseeIssue.setResponsibleLeadDepartmentOrgId(joinOrgListId(responsibleLeadDepart));
                                    }

//                                  责任单位责任部门
                                    if (StringUtils.isBlank(item.getResponsibleMainOrgNames())) {
                                        item.setResponsibleMainOrgNames(item.getResponsibleDeparts());
                                    }
//                                    整改责任部门
                                    if (StringUtils.isNotBlank(item.getResponsibleMainOrgNames())) {
                                        overseeIssue.setResponsibleMainOrgIds(handleOrgNamesToOrgIds(item.getResponsibleMainOrgNames(),overseeIssue.getResponsibleUnitOrgId()));
                                    }
//                                    配合整改部门
                                    if (StringUtils.isNotBlank(item.getResponsibleCoordinationOrgNames())) {
                                        overseeIssue.setResponsibleCoordinationOrgIds(handleOrgNamesToOrgIds(item.getResponsibleCoordinationOrgNames(),overseeIssue.getResponsibleUnitOrgId()));
                                    }

                                }

                                //TODO  本部牵头部门和督办部门单位id都是HQ
                                if (StringUtils.isNotBlank(item.getHeadquartersLeadDepartmentOrg())) {
                                    List<MyOrg> headquartersLeadDepart = findSubDepartByName(item.getHeadquartersLeadDepartmentOrg(), BaseConstant.HEADQUARTERS_ORG_ID);
                                    overseeIssue.setHeadquartersLeadDepartmentOrgId(joinOrgListId(headquartersLeadDepart));
                                }


//                                处理督办部门
                                if (StringUtils.isBlank(item.getSupervisorMainOrgNames())) {
                                    item.setSupervisorMainOrgNames(item.getSupervisorOrg());
                                }

                                String supervisorOrgIds = "";
                                List<IssuesSupervisor> issuesSupervisorList = new ArrayList<>();
                                if (StringUtils.isNotBlank(item.getSupervisorMainOrgNames())) {
                                    String orgIds = handleOrgNamesToOrgIds(item.getSupervisorMainOrgNames(), BaseConstant.HEADQUARTERS_ORG_ID);
                                    if(StringUtil.isNotEmpty(orgIds)){
                                        supervisorOrgIds = orgIds;
                                        String responsibleMainOrgIds = overseeIssue.getResponsibleMainOrgIds();
                                        issuesSupervisorList.addAll(
                                            Arrays.asList(orgIds.split(",")).stream().filter((orgId)->StringUtil.isNotEmpty(orgId)).map((orgId)->{
                                                    IssuesSupervisor issuesSupervisorI = new IssuesSupervisor();
                                                    issuesSupervisorI.setSupervisorOrgId(orgId);
                                                    issuesSupervisorI.setBindResponsibleOrgIds(responsibleMainOrgIds);
                                                    return issuesSupervisorI;
                                            }).collect(Collectors.toList())
                                        );
                                    }
                                }

                                if (StringUtils.isNotBlank(item.getSupervisorCoordinationOrgNames())) {
                                    String orgIds = handleOrgNamesToOrgIds(item.getSupervisorCoordinationOrgNames(), BaseConstant.HEADQUARTERS_ORG_ID);
                                    if(StringUtil.isNotEmpty(orgIds)){
                                        if(StringUtil.isNotEmpty(supervisorOrgIds)){
                                            supervisorOrgIds += ",";
                                        }
                                        supervisorOrgIds += orgIds;
                                        String responsibleCoordinationOrgIds = overseeIssue.getResponsibleCoordinationOrgIds();
                                        issuesSupervisorList.addAll(
                                                Arrays.asList(orgIds.split(",")).stream().filter((orgId)->StringUtil.isNotEmpty(orgId)).map((orgId)->{
                                                    IssuesSupervisor issuesSupervisorI = new IssuesSupervisor();
                                                    issuesSupervisorI.setSupervisorOrgId(orgId);
                                                    issuesSupervisorI.setBindResponsibleOrgIds(responsibleCoordinationOrgIds);
                                                    return issuesSupervisorI;
                                                }).collect(Collectors.toList())
                                        );
                                    }
                                }

                                overseeIssue.setSupervisorOrgIds(supervisorOrgIds);
                                overseeIssue.setIssuesSupervisorList(issuesSupervisorList);

                            }
//                        int insertCount = overseeIssueMapper.insert(overseeIssue);
                            int insertCount = addOrUpdate(overseeIssue, true);
                            Assert.isTrue(insertCount > 0, "问题保存异常");
                            if (StringUtils.isNotBlank(item.getSpecificIssuesContent())) {
                                SpecificIssues specificIssues = new SpecificIssues();
                                specificIssues.setSpecificIssuesContent(item.getSpecificIssuesContent());
                                specificIssues.setCreateUserId(userId);
                                specificIssues.setUpdateUserId(userId);
                                specificIssues.setCreateTime(now);
                                specificIssues.setUpdateTime(now);
                                specificIssues.setDataType(OverseeConstants.DataType.Enable);
                                specificIssues.setSort(1);
                                specificIssues.setIssueId(overseeIssue.getId());
                                specificIssuesService.addOrUpdate(specificIssues);
                                OverseeIssue overseeIssueU = new OverseeIssue();
                                overseeIssueU.setId(overseeIssue.getId());
                                overseeIssueU.setSpecificIssuesId(specificIssues.getId());
                                overseeIssueU.setUpdateUserId(overseeIssue.getUpdateUserId());
                                addOrUpdate(overseeIssueU);
                            }
                            if (StringUtils.isNotBlank(item.getImproveAction())) {
                                Date expectTime = item.getExpectTime() != null ? item.getExpectTime() : new Date();
                                List<IssuesRectificationMeasure> issuesRectificationMeasures = issuesRectificationMeasureService.srcToIssuesRectificationMeasureList(item.getImproveAction(), expectTime);
                                if (null != issuesRectificationMeasures && issuesRectificationMeasures.size() > 0) {
                                    issuesRectificationMeasureService.addOrUpdateList(issuesRectificationMeasures, overseeIssue.getId(), overseeIssue.getResponsibleOrgIds(), overseeIssue.getUpdateUserId(), null, user.getRealname());
                                }
                            }
                            if (StringUtil.isNotEmpty(item.getRectification()) && StringUtil.isNotEmpty(overseeIssue.getResponsibleOrgIds())) {
                                item.setRectification(item.getRectification().replaceAll("\\n", "<br>"));
                                List<String> responsibleOrgIdList = stream(overseeIssue.getResponsibleOrgIds().split(",")).filter((responsibleOrgId) -> StringUtil.isNotEmpty(responsibleOrgId)).collect(Collectors.toList());
                                if (null != responsibleOrgIdList && responsibleOrgIdList.size() > 0) {
                                    for (String responsibleOrgId : responsibleOrgIdList) {
                                        ReasonCancellation reasonCancellation = new ReasonCancellation();
                                        reasonCancellation.setIssueId(overseeIssue.getId());
                                        reasonCancellation.setReason(item.getRectification());
                                        reasonCancellation.setTaskId("系统导入-" + new Date().getTime() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20));
                                        reasonCancellation.setOrgId(responsibleOrgId);
                                        reasonCancellation.setUpdateUserId(userId);
                                        reasonCancellation.setCreateUserId(userId);
                                        reasonCancellation.setUpdateTime(now);
                                        reasonCancellation.setCreateTime(now);
                                        reasonCancellationService.addOrUpdate(reasonCancellation);
                                    }
                                }
                            }
                            resultCount++;
                        }
                    } catch (Exception ex) {
                        log.error("保存导入问题记录异常", ex);
                        String errorSrc = " <br>    [ " + (i + 2) + " : " + (StringUtil.isNotEmpty(ex.getMessage()) ? (ex.getMessage().length() > 20 ? ex.getMessage().substring(0, 20) : ex.getMessage()) : "") + "]";
                        errorRow += errorSrc;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resultMap.put("resultCount", resultCount);
        resultMap.put("errorRow", errorRow);
        return resultMap;
    }


    private String handleOrgNamesToOrgIds(String orgNames,String responsibleUnitOrgId){
        List<MyOrg> responsibleLeadDepart = new ArrayList<MyOrg>();
        if(StringUtil.isNotEmpty(orgNames)){
            String responsibleDeparts = orgNames;
//            String[] responsibleDepartArray = responsibleDeparts.split(",");
//            分割符号修改为;
            responsibleDeparts = responsibleDeparts.replaceAll(",",";");
            String[] responsibleDepartArray = responsibleDeparts.split(";");
            if (null != responsibleDepartArray && responsibleDepartArray.length > 0) {
                for (String responsibleDepart : responsibleDepartArray) {
                    List<MyOrg> subDepartByNameList = findSubDepartByName(responsibleDepart, responsibleUnitOrgId);
                    if (null != subDepartByNameList && subDepartByNameList.size() > 0) {
                        responsibleLeadDepart.addAll(subDepartByNameList);
                    }
                }
            }
        }
        return joinOrgListId(responsibleLeadDepart);
    }



//    public static void main(String[] args) {
//        String str = "1.提高政治思想认识强化执行力，对上级党委重要文件进行全面贯彻学习。\n" +
//                "2.针对学习内容领会精神，做到学习、有记录、有落实、有工作方法。\n" +
//                "3.加强监督管理对上级重要文件及时贯彻落实，组织应知应会测试防止走过场。\n" +
//                "4.针对公司发展情况结合文件进行研讨，党建工作要向集团内先进的公司学习，提高水平、缩小差距。\n" +
//                "（1）编制印发《党组织理论学习中心组专题学习重点内容安排》，严格落实“第一议题要求”，通过党委会、党委理论学习中心组、专题读书班等形式及时学习习近平总书记的重要论述、重要指示批示精神，集团公司的有关重要会议精神和文件精神；\n" +
//                "（2）严格中心组学习纪律、内容和形式，公司党委书记审定学习方案、研讨主题和重点发言人。提前将学习安排发放给中心组成员，要求重点发言人要结合公司实际、分管工作和个人思想实际进行发言，做到带着体会学，带着思考学，提高集中学习的质量和效果；\n" +
//                "（3）今后中心组学习，公司党委在确定学习研讨内容后，将根据领导分工确定重点发言人；\n" +
//                "（4）公司党委将整理“缺课”内容，组织党委理论中心组进行学习。";
//        Pattern pattern = Pattern.compile("(（?\\d(\\.|）)).*");
////2.选择匹配对象
//        Matcher matcher = pattern.matcher(str);
////与谁匹配？与参数字符串str匹配
//
//
//        int count = 0;
//        while (matcher.find())//matcher.find()用于查找是否有这个字符，有的话返回true
//        {
//            System.out.println("第" + (++count) + "次找到");
//            //start()返回上一个匹配项的起始索引
//            //end()返回上一个匹配项的末尾索引。
//            System.out.println(str.substring(matcher.start(),matcher.end()));
//        }
//
//
//    }

    @Override
    @Transactional
    public Integer modifySubmitState(OverseeIssue overseeIssue) {
        Integer updateCount = overseeIssueMapper.updateSubmitState(overseeIssue);
        redisCacheOverseeIssue(overseeIssue.getId());
        return updateCount;
    }

    @Override
    @Transactional
    public Integer deleteByOverseeIssueId(Long id) {
        int deleteCount = 0;
        Assert.isTrue((null != id && id.intValue() > 0), "请传递数据id");
        OverseeIssue overseeIssue = new OverseeIssue();
        overseeIssue.setId(id);
        overseeIssue.setDataType(-1);
        deleteCount = overseeIssueMapper.updateById(overseeIssue);
        Assert.isTrue((deleteCount > 0), "删除异常");
        redisTemplate.delete(BaseConstant.OVERSEE_ISSUE_ID_DATA_PREFIX + overseeIssue.getId());
        return deleteCount;
    }

    @Override
    @Transactional
    public Integer saveIssueSpecific(OverseeIssueSpecific specific) {
        Assert.isTrue(StringUtils.isNotBlank(specific.getUserId()), "用户不能为空");
        Assert.notNull(specific.getIssueId(), "问题ID不能为空");
        Assert.notNull(specific.getSpecificType(), "类型不能为空");
        OverseeIssue overseeIssue = overseeIssueMapper.selectById(specific.getIssueId());
        Assert.notNull(overseeIssue, "上报问题记录不存在");
        Assert.isTrue(overseeIssue.getSubmitState() != null && overseeIssue.getSubmitState().compareTo(OverseeConstants.SubmitState.Submit) == 0, "当前问题整改状态不支持接受调整整改措施");
        Date now = new Date();
        specific.setUpdateTime(now);
        specific.setCreateTime(specific.getUpdateTime());
        specific.setCreateBy(specific.getUpdateBy());
        Integer result = overseeIssueSpecificMapper.insert(specific);
        return result;
    }

    @Override
    public File issueToDoc(String issueIds, HttpServletRequest req, HttpServletResponse response) throws Exception {
        return null;
    }

    @Override
    @Transactional
    public Integer updateDepartmentByMap(Map<String, Object> updateDataMap, LoginUser sysUser){
        int updateCount = 0;
        Assert.isTrue((null != updateDataMap && updateDataMap.size() > 0), "请传递修改数据");
        Assert.isTrue((null != sysUser && StringUtil.isNotEmpty(sysUser.getId())), "未找到登录用户");
        Integer updateType = (Integer) updateDataMap.get("updateType");
        Assert.isTrue((null!=updateType), "请传递修改类型");
        List<Integer> overseeIssueIdList = (List<Integer>) updateDataMap.get("overseeIssueIdList");
        String updateSource = null;
        if(null!=updateType){
            if(updateType.intValue() == 1){

            }else if(updateType.intValue() == 2){
                updateSource = WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE;
            }
        }
        Assert.isTrue((StringUtil.isNotEmpty(updateSource)), "请传递修改类型");
        String originalDepartment = (String) updateDataMap.get("originalDepartment");
        Assert.isTrue((StringUtil.isNotEmpty(originalDepartment)), "请传递原部门");
        String updateDepartment = (String) updateDataMap.get("updateDepartment");
        Assert.isTrue((StringUtil.isNotEmpty(updateDepartment)), "请传递修改部门");
        if(!originalDepartment.equals(updateDepartment)){
            WorkflowDepart originalManagerWorkflowDepart = initWorkflowDepart(originalDepartment,WorkflowConstants.DEPART_ROLE.MANAGER);
            WorkflowDepart originalManagerDepart = workflowDepartService.findDepart(originalManagerWorkflowDepart);
            Assert.isTrue((null!=originalManagerDepart), "未找到当前部门");
            WorkflowDepart originalSupervisorWorkflowDepart = initWorkflowDepart(originalDepartment,WorkflowConstants.DEPART_ROLE.SUPERVISOR);
            WorkflowDepart originalSupervisorDepart = workflowDepartService.findDepart(originalSupervisorWorkflowDepart);
            Assert.isTrue((null!=originalSupervisorDepart), "未找到当前部门");


            WorkflowDepart updateManagerWorkflowDepart = initWorkflowDepart(updateDepartment,WorkflowConstants.DEPART_ROLE.MANAGER);
            WorkflowDepart updateManagerDepart = workflowDepartService.findDepart(updateManagerWorkflowDepart);
            if(updateManagerDepart == null){
                updateManagerWorkflowDepart = workflowProcessDepartService.generateDepart(updateDepartment,WorkflowConstants.DEPART_ROLE.MANAGER);
            }
            Assert.isTrue((null!=updateManagerWorkflowDepart), "未找到当前修改部门");

            WorkflowDepart updateSupervisorWorkflowDepart = initWorkflowDepart(updateDepartment,WorkflowConstants.DEPART_ROLE.SUPERVISOR);
            WorkflowDepart updateSupervisorDepart = workflowDepartService.findDepart(updateSupervisorWorkflowDepart);
            if(updateSupervisorDepart == null){
                updateSupervisorDepart = workflowProcessDepartService.generateDepart(updateDepartment,WorkflowConstants.DEPART_ROLE.SUPERVISOR);
            }
            Assert.isTrue((null!=updateSupervisorDepart), "未找到当前修改部门");

            LambdaQueryWrapper<OverseeIssue> overseeIssueWrapper = Wrappers.<OverseeIssue>lambdaQuery().eq(OverseeIssue::getDataType,1);
            if(updateSource.equals(WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE)){
                overseeIssueWrapper.eq(OverseeIssue::getResponsibleLeadDepartmentOrgId,originalDepartment);
            }

            if(CollectionUtils.isNotEmpty(overseeIssueIdList)){
                overseeIssueWrapper.in(OverseeIssue::getId,overseeIssueIdList);
            }

            List<OverseeIssue> overseeIssueList = overseeIssueMapper.selectList(overseeIssueWrapper);
//            Set<String> processInstanceIds = new HashSet<>();
            Map<String,Object> overseeIssueProcessInstanceMap = Maps.newHashMap();
            Map<Long,String> overseeIssueIdProcessInstanceIdMap = Maps.newHashMap();
            if(CollectionUtils.isNotEmpty(overseeIssueList)){
                for(OverseeIssue overseeIssue : overseeIssueList){
                    if(null!=overseeIssue&&StringUtil.isNotEmpty(overseeIssue.getProcessId())){
//                        processInstanceIds.add(overseeIssue.getProcessId());
                        overseeIssueProcessInstanceMap.put(overseeIssue.getProcessId(),overseeIssue.getId());
                    }
                    overseeIssueIdProcessInstanceIdMap.put(overseeIssue.getId(),overseeIssue.getProcessId());
                }
            }

            if(overseeIssueProcessInstanceMap.size()>0){
                List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(overseeIssueProcessInstanceMap.keySet()).list();
                if(CollectionUtils.isNotEmpty(processInstanceList)){
                    for(ProcessInstance processInstance : processInstanceList){
                        try{
//                            流程数据不论是否处理成功，都不再修改数据库数据 避免流程数据和数据库数据不同步
                            Long overseeIssueId = (Long) overseeIssueProcessInstanceMap.get(processInstance.getId());
                            overseeIssueIdProcessInstanceIdMap.remove(overseeIssueId);
//                            开启新事务 避免流程数据和问题数据修改不同步
                            IOverseeIssueService overseeIssueService = (IOverseeIssueService) AopContext.currentProxy();
                            overseeIssueService.updateOverseeIssueAndProcessInstanceDepartment(
                                     updateSource,  processInstance,  originalDepartment,
                                     updateDepartment,  sysUser,  overseeIssueProcessInstanceMap, overseeIssueIdProcessInstanceIdMap,
                                     updateManagerDepart, updateSupervisorDepart
                            );

                            updateCount++;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            if(overseeIssueIdProcessInstanceIdMap.size()>0){

                for(Long overseeIssueId : overseeIssueIdProcessInstanceIdMap.keySet()){
                    if(null!=overseeIssueId&&overseeIssueId.longValue()>0){
                        try{
                            updateOverseeIssueDepartment(overseeIssueId,updateSource,updateDepartment,sysUser);
//                            overseeIssueIdProcessInstanceIdMap.remove(overseeIssueId);
                            updateCount++;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

//                if(null!=overseeIssueIdProcessInstanceIdMap.keySet()&&overseeIssueIdProcessInstanceIdMap.keySet().size()>0){
//                    Iterator<Long> iterator = overseeIssueIdProcessInstanceIdMap.keySet().iterator();
//                    while (iterator.hasNext()){
//                        Long overseeIssueId = iterator.next();
//                        if(null!=overseeIssueId&&overseeIssueId.longValue()>0){
//                            try{
//                                updateOverseeIssueDepartment(overseeIssueId,updateSource,updateDepartment,sysUser);
//                                overseeIssueIdProcessInstanceIdMap.remove(overseeIssueId);
//                                updateCount++;
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
            }


        }
        return updateCount;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateOverseeIssueAndProcessInstanceDepartment(
            String updateSource, ProcessInstance processInstance, String originalDepartment,
            String updateDepartment, LoginUser sysUser, Map<String, Object> overseeIssueProcessInstanceMap, Map<Long, String> overseeIssueIdProcessInstanceIdMap,
            WorkflowDepart updateManagerDepart, WorkflowDepart updateSupervisorDepart
    ){
        workflowProcessDepartService.updateWorkflowProcessDepartNewDepartByOldDepart(processInstance.getId(),updateSource,originalDepartment,updateDepartment,sysUser.getId());
        Long overseeIssueId = (Long) overseeIssueProcessInstanceMap.get(processInstance.getId());
        if(null!=overseeIssueId&&overseeIssueId.longValue()>0){
            int updateOverseeIssueDepartmentCount = updateOverseeIssueDepartment(overseeIssueId,updateSource,updateDepartment,sysUser);
//            overseeIssueIdProcessInstanceIdMap.remove(overseeIssueId);
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        if(CollectionUtils.isNotEmpty(taskList)){
            for(Task task : taskList){
                if(null!=task&&StringUtil.isNotEmpty(task.getId())){
                    FlowElement flowElement = WorkflowFlowElementUtils.getFlowElementByActivityIdAndProcessDefinitionId(task.getTaskDefinitionKey(), task.getProcessDefinitionId());
                    List<String> candidateGroups = null;
                    List<String> candidateUsers = null;
                    if(null!=flowElement){
                        if(flowElement instanceof UserTask) {
                            candidateGroups = ((UserTask) flowElement).getCandidateGroups();
                            candidateUsers = ((UserTask) flowElement).getCandidateUsers();
                        }
                    }
//                   根据修改类型 查询候选组 删除原候选组数据 增加新的候选组数据
                    if(updateSource.equals(WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE)){
                        String managerCandidateGroup = "${"+(updateSource + "_" + WorkflowConstants.DEPART_ROLE.MANAGER)+"}";
                        String supervisorCandidateGroup = "${"+(updateSource + "_" + WorkflowConstants.DEPART_ROLE.SUPERVISOR)+"}";
                        if(
                                candidateGroups.contains(managerCandidateGroup)
                                        ||candidateGroups.contains(supervisorCandidateGroup)
                        ){
                            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
//                            List<? extends IdentityLinkInfo> identityLinks = task.getIdentityLinks();
                            if(CollectionUtils.isNotEmpty(identityLinks)){
                                for(IdentityLinkInfo identityLinkInfo : identityLinks){
                                    if(null!=identityLinkInfo){
                                        if(StringUtil.isNotEmpty(identityLinkInfo.getType())){
                                            if(identityLinkInfo.getType().equals("candidate")){
                                                taskService.deleteCandidateGroup(task.getId(),identityLinkInfo.getGroupId());
                                            }
                                        }
                                    }
                                }
                            }

                            if(candidateGroups.contains(managerCandidateGroup)){
                                taskService.addCandidateGroup(task.getId(),updateManagerDepart.getId());
                            }else if(candidateGroups.contains(supervisorCandidateGroup)){
                                taskService.addCandidateGroup(task.getId(),updateSupervisorDepart.getId());
                            }
                        }
                    }
                }
            }
        }

        runtimeService.setVariable(processInstance.getId(),updateSource + "_" + WorkflowConstants.DEPART_ROLE.MANAGER,Arrays.asList(new String[]{updateManagerDepart.getId()}));
        runtimeService.setVariable(processInstance.getId(),updateSource + "_" + WorkflowConstants.DEPART_ROLE.SUPERVISOR,Arrays.asList(new String[]{updateSupervisorDepart.getId()}));

    }

    private Integer updateOverseeIssueDepartment(Long overseeIssueId,String updateSource,String updateDepartment,LoginUser sysUser){
        int updateCount = 0;
        if(null!=overseeIssueId&&overseeIssueId.longValue()>0l){
            OverseeIssue overseeIssue = new OverseeIssue();
            overseeIssue.setId(overseeIssueId);
            if(updateSource.equals(WorkflowConstants.DEPART_SOURCE.RESPONSIBLE_HANDLE)){
                overseeIssue.setResponsibleLeadDepartmentOrgId(updateDepartment);
            }
            overseeIssue.setUpdateUserId(sysUser.getId());
            updateCount = addOrUpdate(overseeIssue);
        }
        return updateCount;
    }



    private WorkflowDepart initWorkflowDepart(String departId,String role){
        WorkflowDepart workflowDepart = new WorkflowDepart();
        workflowDepart.setDepartId(departId);
        workflowDepart.setRole(role);
        return workflowDepart;
    }



}
