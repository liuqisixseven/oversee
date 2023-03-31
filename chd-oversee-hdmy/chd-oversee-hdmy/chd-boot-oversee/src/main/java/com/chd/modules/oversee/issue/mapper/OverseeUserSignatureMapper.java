package com.chd.modules.oversee.issue.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chd.modules.oversee.issue.entity.OverseeUserSignature;
import com.chd.modules.oversee.issue.entity.OverseeUserSignatureQueryVo;

import java.util.List;

/**
 * 用户签名
 * @author ljc
 */
public interface OverseeUserSignatureMapper {

    /**
     * @param overseeUserSignature
     * @return
     */
    Integer insert(OverseeUserSignature overseeUserSignature);

    /**
     * 根据id修改
     * @param overseeUserSignature
     * @return
     */
    Integer updateById(OverseeUserSignature overseeUserSignature);

    IPage<OverseeUserSignature> selectListPage(OverseeUserSignatureQueryVo query);

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
     * 根据用户ID获取记录
     * @param userId
     * @return
     */
    OverseeUserSignature findByUserId(String userId);

    /**
     * 根据id查找
     * @param list
     * @return
     */
    OverseeUserSignature findByIdList(List<Long> list);


    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    Integer deleteBatchById (List<Long> ids);
}
