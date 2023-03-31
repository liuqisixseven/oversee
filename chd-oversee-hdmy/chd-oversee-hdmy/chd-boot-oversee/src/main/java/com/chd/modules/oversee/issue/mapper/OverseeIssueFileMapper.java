package com.chd.modules.oversee.issue.mapper;

import com.chd.modules.oversee.issue.entity.OverseeIssueFile;
import com.chd.modules.oversee.issue.entity.OverseeIssueFiles;

import java.util.List;

public interface OverseeIssueFileMapper {

    /**
     * @param overseeIssueFile
     * @return
     */
    Integer insert(OverseeIssueFile overseeIssueFile);

    /**
     * 获取列表
     * @param overseeIssueFile
     * @return
     */
    List<OverseeIssueFile> queryList(OverseeIssueFile overseeIssueFile);

    List<OverseeIssueFile> queryListByIssueId(Long issueId);

    List<OverseeIssueFile> selectOverseeFileList(Long issueId);

    /**
     * 根据id修改
     * @param overseeIssueFile
     * @return
     */
    Integer updateById(OverseeIssueFile overseeIssueFile);


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
    OverseeIssueFile findById(Long id);

    /**
     * 根据id查找
     * @param list
     * @return
     */
    OverseeIssueFile findByIdList(List<Long> list);


    /**
     * 根据id批量删除
     * @param list
     * @return
     */
    Integer deleteBatchById (List<Long> list);
}
