package com.chd.modules.message.service.impl;

import com.chd.common.system.base.service.impl.JeecgServiceImpl;
import com.chd.modules.message.entity.SysMessage;
import com.chd.modules.message.mapper.SysMessageMapper;
import com.chd.modules.message.service.ISysMessageService;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * 
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
