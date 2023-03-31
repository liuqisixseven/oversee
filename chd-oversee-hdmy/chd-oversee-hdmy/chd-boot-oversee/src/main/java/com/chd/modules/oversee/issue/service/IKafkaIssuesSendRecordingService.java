package com.chd.modules.oversee.issue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chd.modules.oversee.issue.entity.KafkaIssuesSendRecording;

/**
 * @Description: recover_funds
 * @Author: jeecg-boot
 * @Date:   2022-10-05
 * @Version: V1.0
 */
public interface IKafkaIssuesSendRecordingService extends IService<KafkaIssuesSendRecording> {

    public int addOrUpdate(KafkaIssuesSendRecording KafkaIssuesSendRecording);


}
