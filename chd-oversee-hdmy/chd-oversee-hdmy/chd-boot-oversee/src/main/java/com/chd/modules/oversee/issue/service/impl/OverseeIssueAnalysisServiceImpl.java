package com.chd.modules.oversee.issue.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chd.common.util.DateUtils;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.IssueAnalysisInfoVo;
import com.chd.modules.oversee.issue.entity.IssueAnalysisItemVo;
import com.chd.modules.oversee.issue.entity.IssueAnalysisQueryVo;
import com.chd.modules.oversee.issue.entity.OverseeIssueQueryVo;
import com.chd.modules.oversee.issue.mapper.OverseeIssueAnalysisMapper;
import com.chd.modules.oversee.issue.mapper.OverseeIssueMapper;
import com.chd.modules.oversee.issue.service.IOverseeIssueAnalysisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class OverseeIssueAnalysisServiceImpl implements IOverseeIssueAnalysisService {

    @Autowired
    private OverseeIssueAnalysisMapper overseeIssueAnalysisMapper;

    @Autowired
    private OverseeIssueMapper overseeIssueMapper;

    @Override
    @Transactional
    public IssueAnalysisInfoVo issueInfo(IssueAnalysisQueryVo query) {
        IssueAnalysisInfoVo result= overseeIssueAnalysisMapper.issueBaseInfo(query);
        result.setSource(overseeIssueAnalysisMapper.issueSource(query));
        result.setCategory(overseeIssueAnalysisMapper.onlyCategory(query));
        result.setFullCategory(overseeIssueAnalysisMapper.issueCategory(query));
//        result.setCheckTime(overseeIssueAnalysisMapper.issueCheckTime(query));
//        result.setCompletedTime(overseeIssueAnalysisMapper.issueCompletedTime(query));
//        result.setCreateTime(overseeIssueAnalysisMapper.issueCreateTime(query));
//        result.setReportTime(overseeIssueAnalysisMapper.issueReportTime(query));
        return result;
    }

    @Override
    public List<IssueAnalysisItemVo> issueListByTime(IssueAnalysisQueryVo query) {
        if("createTime".equalsIgnoreCase(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.issueCreateTime(query);
        }else if("reportTime".equalsIgnoreCase(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.issueReportTime(query);
        }else if("checkTime".equalsIgnoreCase(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.issueCheckTime(query);
        }else if("completedTime".equalsIgnoreCase(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.issueCompletedTime(query);
        }
        return null;
    }

    @Override
    public List<IssueAnalysisItemVo> issueListByType(IssueAnalysisQueryVo query) {
        if("supervisorOrg".equals(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.supervisorOrg(query);
        }else if("responsibleUnit".equals(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.responsibleUnit(query);
        }else if("issueSubmitState".equals(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.issueSubmitState(query);
        }else if("category".equals(query.getAnalysisName())){
            return overseeIssueAnalysisMapper.onlyCategory(query);
        }

        return null;
    }

    @Override
    public List<IssueAnalysisItemVo> overdueSituation(IssueAnalysisQueryVo query) {
        List<IssueAnalysisItemVo> issueAnalysisItemVoList = new ArrayList<IssueAnalysisItemVo>();

        query.setExpectTimeGt(new Date(new Date().getTime() + 30L * 24L * 60L * 60L * 1000L));
        issueAnalysisItemVoList.add(issueAnalysisItemVoNullIsDefaultAndSetName(overseeIssueAnalysisMapper.overdueSituation(query),"正常计划"));
        query.setExpectTimeGt(new Date());
        query.setExpectTimeLt(new Date(new Date().getTime() + 30L * 24L * 60L * 60L * 1000L));
        issueAnalysisItemVoList.add(issueAnalysisItemVoNullIsDefaultAndSetName(overseeIssueAnalysisMapper.overdueSituation(query),"即将超期"));
        query.setExpectTimeGt(null);
        query.setExpectTimeLt(new Date());
        issueAnalysisItemVoList.add(issueAnalysisItemVoNullIsDefaultAndSetName(overseeIssueAnalysisMapper.overdueSituation(query),"已超期"));

        return issueAnalysisItemVoList;
    }

    @Override
    public IssueAnalysisInfoVo newInfo(OverseeIssueQueryVo query) {
        IssueAnalysisInfoVo result= new IssueAnalysisInfoVo();
        try{
            if(null!=query){
                if(StringUtil.isNotEmpty(query.getSelectTypes())){
                    String[] selectTypeArray = query.getSelectTypes().split(",");
                    if(null!=selectTypeArray&&selectTypeArray.length>0){
                        for(String selectTypeSrc :selectTypeArray){
                            try{
                                if(StringUtil.isNotEmpty(selectTypeSrc)){
                                    int selectType = Integer.parseInt(selectTypeSrc);
                                    query.setSelectType(selectType);
                                    // 首页统计只统计整改中和已整改
                                    query.setSubmitStateList(Arrays.asList(new Integer[]{1,2,-1,3}));
                                    if(selectType==1){

                                    } else if(selectType==2){

                                    }else if(selectType==3){
                                        query.setSelectType(null);
                                        query.setExpectTimeGt(new Date(new Date().getTime() + 30L * 24L * 60L * 60L * 1000L));
                                        query.setExpectTimeLt(null);
//                                    query.setCompletedTimeout(1);
                                    }else if(selectType==4){
                                        query.setSelectType(null);
                                        query.setExpectTimeGt(new Date());
                                        query.setExpectTimeLt(new Date(new Date().getTime() + 30L * 24L * 60L * 60L * 1000L));
                                    }else if(selectType==5){
                                        query.setSelectType(null);
                                        query.setExpectTimeGt(null);
                                        query.setExpectTimeLt(null);
                                        query.setCompletedTimeout(2);
                                    }else if(selectType==7){
                                        query.setSelectType(6);
                                        query.setSubmitStateList(Arrays.asList(new Integer[]{2,-1,3}));
                                    }else if(selectType==8){
                                        query.setSelectType(6);
                                        query.setSubmitState(1);
                                    }else if(selectType==13){
                                        query.setSelectType(12);
                                        if(null!=query.getCheckTimeSection()&&query.getCheckTimeSection().size()>0){
                                            List<String> completedTimeSection = new ArrayList<>();
                                            if(null!=query.getCheckTimeSection()&&StringUtil.isNotEmpty((String) query.getCheckTimeSection().get(0))){
                                                Date nextYearPreDay = getNextYearPreDay(DateUtils.parseDate((String) query.getCheckTimeSection().get(0), DateUtils.FORMAT_DATE));
                                                if(null!=nextYearPreDay){
                                                    completedTimeSection.add(DateUtils.formatDate(nextYearPreDay,DateUtils.FORMAT_DATE));
                                                    if(query.getCheckTimeSection().size()>1){
                                                        if(null!=query.getCheckTimeSection()&&StringUtil.isNotEmpty((String) query.getCheckTimeSection().get(1))){
                                                            Date nextYearPreDay1 = getNextYearPreDay(DateUtils.parseDate((String) query.getCheckTimeSection().get(1), DateUtils.FORMAT_DATE));
                                                            if(null!=nextYearPreDay1){
                                                                completedTimeSection.add(DateUtils.formatDate(nextYearPreDay1,DateUtils.FORMAT_DATE));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if(CollectionUtils.isNotEmpty(completedTimeSection)){
                                                query.setCompletedTimeSection(completedTimeSection);
                                                if(CollectionUtils.isNotEmpty(query.getCompletedTimeSection())){
                                                    List<String> checkTimeSection = query.getCompletedTimeSection();
                                                    query.setCompletedTimeGt(checkTimeSection.get(0));
                                                    if(query.getCheckTimeSection().size()>1){
                                                        query.setCompletedTimeLt(checkTimeSection.get(1));
                                                    }
                                                }
                                            }
                                        }
                                    }


                                    List<IssueAnalysisItemVo> issueAnalysisItemVos = overseeIssueMapper.queryIssueDetailAnalysisList(JSONObject.parseObject(JSONObject.toJSONString(query)).getInnerMap());
                                    if(selectType==1){
                                        result.setSource(issueAnalysisItemVos);
                                    } else if(selectType==2) {
                                        result.setSubmitStateList(issueAnalysisItemVos);
                                    }else if(selectType==3||selectType==4||selectType==5) {
                                        if(result.getOverdueSituationList()==null){
                                            result.setOverdueSituationList(new ArrayList<>());
                                        }
                                        if(selectType==3){
                                            result.getOverdueSituationList().add(issueAnalysisItemVoNullIsDefaultAndSetName(issueAnalysisItemVos.get(0),"正常计划"));
                                        }else if(selectType==4){
                                            result.getOverdueSituationList().add(issueAnalysisItemVoNullIsDefaultAndSetName(issueAnalysisItemVos.get(0),"即将超期"));
                                        }else if(selectType==5){
                                            result.getOverdueSituationList().add(issueAnalysisItemVoNullIsDefaultAndSetName(issueAnalysisItemVos.get(0),"已超期"));
                                        }
                                    } else if(selectType==6) {
                                        result.setAllList(issueAnalysisItemVos);
                                    } else if (selectType==7) {
                                        result.setCompleteList(issueAnalysisItemVos);
                                    }else if(selectType==8){
                                        result.setUndoneList(issueAnalysisItemVos);
                                    }else if(selectType==10){
                                        result.setSuperviseList(issueAnalysisItemVos);
                                    }else if(selectType==12){
                                        result.setAllList(issueAnalysisItemVos);
                                    }else if(selectType==13){
                                        result.setRemainingProblems(issueAnalysisItemVos);
                                    }else if(selectType==15){
                                        result.setCategory(issueAnalysisItemVos);
                                    }else if(selectType==16){
                                        result.setIssueSubcategoryList(issueAnalysisItemVos);
                                    }else if(selectType==17){
                                        result.setFullCategory(issueAnalysisItemVos);
                                    }else{
                                        result.setAllList(issueAnalysisItemVos);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static Date getNextYearPreDay(Date date) {
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(date);   //设置时间为当前时间
        ca.add(Calendar.YEAR, +1); //年份+1
        return ca.getTime();
    }


    private IssueAnalysisItemVo issueAnalysisItemVoNullIsDefaultAndSetName(IssueAnalysisItemVo issueAnalysisItemVo,String name){
        if(null==issueAnalysisItemVo){
            issueAnalysisItemVo = new IssueAnalysisItemVo();
            issueAnalysisItemVo.setValue(0L);
        }
        issueAnalysisItemVo.setName(name);
        return issueAnalysisItemVo;
    }
}
