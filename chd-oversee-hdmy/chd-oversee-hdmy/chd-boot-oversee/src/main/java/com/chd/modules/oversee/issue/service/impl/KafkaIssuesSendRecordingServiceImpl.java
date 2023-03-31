package com.chd.modules.oversee.issue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chd.common.constant.OverseeConstants;
import com.chd.common.util.StringUtil;
import com.chd.modules.oversee.issue.entity.KafkaIssuesSendRecording;
import com.chd.modules.oversee.issue.mapper.KafkaIssuesSendRecordingMapper;
import com.chd.modules.oversee.issue.service.IKafkaIssuesSendRecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
@Service
@Transactional(readOnly = true)
public class KafkaIssuesSendRecordingServiceImpl extends ServiceImpl<KafkaIssuesSendRecordingMapper, KafkaIssuesSendRecording> implements IKafkaIssuesSendRecordingService {

    @Autowired
    KafkaIssuesSendRecordingMapper KafkaIssuesSendRecordingMapper;


    @Override
    @Transactional
    public int addOrUpdate(KafkaIssuesSendRecording kafkaIssuesSendRecording) {
        int addOrUpdateCount = 0;
        Assert.isTrue((null!=kafkaIssuesSendRecording),"请传递数据");
        Assert.isTrue((null!=kafkaIssuesSendRecording.getIssueId()&&kafkaIssuesSendRecording.getIssueId().longValue()>0),"请传递问题id");
        Assert.isTrue((StringUtil.isNotEmpty(kafkaIssuesSendRecording.getUpdateUserId())),"未获取到修改用户id");


        kafkaIssuesSendRecording.setUpdateTime(new Date());

        if(null!=kafkaIssuesSendRecording.getId()&&kafkaIssuesSendRecording.getId().intValue()>0){
            addOrUpdateCount = KafkaIssuesSendRecordingMapper.updateById(kafkaIssuesSendRecording);
        }else {
            kafkaIssuesSendRecording.setCreateUserId(kafkaIssuesSendRecording.getUpdateUserId());
            kafkaIssuesSendRecording.setCreateTime(kafkaIssuesSendRecording.getUpdateTime());
            kafkaIssuesSendRecording.setDataType(OverseeConstants.DataType.Enable);
            addOrUpdateCount = KafkaIssuesSendRecordingMapper.insert(kafkaIssuesSendRecording);
        }
        return addOrUpdateCount;
    }
}
