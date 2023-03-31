package com.chd.modules.oversee.issue.service;

import com.chd.modules.oversee.issue.entity.OverseeIssueSpecific;

import java.util.List;

/**
 * 问题细节表
 */
public interface IOverseeIssueSpecificService {
    /**
     * @param overseeIssueSpecific
     * @return
     */
    Integer insert(OverseeIssueSpecific overseeIssueSpecific);

    /**
     * 获取列表
     * @param query
     * @return
     */
    List<OverseeIssueSpecific> queryList(OverseeIssueSpecific query);

    /**
     * 根据id修改
     * @param overseeIssueSpecific
     * @return
     */
    Integer updateById(OverseeIssueSpecific overseeIssueSpecific);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    Integer deleteById(Long id);

    /**
     * 根据id查找
     * @param id
     * @return
     */
    OverseeIssueSpecific findById(Long id);

    /**
     * 根据id查找
     * @param list
     * @return
     */
    OverseeIssueSpecific findByIdList(List<Long> list);


    /**
     * 根据id批量删除
     * @param list
     * @return
     */
    Integer deleteBatchById (List<Long> list);
}
