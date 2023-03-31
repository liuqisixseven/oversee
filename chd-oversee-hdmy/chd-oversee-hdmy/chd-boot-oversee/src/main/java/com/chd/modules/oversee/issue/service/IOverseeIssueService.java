package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chd.common.system.vo.LoginUser;
import com.chd.modules.oversee.issue.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.workflow.entity.WorkflowDepart;
import com.deepoove.poi.XWPFTemplate;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @Description: oversee_issue
 * @Author: jeecg-boot
 * @Date:   2022-08-03
 * @Version: V1.0
 */
public interface IOverseeIssueService extends IService<OverseeIssue> {

    int addOrUpdate(OverseeIssue overseeIssue);

    @Transactional
    int addOrUpdate(OverseeIssue overseeIssue, boolean allUpdate);

    IPage<OverseeIssue> selectOverseeIssuePageVo(Page<?> page, Map<String, Object> map);

    List<OverseeIssue> selectOverseeIssueList(Map<String, Object> map);

    public OverseeIssue redisCacheOverseeIssue(Long overseeIssueId);

    public OverseeIssue redisCacheOverseeIssue(OverseeIssue overseeIssue);

    public OverseeIssue getRedisCacheOverseeIssue(Long overseeIssueId);

    /**
     * 获取上报详情
     * @param issueId
     * @return
     */
    OverseeIssueDetailVo getIssueDetailById(Long issueId);

    OverseeIssueDetailVo getIssueDetailById(Long issueId,String bindResponsibleOrgIds);

    IPage<OverseeIssueDetailVo> queryIssueDetailPage(OverseeIssueQueryVo query);

    void getExamineAndVerifyData(List<OverseeIssueDetailVo> OverseeIssueDetails, Boolean isTtmlToText);

    Map<String,Object> importOfflineIssueData(List<OverseeIssueDetailVo> list, LoginUser user);

    Integer modifySubmitState(OverseeIssue overseeIssue);

    Integer deleteByOverseeIssueId(Long id);

    /**
     * 保存问题细节
     * @param specific
     * @return
     */
    Integer saveIssueSpecific(OverseeIssueSpecific specific);

    /**
     * 问题归档
     * @param issueIds
     */
    File issueToDoc(String issueIds, HttpServletRequest req, HttpServletResponse response) throws Exception;

    XWPFTemplate getCancelANumberWordExport(OverseeIssue overseeIssue);

    IPage<OssFileVo> queryOssFilePage(OverseeIssueQueryVo<?> query);

    Integer updateDepartmentByMap(Map<String, Object> updateDataMap, LoginUser sysUser);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void updateOverseeIssueAndProcessInstanceDepartment(
            String updateSource, ProcessInstance processInstance, String originalDepartment,
            String updateDepartment, LoginUser sysUser, Map<String, Object> overseeIssueProcessInstanceMap, Map<Long, String> overseeIssueIdProcessInstanceIdMap,
            WorkflowDepart updateManagerDepart, WorkflowDepart updateSupervisorDepart
    );
}
