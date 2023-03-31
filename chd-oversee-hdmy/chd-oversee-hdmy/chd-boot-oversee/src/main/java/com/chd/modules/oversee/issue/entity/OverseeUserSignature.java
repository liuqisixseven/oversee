package com.chd.modules.oversee.issue.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import org.apache.ibatis.type.Alias;
import java.io.Serializable;
import lombok.Data;

import java.util.Date;


/**
 * 用户签名
 * @author ljc
 */
@Alias("overseeUserSignature")
@Data
public class OverseeUserSignature {
    private Long id;//主键
    private String userId;//用户id
    private String signatureData;//签名数据

    private String createBy;//创建人
    private Date createTime;//创建时间
    private String updateBy;//更新人
    private Date updateTime;//更新时间
    @TableField(exist = false)
    private String signatureLocalPath;//签名文件路径
    @TableField(exist = false)
    private Integer rotate;//签名文件路径
}
