package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chd.modules.oversee.issue.entity.OverseeUserSignature;
import com.chd.modules.oversee.issue.entity.OverseeUserSignatureQueryVo;

import java.util.List;

public interface IOverseeUserSignatureService {

    /**
     * 添加
     * @param overseeUserSignature
     * @return
     */
    Integer insert(OverseeUserSignature overseeUserSignature);

    /**
     * 获取列表
     * @param query
     * @return
     */
    IPage<OverseeUserSignature> queryListPage(OverseeUserSignatureQueryVo query);


    /**
     * 根据id修改
     * @param overseeUserSignature
     * @return
     */
    Integer updateById(OverseeUserSignature overseeUserSignature);


    List<OverseeUserSignature> getSignatureListLocalPath(List<OverseeUserSignature> overseeUserSignatureList);

    OverseeUserSignature getSignatureLocalPath(OverseeUserSignature overseeUserSignature);

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
    OverseeUserSignature findById(Long id);

    /**
     * 根据用户ID查找
     * @param userId
     * @return
     */
    OverseeUserSignature findByUserId(String userId);

    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    Integer deleteBatchById(List<Long> ids);
}
