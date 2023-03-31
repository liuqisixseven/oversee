package com.chd.modules.oversee.issue.entity;

import com.chd.common.api.vo.PageVo;
import lombok.Data;

import java.util.Date;

@Data
public class OverseeUserSignatureQueryVo<T> extends PageVo {
    private Long id;//主键
    private String userId;//用户id
    private String signatureData;//签名数据
    private String createBy;//创建人
    private Date createTime;//创建时间
    private String updateBy;//更新人
    private Date updateTime;//更新时间
}
